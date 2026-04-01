package com.supheria.solar_apocalypse_core.init;

import com.supheria.solar_apocalypse_core.item.CoordinatesOfTheSunItem;
import com.supheria.solar_apocalypse_core.item.UvUmbrellaItem;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import com.supheria.solar_apocalypse_core.SolarApocalypseCoreMod;

import static net.minecraft.world.item.Items.registerBlock;

public class SapModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, SolarApocalypseCoreMod.MOD_ID);
	public static final RegistryObject<Item> CRUSHED_DIRT = block(SapModBlocks.CRUSHED_DIRT);
	public static final RegistryObject<Item> DUST = block(SapModBlocks.DUST);
	public static final RegistryObject<Item> WITHERED_LEAVES = block(SapModBlocks.WITHERED_LEAVES);
	public static final RegistryObject<Item> HIGH_DENSITY_METAL = block(SapModBlocks.HIGH_DENSITY_METAL);
	public static final RegistryObject<Item> REDSTONE_COATED_METAL = block(SapModBlocks.REDSTONE_COATED_METAL);
	//public static final Item REDSTONE_COATED_METAL = registerBlock(new BlockItem(SapModBlocks.REDSTONE_COATED_METAL.get(), (new Item.Properties()).fireResistant()));
	public static final RegistryObject<Item> HEAT_RESISTANT_HIGH_STRENGTH_METAL = block(SapModBlocks.HEAT_RESISTANT_HIGH_STRENGTH_METAL);
	//public static final Item HEAT_RESISTANT_HIGH_STRENGTH_METAL = registerBlock(new BlockItem(SapModBlocks.HEAT_RESISTANT_HIGH_STRENGTH_METAL.get(), new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> MEASURING_INSTRUMENT = block(SapModBlocks.MEASURING_INSTRUMENT);

	public static final RegistryObject<Item> UV_UMBRELLA = REGISTRY.register("uv_umbrella", () -> new UvUmbrellaItem());
	public static final RegistryObject<Item> COORDINATES_OF_THE_SUN = REGISTRY.register("coordinates_of_the_sun", () -> new CoordinatesOfTheSunItem());

	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
