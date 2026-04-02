package com.supheria.solar_apocalypse_core.client;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarPhase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.client.Minecraft;

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
				SolarPhase currentPhase = SapModVariables.MapVariables.get(minecraft.level).getCurrentPhase();
				isCollapsePhase = (currentPhase == SolarPhase.COLLAPSE);

				// 检查是否为白天
				long dayTime = minecraft.level.dayTime();
				long timeOfDay = dayTime % 24000;
				isDaytime = timeOfDay < 12000;
			} else {
				isCollapsePhase = false;
				isDaytime = false;
			}
		}
	}

	public static boolean isInCollapse() {
		return isCollapsePhase;
	}

	public static boolean isDaytimeInCollapse() {
		return isCollapsePhase && isDaytime;
	}
}
