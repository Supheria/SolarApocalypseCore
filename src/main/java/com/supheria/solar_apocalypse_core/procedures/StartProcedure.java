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

import com.supheria.solar_apocalypse_core.network.SapModVariables;

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
		if (SapModVariables.MapVariables.get(world).SolarFlare != 6 && world.dayTime() >= 0 && world.dayTime() < 144000) {
			SapModVariables.MapVariables.get(world).SolarFlare = 1;
			SapModVariables.MapVariables.get(world).syncData(world);
			world.getLevelData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(4, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(true, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_FREEZE_DAMAGE).set(true, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_WATER_SOURCE_CONVERSION).set(true, world.getServer());
		} else if (SapModVariables.MapVariables.get(world).SolarFlare != 6 && world.dayTime() >= 144000 && world.dayTime() < 312000) {
			SapModVariables.MapVariables.get(world).SolarFlare = 2;
			SapModVariables.MapVariables.get(world).syncData(world);
			world.getLevelData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(5, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(true, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_FREEZE_DAMAGE).set(true, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_WATER_SOURCE_CONVERSION).set(true, world.getServer());
		} else if (SapModVariables.MapVariables.get(world).SolarFlare != 6 && world.dayTime() >= 312000 && world.dayTime() < 480000) {
			SapModVariables.MapVariables.get(world).SolarFlare = 3;
			SapModVariables.MapVariables.get(world).syncData(world);
			world.getLevelData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(8, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_FREEZE_DAMAGE).set(false, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_WATER_SOURCE_CONVERSION).set(false, world.getServer());
			if (world instanceof ServerLevel _level)
				_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(0, 0, 0), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(), "weather clear");
		} else if (SapModVariables.MapVariables.get(world).SolarFlare != 6 && world.dayTime() >= 480000 && world.dayTime() < 648000) {
			SapModVariables.MapVariables.get(world).SolarFlare = 4;
			SapModVariables.MapVariables.get(world).syncData(world);
			world.getLevelData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(9, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_FREEZE_DAMAGE).set(false, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_WATER_SOURCE_CONVERSION).set(false, world.getServer());
			if (world instanceof ServerLevel _level)
				_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(0, 0, 0), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(), "weather clear");
		} else if (SapModVariables.MapVariables.get(world).SolarFlare != 6 && world.dayTime() >= 648000) {
			SapModVariables.MapVariables.get(world).SolarFlare = 5;
			SapModVariables.MapVariables.get(world).syncData(world);
			world.getLevelData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(10, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_FREEZE_DAMAGE).set(false, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_WATER_SOURCE_CONVERSION).set(false, world.getServer());
			if (world instanceof ServerLevel _level)
				_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(0, 0, 0), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(), "weather clear");
		}
	}
}
