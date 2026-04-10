package com.supheria.solar_apocalypse_core.transforms.plant;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformAction;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformCondition;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 苔藓方块去苔过程（苔藓石砖/苔藓鹅卵石 → 对应无苔版本）。
 */
public class MossyDecay {

    /** 苔藓方块到对应无苔方块的映射表。 */
    private static final Map<Block, Block> MOSS_MAP = Map.of(
            Blocks.MOSSY_COBBLESTONE,        Blocks.COBBLESTONE,
            Blocks.MOSSY_COBBLESTONE_SLAB,   Blocks.COBBLESTONE_SLAB,
            Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.COBBLESTONE_STAIRS,
            Blocks.MOSSY_COBBLESTONE_WALL,   Blocks.COBBLESTONE_WALL,
            Blocks.MOSSY_STONE_BRICKS,       Blocks.STONE_BRICKS,
            Blocks.MOSSY_STONE_BRICK_SLAB,   Blocks.STONE_BRICK_SLAB,
            Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.STONE_BRICK_STAIRS,
            Blocks.MOSSY_STONE_BRICK_WALL,   Blocks.STONE_BRICK_WALL
    );

    /** 将当前位置的苔藓方块替换为对应无苔版本（不在映射表中则不操作）。 */
    private static final TransformAction REMOVE_MOSS = (world, x, y, z) -> {
        BlockPos pos = BlockPos.containing(x, y, z);
        Block replacement = MOSS_MAP.get(world.getBlockState(pos).getBlock());
        if (replacement != null) {
            world.setBlock(pos, replacement.defaultBlockState(), 3);
        }
    };

    private static final TransformCondition SOFT_BASE = daytime().and(sky()).and(noRain());

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            when(stageIsEruptionPhase().and(SOFT_BASE),
                    REMOVE_MOSS),
            when(stageRange(SolarStage.STAGE_3, SolarStage.STAGE_6).and(aboveSafeHeight()),
                    REMOVE_MOSS)
    );

    private MossyDecay() {}
}
