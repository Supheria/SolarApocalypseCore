package com.supheria.solar_apocalypse_core.procedures.stones;

import com.supheria.solar_apocalypse_core.Procedure;
import com.supheria.solar_apocalypse_core.procedures.transform.TransformRule;
import net.minecraft.world.level.block.Block;

import static com.supheria.solar_apocalypse_core.procedures.transform.TransformActions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.procedures.transform.TransformRule.when;

/**
 * 通用石头系方块转换工厂。
 * 替代以下8个几乎完全相同的类：
 *   StoneTCCobblestoneProcedure, StoneTCCobblestoneSlabProcedure,
 *   StoneTCCobblestoneStairsProcedure, StoneTCCobblestoneWallProcedure,
 *   StoneTCCobbledDeepslateProcedure, StoneTCCobbledDeepslateSlabProcedure,
 *   StoneTCCobbledDeepslateStairsProcedure, StoneTCCobbledDeepslateWallProcedure
 *
 * 使用方式（在 SolarApocalypseCoreMod 中）：
 *   return StoneTCProcedure.of(Blocks.COBBLESTONE);
 */
public class StoneTCProcedure {

    /**
     * 创建将当前方块转换为 {@code target} 的 {@link Procedure}。
     *
     * <ul>
     *   <li>阶段2-5：天空可见或相邻岩浆 + 高于各阶段最小安全高度 + 概率触发 → target</li>
     *   <li>阶段3：高于阶段2安全高度 + 概率触发 → target（无天空条件）</li>
     *   <li>阶段4-5：高于阶段4安全高度 → target（无概率条件）</li>
     * </ul>
     */
    public static Procedure of(Block target) {
        return TransformRule.rulesOf(
                when(stageRange(2, 6).and(sky().or(adjacentLava())).and(aboveMinSafeHeight()).and(randomDayVariable(16000)),
                        setBlock(target)),
                when(stageExact(3).and(aboveSafeHeight(2)).and(randomDayVariable(16000)),
                        setBlock(target)),
                when(stageRange(4, 6).and(aboveSafeHeight(4)),
                        setBlock(target))
        );
    }

    private StoneTCProcedure() {}
}
