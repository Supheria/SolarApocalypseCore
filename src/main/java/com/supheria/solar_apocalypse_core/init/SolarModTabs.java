package com.supheria.solar_apocalypse_core.init;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.registries.Registries;

import com.supheria.solar_apocalypse_core.SolarApocalypseCoreMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SolarModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SolarApocalypseCoreMod.MOD_ID);

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
			tabData.accept(SolarModBlocks.HIGH_DENSITY_METAL.get().asItem());
			tabData.accept(SolarModBlocks.REDSTONE_COATED_METAL.get().asItem());
			tabData.accept(SolarModBlocks.HEAT_RESISTANT_HIGH_STRENGTH_METAL.get().asItem());
		}

		if (tabData.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			tabData.accept(SolarModBlocks.MEASURING_INSTRUMENT.get().asItem());
		}

		if (tabData.getTabKey() == CreativeModeTabs.INGREDIENTS) {
			tabData.accept(SolarModItems.COORDINATES_OF_THE_SUN.get());
		}

		if (tabData.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
			tabData.accept(SolarModBlocks.CRUSHED_DIRT.get().asItem());
			tabData.accept(SolarModBlocks.DUST.get().asItem());
			tabData.accept(SolarModBlocks.WITHERED_LEAVES.get().asItem());
		}
		if (tabData.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			tabData.accept(SolarModItems.UV_UMBRELLA.get());
		}
	}
}
