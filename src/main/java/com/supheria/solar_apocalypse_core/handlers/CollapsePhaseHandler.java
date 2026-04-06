package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.transforms.fluid.SnowMelt;
import com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 第六阶段特殊事件处理器（原 CollapsePhaseEventHandler）。
 * 处理永恒降雪、积雪堆积和地表水面冻结。
 */
@Mod.EventBusSubscriber
public class CollapsePhaseHandler {

    private static final int SURFACE_WATER_SCAN_TOP = 1;
    private static final int SURFACE_WATER_SCAN_BOTTOM = -3;
    private static int snowTickCounter = 0;
    private static int waterTickCounter = 0;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        LevelAccessor world = event.level;
        SolarStage currentPhase = SolarModVariables.MapVariables.get(world).getSolarStage();
        if (currentPhase != SolarStage.STAGE_6) {
            snowTickCounter = 0;
            waterTickCounter = 0;
            return;
        }

        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!serverLevel.isRaining()) {
            serverLevel.getLevelData().setRaining(true);
        }
        if (serverLevel.players().isEmpty()) {
            return;
        }

        RandomSource random = RandomSource.create();
        tickSnow(serverLevel, random);
        tickWaterFreeze(serverLevel, random);
    }

    private static void tickSnow(ServerLevel serverLevel, RandomSource random) {
        snowTickCounter++;
        int rate = Math.max(1, SolarStageConfig.getCollapseSnowAccumulationRate());
        if (snowTickCounter < rate) {
            return;
        }
        snowTickCounter = 0;
        processCollapseSnowfall(serverLevel, random);
    }

    private static void tickWaterFreeze(ServerLevel serverLevel, RandomSource random) {
        waterTickCounter++;
        int rate = Math.max(1, SolarStageConfig.getCollapseWaterFreezeRate());
        if (waterTickCounter < rate) {
            return;
        }
        waterTickCounter = 0;
        processCollapseWaterFreeze(serverLevel, random);
    }

    private static void processCollapseSnowfall(ServerLevel serverLevel, RandomSource random) {
        int sampleCount = Math.max(1, SolarStageConfig.getCollapseSnowSampleCount());
        int stepBudget = Math.max(1, SolarStageConfig.getCollapseSnowStepBudget());
        for (int i = 0; i < sampleCount; i++) {
            BlockPos pos = sampleSurfacePosition(serverLevel, random);
            if (pos == null) {
                continue;
            }
            SnowMelt.processBudgeted(serverLevel, pos, stepBudget);
        }
    }

    private static void processCollapseWaterFreeze(ServerLevel serverLevel, RandomSource random) {
        int sampleCount = Math.max(1, SolarStageConfig.getCollapseWaterSampleCount());
        int maxBudget = BlockSpreadUtils.OFFSETS_SURFACE_WATER_5X5.length + 1;
        for (int i = 0; i < sampleCount; i++) {
            BlockPos pos = sampleSurfacePosition(serverLevel, random);
            if (pos == null) {
                continue;
            }

            BlockPos waterPos = findNearbySurfaceWater(serverLevel, pos);
            if (waterPos == null) {
                continue;
            }

            int matched = BlockSpreadUtils.countSurfaceWater(serverLevel, waterPos,
                    BlockSpreadUtils.OFFSETS_SURFACE_WATER_5X5, Math.min(maxBudget, 3));
            int budget = Math.max(1, Math.min(maxBudget,
                    SolarStageConfig.getStageSpreadBudget(SolarStage.STAGE_6) + matched - 1));
            BlockSpreadUtils.freezeSurfaceWaterLimited(serverLevel, waterPos,
                    BlockSpreadUtils.OFFSETS_SURFACE_WATER_5X5, budget);
        }
    }

    private static BlockPos sampleSurfacePosition(ServerLevel serverLevel, RandomSource random) {
        var player = serverLevel.players().get(random.nextInt(serverLevel.players().size()));
        int centerX = player.blockPosition().getX();
        int centerZ = player.blockPosition().getZ();
        int x = centerX + random.nextInt(SolarStageConfig.getCollapseSnowSampleDiameter())
                - SolarStageConfig.getCollapseSnowSampleOffset();
        int z = centerZ + random.nextInt(SolarStageConfig.getCollapseSnowSampleDiameter())
                - SolarStageConfig.getCollapseSnowSampleOffset();

        if (!serverLevel.hasChunkAt(new BlockPos(x, player.blockPosition().getY(), z))) {
            return null;
        }

        int surfaceY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1;
        if (surfaceY < serverLevel.getMinBuildHeight()) {
            return null;
        }
        return new BlockPos(x, surfaceY, z);
    }

    private static BlockPos findNearbySurfaceWater(ServerLevel serverLevel, BlockPos origin) {
        for (int dy = SURFACE_WATER_SCAN_TOP; dy >= SURFACE_WATER_SCAN_BOTTOM; dy--) {
            BlockPos candidate = origin.above(dy);
            if (BlockSpreadUtils.isSurfaceWater(serverLevel, candidate)) {
                return candidate;
            }
        }

        for (int[] offset : BlockSpreadUtils.OFFSETS_8H) {
            BlockPos candidate = origin.offset(offset[0], 0, offset[2]);
            for (int dy = SURFACE_WATER_SCAN_TOP; dy >= SURFACE_WATER_SCAN_BOTTOM; dy--) {
                BlockPos adjusted = candidate.above(dy);
                if (BlockSpreadUtils.isSurfaceWater(serverLevel, adjusted)) {
                    return adjusted;
                }
            }
        }

        return null;
    }
}
