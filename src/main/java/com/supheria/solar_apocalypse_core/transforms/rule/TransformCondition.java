package com.supheria.solar_apocalypse_core.transforms.rule;

import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.world.level.LevelAccessor;

/**
 * 方块转换条件。可通过 {@link #and} / {@link #or} 组合。
 *
 * <p>设计为无状态，可安全地作为静态常量复用。</p>
 *
 * <pre>
 * // 示例：白天 + 阶段3-5 + 天空可见
 * TransformCondition cond = stageRange(3, 6).and(daytime()).and(sky());
 * </pre>
 */
@FunctionalInterface
public interface TransformCondition {

    /**
     * @param world 世界
     * @param x     方块 X 坐标
     * @param y     方块 Y 坐标
     * @param z     方块 Z 坐标
     * @param stage 当前太阳阶段（已从 SolarModVariables 读取）
     */
    boolean test(LevelAccessor world, double x, double y, double z, SolarStage stage);

    /** 逻辑与：当前条件 AND 另一条件都满足时成立。 */
    default TransformCondition and(TransformCondition other) {
        return (world, x, y, z, stage) ->
                this.test(world, x, y, z, stage) && other.test(world, x, y, z, stage);
    }

    /** 逻辑或：当前条件 OR 另一条件满足时成立。 */
    default TransformCondition or(TransformCondition other) {
        return (world, x, y, z, stage) ->
                this.test(world, x, y, z, stage) || other.test(world, x, y, z, stage);
    }

    /** 逻辑非。 */
    default TransformCondition negate() {
        return (world, x, y, z, stage) -> !this.test(world, x, y, z, stage);
    }
}
