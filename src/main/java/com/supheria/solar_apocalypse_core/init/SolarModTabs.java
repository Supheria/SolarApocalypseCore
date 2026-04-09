package com.supheria.solar_apocalypse_core.init;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import com.supheria.solar_apocalypse_core.SolarApocalypseCoreMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SolarModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SolarApocalypseCoreMod.MOD_ID);
	public static final RegistryObject<CreativeModeTab> SOLAR_TAB = REGISTRY.register("solar_tab", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.solar_apocalypse_core"))
			.withTabsBefore(CreativeModeTabs.NATURAL_BLOCKS)
			.icon(() -> SolarModItems.CRUSHED_DIRT.get().getDefaultInstance())
			.displayItems((parameters, output) -> {
				output.accept(SolarModItems.CRUSHED_DIRT.get());
				output.accept(SolarModItems.DUST.get());
				output.accept(SolarModItems.WITHERED_LEAVES.get());
			})
			.build());
}
