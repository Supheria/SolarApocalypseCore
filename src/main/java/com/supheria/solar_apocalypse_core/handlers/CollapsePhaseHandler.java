package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.transforms.fluid.SnowMelt;
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
 * 处理永恒降雪和积雪堆积。
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
                    processCollapseSnowfall(world);
                }
            }
        }
    }

    private static void processCollapseSnowfall(LevelAccessor world) {
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

        for (int i = 0; i < SolarStageConfig.getCollapseSnowSampleCount(); i++) {
            var player = serverLevel.players().get(random.nextInt(serverLevel.players().size()));
            int centerX = player.blockPosition().getX();
            int centerZ = player.blockPosition().getZ();
            int x = centerX + random.nextInt(SolarStageConfig.getCollapseSnowSampleDiameter()) - SolarStageConfig.getCollapseSnowSampleOffset();
            int z = centerZ + random.nextInt(SolarStageConfig.getCollapseSnowSampleDiameter()) - SolarStageConfig.getCollapseSnowSampleOffset();
            int surfaceY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1;

            if (surfaceY < serverLevel.getMinBuildHeight()) {
                continue;
            }

            BlockPos pos = new BlockPos(x, surfaceY, z);
            SnowMelt.TRANSFORM.call(serverLevel, pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
