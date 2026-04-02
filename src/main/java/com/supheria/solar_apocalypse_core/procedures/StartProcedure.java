package com.supheria.solar_apocalypse_core.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.GameRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import javax.annotation.Nullable;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarPhase;
import com.supheria.solar_apocalypse_core.world.SolarPhaseHelper;

import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.biome.Biomes;

@Mod.EventBusSubscriber
public class StartProcedure {
	@SubscribeEvent
	public static void onWorldTick(TickEvent.LevelTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.level);
		}
	}

	public static void execute(LevelAccessor world) {
		execute(null, world);
	}

	private static void execute(@Nullable Event event, LevelAccessor world) {
		long dayTime = world.dayTime();
		SapModVariables.MapVariables mapVars = SapModVariables.MapVariables.get(world);

		// 获取当前应该处于的阶段
		SolarPhase targetPhase = SolarPhaseHelper.getPhaseByDayTime(dayTime);
		SolarPhase currentPhase = mapVars.getCurrentPhase();

		// 如果阶段变化，更新状态并调整游戏规则
		if (currentPhase != targetPhase) {
			mapVars.setPhase(targetPhase);
			mapVars.syncData(world);
			applyPhaseRules(world, targetPhase);
		}
	}

	/**
	 * 根据阶段应用相应的游戏规则
	 */
	private static void applyPhaseRules(LevelAccessor world, SolarPhase phase) {
		int randomTickingLevel = SolarStageConfig.getRandomTickingLevel(phase.ordinal());
		boolean allowWeather = phase.ordinal() <= 2; // 仅阶段1-2允许天气
		boolean allowFreeze = phase.ordinal() <= 2;   // 仅阶段1-2允许冻伤

		world.getLevelData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(randomTickingLevel, world.getServer());
		world.getLevelData().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(allowWeather, world.getServer());
		world.getLevelData().getGameRules().getRule(GameRules.RULE_FREEZE_DAMAGE).set(allowFreeze, world.getServer());
		world.getLevelData().getGameRules().getRule(GameRules.RULE_WATER_SOURCE_CONVERSION).set(allowWeather, world.getServer());

		// 从阶段3开始，清晰天气
		if (phase.ordinal() >= 3 && phase != SolarPhase.COLLAPSE && world instanceof ServerLevel _level) {
			_level.getServer().getCommands().performPrefixedCommand(
					new CommandSourceStack(CommandSource.NULL, new Vec3(0, 0, 0), Vec2.ZERO, _level, 4, "",
					Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
					"weather clear");
		}

		// 第六阶段（坍缺）的特殊处理
		if (phase == SolarPhase.COLLAPSE) {
			// 强制持续降雨
			world.getLevelData().setRaining(true);

			// 修改所有已加载的chunks的生物群系为寒冷生物群系
			if (world instanceof ServerLevel serverLevel) {
				modifyLoadedChunksBiomes(serverLevel);
			}
		}
	}

	/**
	 * 修改所有已加载的chunks的生物群系
	 * 在COLLAPSE阶段，使所有地方都显示为寒冷生物群系（下雪）
	 */
	private static void modifyLoadedChunksBiomes(ServerLevel serverLevel) {
		// 注：实际的chunk生物群系修改通过mixin实现
		// 这里只需设置标志，mixin会在后续处理生物群系查询时使用此标志
	}
}
