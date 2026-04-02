package com.supheria.solar_apocalypse_core.procedures;

import com.supheria.solar_apocalypse_core.Procedure;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformAction;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformCondition;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformRule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

import static com.supheria.solar_apocalypse_core.procedures.transform.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformRule.when;

/**
 * 苔藓方块去苔过程（苔藓石砖/苔藓鹅卵石 → 对应无苔版本）。
 */
public class MossyDeleteProcedure {

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

    private static final Procedure INSTANCE = TransformRule.rulesOf(
            // 阶段1-5：白天 + 天空 + 不下雨 + dayTime>=48000 + 概率触发 → 去苔
            when(stageRange(1, 6).and(SOFT_BASE).and(dayTimeMin(48000)).and(randomDayRate()),
                    REMOVE_MOSS),
            // 阶段2：白天 + y>=63 + 概率触发 → 去苔
            when(stageExact(2).and(daytime()).and(minY(63)).and(randomDayRate()),
                    REMOVE_MOSS),
            // 阶段3：y>=63 → 去苔
            when(stageExact(3).and(minY(63)),
                    REMOVE_MOSS),
            // 阶段4：y>=32 → 去苔
            when(stageExact(4).and(minY(32)),
                    REMOVE_MOSS),
            // 阶段5：y>=8 → 去苔
            when(stageExact(5).and(minY(8)),
                    REMOVE_MOSS)
    );

    public static void execute(LevelAccessor world, double x, double y, double z) {
        INSTANCE.call(world, x, y, z);
    }
}
