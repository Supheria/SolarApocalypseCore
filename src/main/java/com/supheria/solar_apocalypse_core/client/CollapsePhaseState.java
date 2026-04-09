package com.supheria.solar_apocalypse_core.client;

import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

/**
 * 客户端COLLAPSE阶段状态追踪
 * 用于在mixin中判断是否在COLLAPSE阶段
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "solar_apocalypse_core", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CollapsePhaseState {
	private static boolean isCollapsePhase = false;
	private static boolean isDaytime = false;

	/**
	 * 每帧更新COLLAPSE阶段状态
	 */
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft.level != null) {
				SolarStage currentPhase = SolarModVariables.MapVariables.get(minecraft.level).getSolarStage();
				isCollapsePhase = (currentPhase == SolarStage.STAGE_6);

				isDaytime = SolarStageHelper.isDaytime(minecraft.level.dayTime());
			} else {
				isCollapsePhase = false;
				isDaytime = false;
			}
		}
	}

	public static boolean isInCollapse() {
		return isCollapsePhase;
	}

	public static boolean isInCollapseOverworld() {
		Minecraft minecraft = Minecraft.getInstance();
		return isCollapsePhase && minecraft.level != null && minecraft.level.dimension() == Level.OVERWORLD;
	}

	public static boolean isDaytimeInCollapse() {
		return isCollapsePhase && isDaytime;
	}
}
