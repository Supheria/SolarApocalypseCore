package com.supheria.solar_apocalypse_core.init;

import com.supheria.solar_apocalypse_core.block.*;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import com.supheria.solar_apocalypse_core.SolarApocalypseCoreMod;

public class SolarModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, SolarApocalypseCoreMod.MOD_ID);
	public static final RegistryObject<Block> CRUSHED_DIRT = REGISTRY.register("crushed_dirt", () -> new CrushedDirtBlock());
	public static final RegistryObject<Block> DUST = REGISTRY.register("dust", () -> new DustBlock());
	public static final RegistryObject<Block> WITHERED_LEAVES = REGISTRY.register("withered_leaves", () -> new WitheredLeavesBlock());
	public static final RegistryObject<Block> EVAPORATED_VOID = REGISTRY.register("evaporated_void", () -> new EvaporatedVoidBlock());
}
