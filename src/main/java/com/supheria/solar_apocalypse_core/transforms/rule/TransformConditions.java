package com.supheria.solar_apocalypse_core.transforms.rule;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * 常用 {@link TransformCondition} 的静态工厂。
 *
 * <p>所有方法均返回无状态的 lambda，可安全地组合复用：</p>
 * <pre>
 * stageRange(2, 6).and(sky()).and(noRain()).and(randomDay(10))
 * </pre>
 */
public final class TransformConditions {
    // -----------------------------------------------------------------------
    // 阶段 (Stage)
    // -----------------------------------------------------------------------

    /** stage >= min && stage < maxExclusive */
    public static TransformCondition stageRange(SolarStage min, SolarStage maxExclusive) {
        return (world, x, y, z, stage) -> stage.isAtLeast(min) && stage.isBefore(maxExclusive);
    }

    /** stage == s */
    public static TransformCondition stageExact(SolarStage s) {
        return (world, x, y, z, stage) -> stage == s;
    }

    /**
     * 判断是否为爆发阶段（STAGE_1 - STAGE_5）
     */
    public static TransformCondition stageIsEruptionPhase() {
        return (world, x, y, z, stage) -> stage != SolarStage.NONE && stage != SolarStage.STAGE_6;
    }

    /**
     * 判断是否为坍缺阶段（STAGE_6）
     */
    public static TransformCondition stageIsCollapsePhase() {
        return (world, x, y, z, stage) -> stage == SolarStage.STAGE_6;
    }

    // -----------------------------------------------------------------------
    // 世界状态 (World State)
    // -----------------------------------------------------------------------

    /** 天空可见（上方一格） */
    public static TransformCondition sky() {
        return (world, x, y, z, stage) ->
                world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z));
    }

    /** 上方一格无水方块 */
    public static TransformCondition noWaterAbove() {
        return (world, x, y, z, stage) ->
                world.getBlockState(BlockPos.containing(x, y + 1, z)).getBlock() != Blocks.WATER;
    }

    /** 当前方块必须是完整水源块。 */
    public static TransformCondition waterSource() {
        return (world, x, y, z, stage) ->
                world.getFluidState(BlockPos.containing(x, y, z)).is(FluidTags.WATER)
                        && world.getFluidState(BlockPos.containing(x, y, z)).isSource();
    }

    /** 当前方块必须是最上层完整水源块。 */
    public static TransformCondition surfaceWaterSource() {
        return (world, x, y, z, stage) -> BlockSpreadUtils.isSurfaceWater(world, BlockPos.containing(x, y, z));
    }

    /** 当前方块必须是最上层水面的边缘水源块。 */
    public static TransformCondition surfaceWaterEdgeSource() {
        return (world, x, y, z, stage) -> BlockSpreadUtils.isSurfaceWaterEdge(world, BlockPos.containing(x, y, z));
    }

    /** 当前不下雨 */
    public static TransformCondition noRain() {
        return (world, x, y, z, stage) -> !world.getLevelData().isRaining();
    }

    /** 当前是白天（currentTimeOfDay 不在夜间区间 12566–23450） */
    public static TransformCondition daytime() {
        return (world, x, y, z, stage) -> {
            long t = (long) SolarModVariables.MapVariables.get(world).currentTimeOfDay;
            return !SolarStageHelper.isNightWindow(t);
        };
    }

    /** 六面相邻有岩浆 */
    public static TransformCondition adjacentLava() {
        return (world, x, y, z, stage) -> BlockSpreadUtils.hasAdjacentLava(world, x, y, z);
    }

    /** 上方一格是空气 */
    public static TransformCondition airAbove() {
        return (world, x, y, z, stage) ->
                world.getBlockState(BlockPos.containing(x, y + 1, z)).getBlock() == Blocks.AIR;
    }

    // -----------------------------------------------------------------------
    // 高度 (Height)
    // -----------------------------------------------------------------------

    /** y >= StageHeightConfig.getSafeHeight(forStage) */
    public static TransformCondition aboveSafeHeight() {
        return (world, x, y, z, stage) -> y >= StageHeightConfig.getSafeHeight(stage);
    }

    /** y >= StageHeightConfig.getCozyHeight(forStage) */
    public static TransformCondition aboveCozyHeight() {
        return (world, x, y, z, stage) -> y >= StageHeightConfig.getCozyHeight(stage);
    }

    /** world.dayTime() < maxTicks */
    public static TransformCondition dayTimeLessThan(long maxTicks) {
        return (world, x, y, z, stage) -> world.dayTime() < maxTicks;
    }

    /**
     * 按当前阶段速率判定本次是否允许触发。
     */
    public static TransformCondition stageRate() {
        return (world, x, y, z, stage) -> true;
    }

    /**
     * 在当前阶段速率上叠加规则自身倍率，用于后期阶段适当加速重型链。
     */
    public static TransformCondition stageRateScaled(double multiplier) {
        return (world, x, y, z, stage) -> true;
    }

    /** 当前坐标处的方块为指定方块。 */
    public static TransformCondition isBlock(Block block) {
        return (world, x, y, z, stage) ->
                world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == block;
    }

    /**
     * y >= min(getSafeHeight(stageA), getSafeHeight(stageB)) - lazy evaluation。
     * 用于砾石→岩浆阶段4-5：取两阶段安全高度的较小值，保证一致的触发下限。
     */
    public static TransformCondition aboveMinOf(SolarStage stageA, SolarStage stageB) {
        return (world, x, y, z, stage) ->
                y >= Math.min(StageHeightConfig.getSafeHeight(stageA), StageHeightConfig.getSafeHeight(stageB));
    }

    /** y > StageHeightConfig.getSafeHeight(stage)（水蒸发规则与安全高度同步，严格大于） */
    public static TransformCondition aboveWaterEvaporateHeight() {
        return (world, x, y, z, stage) -> y > StageHeightConfig.getWaterEvaporateHeight(stage);
    }

    /** y >= threshold (冰融化高度条件) */
    public static TransformCondition minIceHeight(double threshold) {
        return (world, x, y, z, stage) -> y >= threshold;
    }

    private TransformConditions() {}
}
