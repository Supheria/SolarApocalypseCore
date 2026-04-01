package com.supheria.solar_apocalypse_core.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;

import com.supheria.solar_apocalypse_core.network.SapModVariables;

@Mod.EventBusSubscriber
public class TodayCountProcedure {
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
		SapModVariables.MapVariables.get(world).Today = Math.floor(world.dayTime() / 24000);
		SapModVariables.MapVariables.get(world).LunarToday = Math.floor((world.dayTime() / 24000) + 1/4);
		if (world.dayTime() / 24000 >= 1) {
			SapModVariables.MapVariables.get(world).TodayTime = world.dayTime() - Math.floor(world.dayTime() / 24000) * 24000;
			SapModVariables.MapVariables.get(world).syncData(world);
		} else {
			SapModVariables.MapVariables.get(world).TodayTime = world.dayTime();
			SapModVariables.MapVariables.get(world).syncData(world);
		}
	}
}
