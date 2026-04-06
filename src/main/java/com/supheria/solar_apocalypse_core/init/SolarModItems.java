package com.supheria.solar_apocalypse_core.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import com.supheria.solar_apocalypse_core.SolarApocalypseCoreMod;

public class SolarModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, SolarApocalypseCoreMod.MOD_ID);
	public static final RegistryObject<Item> CRUSHED_DIRT = block(SolarModBlocks.CRUSHED_DIRT);
	public static final RegistryObject<Item> DUST = block(SolarModBlocks.DUST);
	public static final RegistryObject<Item> FALLING_SNOW = block(SolarModBlocks.FALLING_SNOW);
	public static final RegistryObject<Item> WITHERED_LEAVES = block(SolarModBlocks.WITHERED_LEAVES);

	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
