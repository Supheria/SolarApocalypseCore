package com.supheria.solar_apocalypse_core;

import net.minecraft.world.level.LevelAccessor;

/**
 * 方块随机 tick 或放置时执行的转换过程。
 *
 * <p>通过 {@link SolarApocalypseCoreMod#getProcedure(net.minecraft.world.level.block.state.BlockState)}
 * 注册到各方块状态，并由 {@code BlockStateBaseMixin} 在 randomTick / onPlace 时调用。</p>
 */
@FunctionalInterface
public interface Procedure {
    void call(LevelAccessor world, double x, double y, double z);
}
