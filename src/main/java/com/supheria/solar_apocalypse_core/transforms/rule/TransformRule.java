package com.supheria.solar_apocalypse_core.transforms.rule;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.world.level.LevelAccessor;

/**
 * 一条转换规则：条件满足时执行动作。
 *
 * <pre>
 * TransformRule rule = TransformRule.when(
 *     stageRange(3, 6).and(sky()).and(randomDaySoft()),
 *     setBlock(Blocks.GRAVEL)
 * );
 * </pre>
 */
public record TransformRule(TransformCondition condition, TransformAction action) {

    /** 构造一条规则。 */
    public static TransformRule when(TransformCondition condition, TransformAction action) {
        return new TransformRule(condition, action);
    }

    /** 若条件成立则执行动作。 */
    public void apply(LevelAccessor world, double x, double y, double z, int stage) {
        if (condition.test(world, x, y, z, stage)) {
            action.execute(world, x, y, z);
        }
    }

    /**
     * 将一组规则组合为一个 {@link BlockTransform}。
     *
     * <p>执行时会：<br>
     * 1. 检查主世界（不满足则直接返回）<br>
     * 2. 读取当前阶段<br>
     * 3. 按顺序尝试每条规则</p>
     */
    public static BlockTransform rulesOf(TransformRule... rules) {
        return (world, x, y, z) -> {
            if (!BlockSpreadUtils.isOverworld(world, x, y, z)) return;
            int stage = SapModVariables.MapVariables.get(world).getSolarStage().ordinal();
            for (TransformRule rule : rules) {
                rule.apply(world, x, y, z, stage);
            }
        };
    }
}
