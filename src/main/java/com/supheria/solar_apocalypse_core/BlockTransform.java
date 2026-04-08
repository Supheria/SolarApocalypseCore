package com.supheria.solar_apocalypse_core;

import net.minecraft.world.level.LevelAccessor;

/**
 * 太阳环境调度器或放置修正逻辑执行的方块转换过程。
 *
 * <p>通过 {@link SolarApocalypseCoreMod#getBlockTransform(net.minecraft.world.level.block.state.BlockState)}
 * 注册到各方块状态，并由环境调度器或 {@code BlockStateBaseMixin} 的 onPlace 修正调用。</p>
 */
@FunctionalInterface
public interface BlockTransform {
    void call(LevelAccessor world, double x, double y, double z);
}
