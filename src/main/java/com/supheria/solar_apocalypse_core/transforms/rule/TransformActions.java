package com.supheria.solar_apocalypse_core.transforms.rule;

import com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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

    /**
     * 50% 概率二选一：随机执行 heads 或 tails 动作。
     * （枯叶/木块阶段高级燃烧逻辑中使用）
     */
    public static TransformAction coinFlip(TransformAction heads, TransformAction tails) {
        return (world, x, y, z) -> {
            if (Mth.nextDouble(RandomSource.create(), 0, 2) <= 1) {
                heads.execute(world, x, y, z);
            } else {
                tails.execute(world, x, y, z);
            }
        };
    }

    /** 将当前方块正上方一格设为指定方块（不影响当前方块本身）。 */
    public static TransformAction setBlockAbove(Block target) {
        return (world, x, y, z) ->
                world.setBlock(BlockPos.containing(x, y + 1, z), target.defaultBlockState(), 3);
    }

    /** 将中心及水流体邻居（按 offsets 扩散）全部设为 AIR（水蒸发扩散）。 */
    public static TransformAction spreadWater(int[][] offsets) {
        return (world, x, y, z) -> BlockSpreadUtils.spreadWater(world, x, y, z, offsets);
    }

    /** 替换中心并向4方向水平邻居扩散（冰类扩散）。 */
    public static TransformAction spreadIce4H(BlockState target) {
        return (world, x, y, z) ->
                BlockSpreadUtils.spreadBlock(world, x, y, z, target, bs -> bs.is(net.minecraft.tags.BlockTags.ICE), BlockSpreadUtils.OFFSETS_4H);
    }

    /** 替换中心并向8方向水平邻居扩散（冰类扩散）。 */
    public static TransformAction spreadIce8H(BlockState target) {
        return (world, x, y, z) ->
                BlockSpreadUtils.spreadBlock(world, x, y, z, target, bs -> bs.is(net.minecraft.tags.BlockTags.ICE), BlockSpreadUtils.OFFSETS_8H);
    }

    /** 替换中心并向17格扩散（冰类扩散）。 */
    public static TransformAction spreadIce17(BlockState target) {
        return (world, x, y, z) ->
                BlockSpreadUtils.spreadBlock(world, x, y, z, target, bs -> bs.is(net.minecraft.tags.BlockTags.ICE), BlockSpreadUtils.OFFSETS_17);
    }

    /** 替换中心并向5×5扩散（冰类扩散）。 */
    public static TransformAction spreadIce5x5(BlockState target) {
        return (world, x, y, z) ->
                BlockSpreadUtils.spreadBlock(world, x, y, z, target, bs -> bs.is(net.minecraft.tags.BlockTags.ICE), BlockSpreadUtils.OFFSETS_5X5);
    }

    /** 冰类方块直接消除（设为AIR）。 */
    public static TransformAction removeIce(int[][] offsets) {
        return (world, x, y, z) ->
                BlockSpreadUtils.spreadBlock(world, x, y, z, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),
                        bs -> bs.is(net.minecraft.tags.BlockTags.ICE), offsets);
    }

    private TransformActions() {}
}
