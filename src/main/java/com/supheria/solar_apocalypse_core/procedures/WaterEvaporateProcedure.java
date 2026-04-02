package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class WaterEvaporateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		int stage = (int) SapModVariables.MapVariables.get(world).SolarFlare;

		// 阶段2-5的水蒸发逻辑（需要检查高度）
		if (stage >= 2 && stage < 6
				&& world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
				&& world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
				&& y > StageHeightConfig.getWaterEvapHeight(stage)
				&& Mth.nextDouble(RandomSource.create(), 0, (world.dayTime() / 24000) + 1) <= (world.dayTime() / 24000)) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
		}
		if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare >= 3 && SapModVariables.MapVariables.get(world).SolarFlare < 4
				&& world.dayTime() >= 384000
				&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
				&& y >= 63) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
		} else if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare == 4
				&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
				&& y >= 8) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			//5x1
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 1, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 1, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			//5x2
			if ((world.getFluidState(BlockPos.containing(x, y - 2, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 2, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 2, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 2, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 2, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 2, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 2, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 2, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 2, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 2, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 2, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 2, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 2, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 2, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 2, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 2, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 2, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 2, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 2, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			//5x3
			if ((world.getFluidState(BlockPos.containing(x, y - 3, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 3, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 3, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 3, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 3, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 3, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 3, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 3, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 3, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 3, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 3, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 3, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 3, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 3, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 3, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 3, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 3, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 3, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 3, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			//5x4
			if ((world.getFluidState(BlockPos.containing(x, y - 4, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 4, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 4, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 4, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 4, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 4, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 4, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 4, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 4, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 4, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 4, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 4, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 4, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 4, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 4, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 4, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 4, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 4, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 4, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			//5x5
		} else if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare == 5
				&& y >= 8) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			//5x1
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 1, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 1, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 1, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 1, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			//5x2
			if ((world.getFluidState(BlockPos.containing(x, y - 2, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 2, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 2, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 2, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 2, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 2, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 2, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 2, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 2, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 2, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 2, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 2, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 2, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 2, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 2, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 2, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 2, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 2, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 2, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 2, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			//5x3
			if ((world.getFluidState(BlockPos.containing(x, y - 3, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 3, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 3, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 3, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 3, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 3, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 3, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 3, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 3, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 3, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 3, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 3, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 3, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 3, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 3, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 3, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 3, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 3, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 3, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 3, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			//5x4
			if ((world.getFluidState(BlockPos.containing(x, y - 4, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 4, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 4, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 4, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 4, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 4, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 4, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 4, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 4, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 4, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 4, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 4, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 4, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 4, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 4, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 4, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 4, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 4, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 4, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 4, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			//5x5
			if ((world.getFluidState(BlockPos.containing(x, y + 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 1, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 1, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 1, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 1, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 1, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 1, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 1, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 1, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 1, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 1, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 1, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 1, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 1, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			//5x6
			if ((world.getFluidState(BlockPos.containing(x, y + 2, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 2, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 2, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 2, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 2, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 2, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 2, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 2, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 2, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 2, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 2, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 2, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 2, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 2, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 2, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 2, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 2, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 2, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 2, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 2, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 2, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 2, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 2, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 2, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 2, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 2, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 2, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 2, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 2, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			//5x7
			if ((world.getFluidState(BlockPos.containing(x, y + 3, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 3, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 3, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 3, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 3, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 3, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 3, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 3, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 3, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 3, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 3, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 3, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 3, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 3, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 3, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 3, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 3, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 3, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 3, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 3, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 3, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 3, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 3, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 3, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 3, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 3, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 3, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 3, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 3, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			//5x8
			if ((world.getFluidState(BlockPos.containing(x, y + 4, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 4, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 4, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 4, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 4, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 4, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 4, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 4, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 4, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 4, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 4, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 4, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 4, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 4, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 4, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 4, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 4, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 4, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 4, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 4, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 4, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 4, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y + 4, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y + 4, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y + 4, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y + 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y + 4, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y + 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y + 4, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y + 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y + 4, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y + 4, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			//5x9
			if ((world.getFluidState(BlockPos.containing(x, y - 5, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 5, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 5, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 5, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 5, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 5, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 5, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 5, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 5, z))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 5, z), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 5, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 5, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 5, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 5, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 5, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 5, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 5, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 5, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 5, z + 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 5, z + 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 5, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 5, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 5, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 5, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 5, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 5, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 5, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 5, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 5, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 5, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 5, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 5, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 5, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 5, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 5, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 5, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 5, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 5, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 5, z - 1))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 5, z - 1), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x, y - 5, z + 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x, y - 5, z + 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 1, y - 5, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 1, y - 5, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x + 2, y - 5, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x + 2, y - 5, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 1, y - 5, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 1, y - 5, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			if ((world.getFluidState(BlockPos.containing(x - 2, y - 5, z - 2))).is(FluidTags.WATER)) {
				world.setBlock(BlockPos.containing(x - 2, y - 5, z - 2), Blocks.AIR.defaultBlockState(), 3);
			}
			//5x10
		}
	}
}
