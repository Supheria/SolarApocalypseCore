package com.supheria.solar_apocalypse_core.transforms.rule;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * 常用 {@link TransformAction} 的静态工厂。
 *
 * <p>底层扩散逻辑委托给 {@link BlockSpreadUtils}。</p>
 */
public final class TransformActions {

    /** 将当前方块替换为指定方块（不扩散）。 */
    public static TransformAction setBlock(Block target) {
        return setBlockState(target.defaultBlockState());
    }

    /** 将当前方块替换为指定方块状态（不扩散）。 */
    public static TransformAction setBlockState(BlockState target) {
        return (world, x, y, z) -> BlockSpreadUtils.setBlockIfChanged(world, BlockPos.containing(x, y, z), target);
    }

    /** 替换中心并向4方向水平邻居扩散。 */
    public static TransformAction spread4H(BlockState target, Predicate<BlockState> neighborPredicate) {
        return (world, x, y, z) ->
                BlockSpreadUtils.spreadBlock(world, x, y, z, target, neighborPredicate, BlockSpreadUtils.OFFSETS_4H);
    }

    public static TransformAction spread4HRateLimited(BlockState target, Predicate<BlockState> neighborPredicate) {
        return spreadRateLimited(target, neighborPredicate, BlockSpreadUtils.OFFSETS_4H, SolarStageConfig::getStageSpreadBudget);
    }

    /** 替换中心并向8方向水平邻居扩散（含对角）。 */
    public static TransformAction spread8H(BlockState target, Predicate<BlockState> neighborPredicate) {
        return (world, x, y, z) ->
                BlockSpreadUtils.spreadBlock(world, x, y, z, target, neighborPredicate, BlockSpreadUtils.OFFSETS_8H);
    }

    public static TransformAction spread8HRateLimited(BlockState target, Predicate<BlockState> neighborPredicate) {
        return spreadRateLimited(target, neighborPredicate, BlockSpreadUtils.OFFSETS_8H, SolarStageConfig::getStageSpreadBudget);
    }

    /** 替换中心并向17格扩散（8H + 下层9格）。 */
    public static TransformAction spread17(BlockState target, Predicate<BlockState> neighborPredicate) {
        return (world, x, y, z) ->
                BlockSpreadUtils.spreadBlock(world, x, y, z, target, neighborPredicate, BlockSpreadUtils.OFFSETS_17);
    }

    public static TransformAction spread17RateLimited(BlockState target, Predicate<BlockState> neighborPredicate) {
        return spreadRateLimited(target, neighborPredicate, BlockSpreadUtils.OFFSETS_17, SolarStageConfig::getStageSpreadBudget);
    }

    /** 替换中心并向49格扩散（5×5同层 + 5×5下层）。 */
    public static TransformAction spread5x5(BlockState target, Predicate<BlockState> neighborPredicate) {
        return (world, x, y, z) ->
                BlockSpreadUtils.spreadBlock(world, x, y, z, target, neighborPredicate, BlockSpreadUtils.OFFSETS_5X5);
    }

    public static TransformAction spread5x5RateLimited(BlockState target, Predicate<BlockState> neighborPredicate) {
        return spreadRateLimited(target, neighborPredicate, BlockSpreadUtils.OFFSETS_5X5, SolarStageConfig::getStageSpreadBudget);
    }

    private static TransformAction spreadRateLimited(BlockState target, Predicate<BlockState> neighborPredicate,
                                                     int[][] offsets, ToIntFunction<SolarStage> budgetProvider) {
        return (world, x, y, z) -> BlockSpreadUtils.spreadBlockLimited(world, x, y, z, target, neighborPredicate,
                offsets, budgetProvider.applyAsInt(getSolarStage(world)));
    }

    private static TransformAction spreadSameLayerFirstRateLimited(BlockState target, Predicate<BlockState> neighborPredicate,
                                                                   int[][] offsets, ToIntFunction<SolarStage> budgetProvider) {
        return (world, x, y, z) -> BlockSpreadUtils.spreadBlockSameLayerFirstLimited(world, x, y, z, target, neighborPredicate,
                offsets, budgetProvider.applyAsInt(getSolarStage(world)));
    }

    public static TransformAction spreadWaterRateLimited(int[][] offsets) {
        return (world, x, y, z) -> BlockSpreadUtils.spreadWaterLimited(world, x, y, z, offsets,
                SolarStageConfig.getWaterSpreadBudget(getSolarStage(world)));
    }

    public static TransformAction spreadWaterRateLimited(int[][] offsets, ToIntFunction<SolarStage> budgetProvider) {
        return (world, x, y, z) -> BlockSpreadUtils.spreadWaterLimited(world, x, y, z, offsets,
                budgetProvider.applyAsInt(getSolarStage(world)));
    }

    public static TransformAction freezeSurfaceWaterRateLimited(int[][] offsets, ToIntFunction<SolarStage> budgetProvider) {
        return (world, x, y, z) -> BlockSpreadUtils.freezeSurfaceWaterLimited(world, BlockPos.containing(x, y, z), offsets,
                budgetProvider.applyAsInt(getSolarStage(world)));
    }

    private static SolarStage getSolarStage(LevelAccessor world) {
        return SolarModVariables.MapVariables.get(world).getSolarStage();
    }

    public static int stageSpreadBudget(LevelAccessor world) {
        return SolarStageConfig.getStageSpreadBudget(getSolarStage(world));
    }

    public static int waterSpreadBudget(LevelAccessor world) {
        return SolarStageConfig.getWaterSpreadBudget(getSolarStage(world));
    }

    public static int waterSpreadBudget(SolarStage stage) {
        return SolarStageConfig.getWaterSpreadBudget(stage);
    }

    public static int stageSpreadBudget(SolarStage stage) {
        return SolarStageConfig.getStageSpreadBudget(stage);
    }

    public static int scaleBudget(SolarStage stage, double multiplier) {
        return Math.max(1, (int) Math.round(SolarStageConfig.getStageSpreadBudget(stage) * multiplier));
    }

    public static int scaleBudget(LevelAccessor world, double multiplier) {
        return scaleBudget(getSolarStage(world), multiplier);
    }

    public static int scaleWaterBudget(SolarStage stage, double multiplier) {
        return Math.max(1, (int) Math.round(SolarStageConfig.getWaterSpreadBudget(stage) * multiplier));
    }

    public static int scaleWaterBudget(LevelAccessor world, double multiplier) {
        return scaleWaterBudget(getSolarStage(world), multiplier);
    }

    public static int countMatchingNeighbors(LevelAccessor world, double x, double y, double z,
                                             Predicate<BlockState> predicate, int[][] offsets, int limit) {
        return BlockSpreadUtils.countMatchingNeighbors(world, BlockPos.containing(x, y, z), offsets, predicate, limit);
    }

    public static int countWaterNeighbors(LevelAccessor world, double x, double y, double z, int[][] offsets, int limit) {
        return BlockSpreadUtils.countWaterNeighbors(world, BlockPos.containing(x, y, z), offsets, limit);
    }

    public static int countSurfaceWater(LevelAccessor world, double x, double y, double z, int[][] offsets, int limit) {
        return BlockSpreadUtils.countSurfaceWater(world, BlockPos.containing(x, y, z), offsets, limit);
    }

    public static int collapseSnowBudget() {
        return SolarStageConfig.getCollapseSnowStepBudget();
    }

    public static int collapseWaterBudget() {
        return SolarStageConfig.getStageSpreadBudget(SolarStage.STAGE_6);
    }

    public static int collapseWaterSampleBudget() {
        return SolarStageConfig.getCollapseWaterSampleCount();
    }

    public static int collapseSnowSampleBudget() {
        return SolarStageConfig.getCollapseSnowSampleCount();
    }

    public static int collapseSnowPulseRate() {
        return SolarStageConfig.getCollapseSnowAccumulationRate();
    }

    public static int collapseWaterPulseRate() {
        return SolarStageConfig.getCollapseWaterFreezeRate();
    }

    public static int clampBudget(int budget, int max) {
        return Math.max(1, Math.min(budget, max));
    }

    public static int chooseBudget(int preferred, int fallback) {
        return preferred > 0 ? preferred : fallback;
    }

    public static int matchedSpreadBudget(LevelAccessor world, double x, double y, double z,
                                          Predicate<BlockState> predicate, int[][] offsets, double multiplier) {
        int budget = scaleBudget(world, multiplier);
        return Math.max(1, Math.min(budget, countMatchingNeighbors(world, x, y, z, predicate, offsets, budget)));
    }

    public static int matchedWaterBudget(LevelAccessor world, double x, double y, double z, int[][] offsets, double multiplier) {
        int budget = scaleWaterBudget(world, multiplier);
        return Math.max(1, Math.min(budget, countWaterNeighbors(world, x, y, z, offsets, budget)));
    }

    public static int matchedSurfaceWaterBudget(LevelAccessor world, double x, double y, double z, int[][] offsets, double multiplier) {
        int budget = scaleBudget(world, multiplier);
        return Math.max(1, Math.min(budget, countSurfaceWater(world, x, y, z, offsets, budget)));
    }

    public static TransformAction spread5x5RateLimited(BlockState target, Predicate<BlockState> neighborPredicate,
                                                        ToIntFunction<SolarStage> budgetProvider) {
        return spreadRateLimited(target, neighborPredicate, BlockSpreadUtils.OFFSETS_5X5, budgetProvider);
    }

    public static TransformAction spread5x5SameLayerFirstRateLimited(BlockState target, Predicate<BlockState> neighborPredicate,
                                                                     ToIntFunction<SolarStage> budgetProvider) {
        return spreadSameLayerFirstRateLimited(target, neighborPredicate, BlockSpreadUtils.OFFSETS_5X5, budgetProvider);
    }

    public static TransformAction spread17RateLimited(BlockState target, Predicate<BlockState> neighborPredicate,
                                                       ToIntFunction<SolarStage> budgetProvider) {
        return spreadRateLimited(target, neighborPredicate, BlockSpreadUtils.OFFSETS_17, budgetProvider);
    }

    public static TransformAction spread17SameLayerFirstRateLimited(BlockState target, Predicate<BlockState> neighborPredicate,
                                                                    ToIntFunction<SolarStage> budgetProvider) {
        return spreadSameLayerFirstRateLimited(target, neighborPredicate, BlockSpreadUtils.OFFSETS_17, budgetProvider);
    }

    public static TransformAction spread8HRateLimited(BlockState target, Predicate<BlockState> neighborPredicate,
                                                       ToIntFunction<SolarStage> budgetProvider) {
        return spreadRateLimited(target, neighborPredicate, BlockSpreadUtils.OFFSETS_8H, budgetProvider);
    }

    public static TransformAction spread4HRateLimited(BlockState target, Predicate<BlockState> neighborPredicate,
                                                       ToIntFunction<SolarStage> budgetProvider) {
        return spreadRateLimited(target, neighborPredicate, BlockSpreadUtils.OFFSETS_4H, budgetProvider);
    }

    public static TransformAction freezeSurfaceWaterRateLimited(int[][] offsets) {
        return freezeSurfaceWaterRateLimited(offsets, SolarStageConfig::getStageSpreadBudget);
    }

    public static TransformAction spreadWaterRateLimited(int[][] offsets, double multiplier) {
        return spreadWaterRateLimited(offsets, stage -> Math.max(1,
                (int) Math.round(SolarStageConfig.getWaterSpreadBudget(stage) * multiplier)));
    }

    public static TransformAction spread5x5RateLimited(BlockState target, Predicate<BlockState> neighborPredicate,
                                                        double multiplier) {
        return spread5x5RateLimited(target, neighborPredicate, stage -> scaleBudget(stage, multiplier));
    }

    public static TransformAction spread5x5SameLayerFirstRateLimited(BlockState target, Predicate<BlockState> neighborPredicate,
                                                                     double multiplier) {
        return spread5x5SameLayerFirstRateLimited(target, neighborPredicate, stage -> scaleBudget(stage, multiplier));
    }

    public static TransformAction spread17RateLimited(BlockState target, Predicate<BlockState> neighborPredicate,
                                                       double multiplier) {
        return spread17RateLimited(target, neighborPredicate, stage -> scaleBudget(stage, multiplier));
    }

    public static TransformAction spread17SameLayerFirstRateLimited(BlockState target, Predicate<BlockState> neighborPredicate,
                                                                    double multiplier) {
        return spread17SameLayerFirstRateLimited(target, neighborPredicate, stage -> scaleBudget(stage, multiplier));
    }

    public static TransformAction spread8HRateLimited(BlockState target, Predicate<BlockState> neighborPredicate,
                                                       double multiplier) {
        return spread8HRateLimited(target, neighborPredicate, stage -> scaleBudget(stage, multiplier));
    }

    public static TransformAction spread4HRateLimited(BlockState target, Predicate<BlockState> neighborPredicate,
                                                       double multiplier) {
        return spread4HRateLimited(target, neighborPredicate, stage -> scaleBudget(stage, multiplier));
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
