package com.supheria.solar_apocalypse_core.init;

import com.supheria.solar_apocalypse_core.SolarApocalypseCoreMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class SolarModTags {
    public static class Blocks {
        public static final TagKey<Block> SIMPLE_DELETE = BlockTags.create(new ResourceLocation(SolarApocalypseCoreMod.MOD_ID, "simple_delete"));
        public static final TagKey<Block> FIRE_RESISTANCE = BlockTags.create(new ResourceLocation(SolarApocalypseCoreMod.MOD_ID, "fire_resistance"));
        public static final TagKey<Block> MOIST_DIRT = BlockTags.create(new ResourceLocation(SolarApocalypseCoreMod.MOD_ID, "moist_dirt"));
        public static final TagKey<Block> DIRT = BlockTags.create(new ResourceLocation(SolarApocalypseCoreMod.MOD_ID, "dirt"));
        public static final TagKey<Block> HARD_DIRT = BlockTags.create(new ResourceLocation(SolarApocalypseCoreMod.MOD_ID, "hard_dirt"));
        public static final TagKey<Block> POWDER = BlockTags.create(new ResourceLocation(SolarApocalypseCoreMod.MOD_ID, "powder"));
        public static final TagKey<Block> MOSSY = BlockTags.create(new ResourceLocation(SolarApocalypseCoreMod.MOD_ID, "mossy"));
        public static final TagKey<Block> TNT = BlockTags.create(new ResourceLocation(SolarApocalypseCoreMod.MOD_ID, "tnt"));
        public static final TagKey<Block> DEEPSLATE = BlockTags.create(new ResourceLocation(SolarApocalypseCoreMod.MOD_ID, "deepslate"));
        public static final TagKey<Block> COBBLESTONE = BlockTags.create(new ResourceLocation(SolarApocalypseCoreMod.MOD_ID, "cobblestone"));
        public static final TagKey<Block> SANDSTONE = BlockTags.create(new ResourceLocation(SolarApocalypseCoreMod.MOD_ID, "sandstone"));
        public static final TagKey<Block> CLAY = BlockTags.create(new ResourceLocation(SolarApocalypseCoreMod.MOD_ID, "clay"));
    }
}
