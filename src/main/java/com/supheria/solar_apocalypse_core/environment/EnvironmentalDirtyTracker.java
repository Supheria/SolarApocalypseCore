package com.supheria.solar_apocalypse_core.environment;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.integration.minecollapse.MineCollapseBridge;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class EnvironmentalDirtyTracker {
    private static final Map<ResourceKey<Level>, EnumMap<EnvironmentalWorkType, LinkedHashMap<Long, BlockState>>> QUEUES = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, Map<Long, Integer>> FIRE_QUEUE_CHUNK_COUNTS = new ConcurrentHashMap<>();
    private static final int MAX_FIRE_QUEUE_PER_CHUNK = 8;
    private static final int MAX_FIRE_WRITES_PER_CHUNK_PER_TICK = 2;

    public static void queueBlockChange(net.minecraft.world.level.LevelAccessor world, BlockPos pos, BlockState target, EnvironmentalWorkType workType) {
        if (!(world instanceof ServerLevel serverLevel)) {
            MineCollapseBridge.setBlockWithCurrentSource(world, pos, target);
            return;
        }

        EnumMap<EnvironmentalWorkType, LinkedHashMap<Long, BlockState>> byType = QUEUES.computeIfAbsent(serverLevel.dimension(), ignored -> new EnumMap<>(EnvironmentalWorkType.class));
        LinkedHashMap<Long, BlockState> queue = byType.computeIfAbsent(workType, ignored -> new LinkedHashMap<>());
        long posKey = pos.asLong();
        if (workType == EnvironmentalWorkType.FIRE && target.is(Blocks.FIRE)) {
            long chunkKey = chunkKey(pos);
            Map<Long, Integer> fireCounts = FIRE_QUEUE_CHUNK_COUNTS.computeIfAbsent(serverLevel.dimension(), ignored -> new ConcurrentHashMap<>());
            if (!queue.containsKey(posKey) && fireCounts.getOrDefault(chunkKey, 0) >= MAX_FIRE_QUEUE_PER_CHUNK) {
                return;
            }
            if (!queue.containsKey(posKey)) {
                fireCounts.merge(chunkKey, 1, Integer::sum);
            }
        }
        queue.put(posKey, target);
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) {
            return;
        }

        EnumMap<EnvironmentalWorkType, LinkedHashMap<Long, BlockState>> byType = QUEUES.get(level.dimension());
        if (byType == null || byType.isEmpty()) {
            return;
        }

        SolarStage stage = SolarModVariables.MapVariables.get(level).getSolarStage();
        Map<Long, Integer> fireWritesThisTick = new ConcurrentHashMap<>();
        for (EnvironmentalWorkType workType : EnvironmentalWorkType.values()) {
            LinkedHashMap<Long, BlockState> queue = byType.get(workType);
            if (queue == null || queue.isEmpty()) {
                continue;
            }

            int remaining = budgetFor(stage, workType);
            List<Map.Entry<Long, BlockState>> batch = new ArrayList<>(Math.min(remaining, queue.size()));
            Iterator<Map.Entry<Long, BlockState>> iterator = queue.entrySet().iterator();
            while (iterator.hasNext() && remaining > 0) {
                Map.Entry<Long, BlockState> entry = iterator.next();
                if (workType == EnvironmentalWorkType.FIRE && entry.getValue().is(Blocks.FIRE)) {
                    long chunkKey = chunkKey(BlockPos.of(entry.getKey()));
                    int written = fireWritesThisTick.getOrDefault(chunkKey, 0);
                    if (written >= MAX_FIRE_WRITES_PER_CHUNK_PER_TICK) {
                        continue;
                    }
                    fireWritesThisTick.put(chunkKey, written + 1);
                    decrementQueuedFire(level.dimension(), chunkKey);
                }
                batch.add(Map.entry(entry.getKey(), entry.getValue()));
                iterator.remove();
                remaining--;
            }

            for (Map.Entry<Long, BlockState> entry : batch) {
                BlockPos pos = BlockPos.of(entry.getKey());
                BlockState target = entry.getValue();
                MineCollapseBridge.withSolarSource(MineCollapseBridge.SOURCE_SOLAR_SPREAD,
                        () -> MineCollapseBridge.setBlockWithCurrentSource(level, pos, target));
            }
        }
    }

    private static int budgetFor(SolarStage stage, EnvironmentalWorkType workType) {
        return switch (workType) {
            case WATER -> SolarStageConfig.getWaterSpreadBudget(stage) * 4;
            case ICE -> SolarStageConfig.getStageSpreadBudget(stage) * 4;
            case FIRE -> Math.max(1, SolarStageConfig.getStageSpreadBudget(stage));
            case STAGE6_SURFACE -> Math.max(4, SolarStageConfig.getCollapseSnowStepBudget() * SolarStageConfig.getCollapseSnowSampleCount());
            case STONE -> SolarStageConfig.getStageSpreadBudget(stage) * 3;
            case SURFACE -> SolarStageConfig.getStageSpreadBudget(stage) * 4;
        };
    }

    private static void decrementQueuedFire(ResourceKey<Level> dimension, long chunkKey) {
        Map<Long, Integer> fireCounts = FIRE_QUEUE_CHUNK_COUNTS.get(dimension);
        if (fireCounts == null) {
            return;
        }
        fireCounts.computeIfPresent(chunkKey, (ignored, count) -> count > 1 ? count - 1 : null);
    }

    private static long chunkKey(BlockPos pos) {
        return (((long) pos.getX() >> 4) << 32) ^ (((long) pos.getZ() >> 4) & 0xffffffffL);
    }

    private EnvironmentalDirtyTracker() {}
}
