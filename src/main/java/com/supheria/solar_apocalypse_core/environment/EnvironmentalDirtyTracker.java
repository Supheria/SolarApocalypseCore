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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class EnvironmentalDirtyTracker {
    private static final Map<ResourceKey<Level>, EnumMap<EnvironmentalWorkType, LinkedHashMap<Long, BlockState>>> QUEUES = new ConcurrentHashMap<>();

    public static void queueBlockChange(net.minecraft.world.level.LevelAccessor world, BlockPos pos, BlockState target, EnvironmentalWorkType workType) {
        if (!(world instanceof ServerLevel serverLevel)) {
            MineCollapseBridge.setBlockWithCurrentSource(world, pos, target);
            return;
        }

        EnumMap<EnvironmentalWorkType, LinkedHashMap<Long, BlockState>> byType = QUEUES.computeIfAbsent(serverLevel.dimension(), ignored -> new EnumMap<>(EnvironmentalWorkType.class));
        byType.computeIfAbsent(workType, ignored -> new LinkedHashMap<>()).put(pos.asLong(), target);
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
            case FIRE -> Math.max(2, SolarStageConfig.getStageSpreadBudget(stage) * 2);
            case STAGE6_SURFACE -> Math.max(4, SolarStageConfig.getCollapseSnowStepBudget() * SolarStageConfig.getCollapseSnowSampleCount());
            case STONE -> SolarStageConfig.getStageSpreadBudget(stage) * 3;
            case SURFACE -> SolarStageConfig.getStageSpreadBudget(stage) * 4;
        };
    }

    private EnvironmentalDirtyTracker() {}
}
