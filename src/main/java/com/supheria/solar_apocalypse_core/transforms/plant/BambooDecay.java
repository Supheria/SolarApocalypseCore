package com.supheria.solar_apocalypse_core.transforms.plant;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformCondition;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.setBlock;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 竹子仅从顶部开始点燃/消失，避免中段先转换造成大量掉落物。
 */
public class BambooDecay {

    private static final TransformCondition BAMBOO_TOP = (world, x, y, z, stage) -> {
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BambooSaplingBlock) {
            return true;
        }
        if (!(state.getBlock() instanceof BambooStalkBlock)) {
            return false;
        }

        BlockState aboveState = world.getBlockState(pos.above());
        return !(aboveState.getBlock() instanceof BambooStalkBlock);
    };

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            when(BAMBOO_TOP.and(stageExact(SolarStage.STAGE_2)).and(sky()).and(noRain()).and(randomDayRate()),
                    setBlock(Blocks.FIRE)),
            when(BAMBOO_TOP.and(stageExact(SolarStage.STAGE_3)).and(aboveSafeHeight()),
                    setBlock(Blocks.FIRE)),
            when(BAMBOO_TOP.and(stageRange(SolarStage.STAGE_4, SolarStage.STAGE_6)).and(aboveSafeHeight()),
                    setBlock(Blocks.FIRE))
    );

    private BambooDecay() {}
}
