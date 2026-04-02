package com.supheria.solar_apocalypse_core.procedures.transform;

import net.minecraft.world.level.LevelAccessor;

/**
 * 条件满足时执行的方块转换动作。
 *
 * <p>通过 {@link TransformActions} 中的静态工厂创建常用动作，
 * 也可直接使用 lambda 表达式描述自定义行为。</p>
 */
@FunctionalInterface
public interface TransformAction {
    void execute(LevelAccessor world, double x, double y, double z);
}
