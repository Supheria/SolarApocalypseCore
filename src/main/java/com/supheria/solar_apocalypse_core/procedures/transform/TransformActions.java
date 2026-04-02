package com.supheria.solar_apocalypse_core.procedures.transform;

import com.supheria.solar_apocalypse_core.procedures.util.BlockSpreadUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

/**
 * 常用 {@link TransformAction} 的静态工厂。
 *
 * <p>底层扩散逻辑委托给 {@link BlockSpreadUtils}。</p>
 */
public final class TransformActions {

    /** 将当前方块替换为指定方块（不扩散）。 */
    public static TransformAction setBlock(Block target) {
        return (world, x, y, z) ->
                world.setBlock(BlockPos.containing(x, y, z), target.defaultBlockState(), 3);
    }

    /** 将当前方块替换为指定方块状态（不扩散）。 */
    public static TransformAction setBlockState(BlockState target) {
        return (world, x, y, z) ->
                world.setBlock(BlockPos.containing(x, y, z), target, 3);
    }

    /** 替换中心并向4方向水平邻居扩散。 */
    public static TransformAction spread4H(BlockState target, Predicate<BlockState> neighborPredicate) {
        return (world, x, y, z) ->
                BlockSpreadUtils.spreadBlock(world, x, y, z, target, neighborPredicate, BlockSpreadUtils.OFFSETS_4H);
    }

    /** 替换中心并向8方向水平邻居扩散（含对角）。 */
    public static TransformAction spread8H(BlockState target, Predicate<BlockState> neighborPredicate) {
        return (world, x, y, z) ->
                BlockSpreadUtils.spreadBlock(world, x, y, z, target, neighborPredicate, BlockSpreadUtils.OFFSETS_8H);
    }

    /** 替换中心并向17格扩散（8H + 下层9格）。 */
    public static TransformAction spread17(BlockState target, Predicate<BlockState> neighborPredicate) {
        return (world, x, y, z) ->
                BlockSpreadUtils.spreadBlock(world, x, y, z, target, neighborPredicate, BlockSpreadUtils.OFFSETS_17);
    }

    /** 替换中心并向49格扩散（5×5同层 + 5×5下层）。 */
    public static TransformAction spread5x5(BlockState target, Predicate<BlockState> neighborPredicate) {
        return (world, x, y, z) ->
                BlockSpreadUtils.spreadBlock(world, x, y, z, target, neighborPredicate, BlockSpreadUtils.OFFSETS_5X5);
    }

    private TransformActions() {}
}
