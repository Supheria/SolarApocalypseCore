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
public class SapModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SolarApocalypseCoreMod.MOD_ID);

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
			tabData.accept(SapModBlocks.HIGH_DENSITY_METAL.get().asItem());
			tabData.accept(SapModBlocks.REDSTONE_COATED_METAL.get().asItem());
			tabData.accept(SapModBlocks.HEAT_RESISTANT_HIGH_STRENGTH_METAL.get().asItem());
		}

		if (tabData.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			tabData.accept(SapModBlocks.MEASURING_INSTRUMENT.get().asItem());
		}

		if (tabData.getTabKey() == CreativeModeTabs.INGREDIENTS) {
			tabData.accept(SapModItems.COORDINATES_OF_THE_SUN.get());
		}

		if (tabData.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
			tabData.accept(SapModBlocks.CRUSHED_DIRT.get().asItem());
			tabData.accept(SapModBlocks.DUST.get().asItem());
			tabData.accept(SapModBlocks.WITHERED_LEAVES.get().asItem());
		}
		if (tabData.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			tabData.accept(SapModItems.UV_UMBRELLA.get());
		}
	}
}
