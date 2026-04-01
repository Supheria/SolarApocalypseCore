package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class EndFrameTagDeleteProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& SapModVariables.MapVariables.get(world).SolarFlare < 6
				&& ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock().getStateDefinition().getProperty("eye") instanceof BooleanProperty _getbp2 && (world.getBlockState(BlockPos.containing(x, y, z))).getValue(_getbp2)) == true) {
			if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock().getStateDefinition().getProperty("eye") instanceof BooleanProperty _booleanProp)
				world.setBlock(BlockPos.containing(x, y, z), world.getBlockState(BlockPos.containing(x, y, z)).setValue(_booleanProp, false), 3);
		}
	}
}
