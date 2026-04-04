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
            // 阶段1-5：白天 + 露天 + 不下雨 概率触发 → 去苔
            when(stageIsEruptionPhase().and(SOFT_BASE).and(randomDayRate()),
                    REMOVE_MOSS),
            // 阶段2：白天 + 高于安全高度 + 概率触发 → 去苔
            when(stageExact(SolarStage.STAGE_2).and(daytime()).and(aboveSafeHeight()).and(randomDayRate()),
                    REMOVE_MOSS),
            // 阶段3：高于安全高度 → 去苔
            when(stageExact(SolarStage.STAGE_3).and(aboveSafeHeight()),
                    REMOVE_MOSS),
            // 阶段4：高于安全高度 → 去苔
            when(stageExact(SolarStage.STAGE_4).and(aboveSafeHeight()),
                    REMOVE_MOSS),
            // 阶段5：高于安全高度 → 去苔
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()),
                    REMOVE_MOSS),
            // 阶段6：露天 → 去苔
            when(stageExact(SolarStage.STAGE_6).and(sky()),
                    REMOVE_MOSS)
    );

    private MossyDecay() {}
}
