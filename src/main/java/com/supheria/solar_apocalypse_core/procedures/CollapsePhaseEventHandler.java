package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarPhase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

/**
 * 第六阶段的特殊事件处理器
 * 处理永恒降雪和积雪堆积
 */
@Mod.EventBusSubscriber
public class CollapsePhaseEventHandler {
	private static int tickCounter = 0;

	@SubscribeEvent
	public static void onWorldTick(TickEvent.LevelTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			LevelAccessor world = event.level;
			SolarPhase currentPhase = SapModVariables.MapVariables.get(world).getCurrentPhase();

			// 仅在第六阶段执行
			if (currentPhase == SolarPhase.COLLAPSE) {
				// 获取配置的积雪积累周期
				int accumulationRate = SolarStageConfig.SOLAR_STAGE_VALUES.collapseSnowAccumulationRate.get();
				tickCounter++;

				// 每个周期执行一次
				if (tickCounter >= accumulationRate) {
					tickCounter = 0;
					processCollapseSnowfall(world);
				}
			}
		}
	}

	/**
	 * 处理第六阶段的积雪堆积
	 * 在加载的chunks中随机选择几个位置来堆积雪
	 * 同时强制设置天气为下雪状态
	 */
	private static void processCollapseSnowfall(LevelAccessor world) {
		// 强制设置为下雪状态
		// 在COLLAPSE阶段，所有降雨都应该表现为降雪
		if (world instanceof net.minecraft.server.level.ServerLevel serverLevel) {
			// 确保降雨持续
			if (!serverLevel.isRaining()) {
				serverLevel.getLevelData().setRaining(true);
			}
		}

		// 处理积雪堆积
		RandomSource random = RandomSource.create();

		for (int i = 0; i < 10; i++) {
			int x = random.nextInt(32) - 16;
			int z = random.nextInt(32) - 16;

			for (int y = world.getHeight() - 1; y >= world.getMinBuildHeight(); y--) {
				BlockPos pos = BlockPos.containing(x, y, z);
				EternalSnowProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
				break;
			}
		}
	}
}
