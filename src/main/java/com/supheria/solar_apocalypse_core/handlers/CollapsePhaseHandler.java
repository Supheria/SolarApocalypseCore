package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.transforms.fluid.SnowMelt;
import com.supheria.solar_apocalypse_core.world.SolarPhase;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
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
            SolarPhase currentPhase = SapModVariables.MapVariables.get(world).getCurrentPhase();

            if (currentPhase == SolarPhase.COLLAPSE) {
                int accumulationRate = SolarStageConfig.SOLAR_STAGE_VALUES.collapseSnowAccumulationRate.get();
                tickCounter++;

                if (tickCounter >= accumulationRate) {
                    tickCounter = 0;
                    processCollapseSnowfall(world);
                }
            }
        }
    }

    private static void processCollapseSnowfall(LevelAccessor world) {
        if (world instanceof ServerLevel serverLevel) {
            if (!serverLevel.isRaining()) {
                serverLevel.getLevelData().setRaining(true);
            }
        }

        RandomSource random = RandomSource.create();

        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(32) - 16;
            int z = random.nextInt(32) - 16;

            for (int y = world.getHeight() - 1; y >= world.getMinBuildHeight(); y--) {
                BlockPos pos = BlockPos.containing(x, y, z);
                SnowMelt.TRANSFORM.call(world, pos.getX(), pos.getY(), pos.getZ());
                break;
            }
        }
    }
}
