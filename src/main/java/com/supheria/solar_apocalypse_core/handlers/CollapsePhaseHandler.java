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

    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            LevelAccessor world = event.level;
            SolarStage currentPhase = SolarModVariables.MapVariables.get(world).getCurrentStage();

            if (currentPhase == SolarStage.STAGE_6) {
                int accumulationRate = SolarStageConfig.getCollapseSnowAccumulationRate();
                tickCounter++;

                if (tickCounter >= accumulationRate) {
                    tickCounter = 0;
                    processCollapseEnvironment(world);
                }
            }
        }
    }

    private static void processCollapseEnvironment(LevelAccessor world) {
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
        processCollapseSnowfall(serverLevel, random);
        processCollapseWaterFreeze(serverLevel, random);
    }

    private static void processCollapseSnowfall(ServerLevel serverLevel, RandomSource random) {
        for (int i = 0; i < SolarStageConfig.getCollapseSnowSampleCount(); i++) {
            BlockPos pos = sampleSurfacePosition(serverLevel, random);
            if (pos == null) {
                continue;
            }
            SnowMelt.TRANSFORM.call(serverLevel, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    private static void processCollapseWaterFreeze(ServerLevel serverLevel, RandomSource random) {
        for (int i = 0; i < SolarStageConfig.getCollapseSnowSampleCount(); i++) {
            BlockPos pos = sampleSurfacePosition(serverLevel, random);
            if (pos == null) {
                continue;
            }

            BlockPos waterPos = findNearbySurfaceWater(serverLevel, pos);
            if (waterPos != null) {
                BlockSpreadUtils.freezeSurfaceWater(serverLevel, waterPos, BlockSpreadUtils.OFFSETS_SURFACE_WATER_5X5);
            }
        }
    }

    private static BlockPos sampleSurfacePosition(ServerLevel serverLevel, RandomSource random) {
        var player = serverLevel.players().get(random.nextInt(serverLevel.players().size()));
        int centerX = player.blockPosition().getX();
        int centerZ = player.blockPosition().getZ();
        int x = centerX + random.nextInt(SolarStageConfig.getCollapseSnowSampleDiameter()) - SolarStageConfig.getCollapseSnowSampleOffset();
        int z = centerZ + random.nextInt(SolarStageConfig.getCollapseSnowSampleDiameter()) - SolarStageConfig.getCollapseSnowSampleOffset();

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
        for (int dy = 1; dy >= -3; dy--) {
            BlockPos candidate = origin.above(dy);
            if (BlockSpreadUtils.isSurfaceWater(serverLevel, candidate)) {
                return candidate;
            }
        }

        for (int[] offset : BlockSpreadUtils.OFFSETS_8H) {
            BlockPos candidate = origin.offset(offset[0], 0, offset[2]);
            for (int dy = 1; dy >= -3; dy--) {
                BlockPos adjusted = candidate.above(dy);
                if (BlockSpreadUtils.isSurfaceWater(serverLevel, adjusted)) {
                    return adjusted;
                }
            }
        }

        return null;
    }
}
