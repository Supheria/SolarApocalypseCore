package com.supheria.solar_apocalypse_core.world;

import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;

/**
 * 统一封装太阳阶段下的高度区间语义与受农业限制的植物类型判断。
 */
public final class HeightZoneHelper {
    public enum HeightZone {
        COZY,
        SAFE,
        DANGER
    }

    public static HeightZone getHeightZone(LevelAccessor level, BlockPos pos) {
        SolarStage stage = SolarModVariables.MapVariables.get(level).getSolarStage();
        return getHeightZone(stage, pos.getY());
    }

    public static HeightZone getHeightZone(SolarStage stage, int y) {
        if (stage == null || stage == SolarStage.NONE) {
            return HeightZone.COZY;
        }
        int cozyHeight = StageHeightConfig.getCozyHeight(stage);
        int safeHeight = StageHeightConfig.getSafeHeight(stage);
        if (y < cozyHeight) {
            return HeightZone.COZY;
        }
        if (y <= safeHeight) {
            return HeightZone.SAFE;
        }
        return HeightZone.DANGER;
    }

    public static boolean isCozyHeight(LevelAccessor level, BlockPos pos) {
        return getHeightZone(level, pos) == HeightZone.COZY;
    }

    public static boolean isSafeHeight(LevelAccessor level, BlockPos pos) {
        return getHeightZone(level, pos) == HeightZone.SAFE;
    }

    public static boolean isDangerHeight(LevelAccessor level, BlockPos pos) {
        return getHeightZone(level, pos) == HeightZone.DANGER;
    }

    public static boolean allowsNaturalGrowth(LevelAccessor level, BlockPos pos) {
        return isCozyHeight(level, pos);
    }

    public static boolean isManagedPlant(Block block) {
        return block instanceof CropBlock
                || block instanceof StemBlock
                || block instanceof SaplingBlock
                || block instanceof SweetBerryBushBlock
                || block instanceof BambooSaplingBlock
                || block instanceof BambooStalkBlock
                || block instanceof SugarCaneBlock
                || block instanceof CactusBlock
                || block instanceof CocoaBlock;
    }

    private HeightZoneHelper() {}
}
