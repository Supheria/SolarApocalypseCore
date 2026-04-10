package com.supheria.solar_apocalypse_core.transforms.fluid;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.transforms.rule.TransformRule;
import com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils;
import com.supheria.solar_apocalypse_core.world.SolarStage;

import static com.supheria.solar_apocalypse_core.transforms.rule.TransformActions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.*;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformConditions.aboveSafeHeight;
import static com.supheria.solar_apocalypse_core.transforms.rule.TransformRule.when;

/**
 * 冰类方块融化过程。
 */
public class IceMelt {

    public static final BlockTransform TRANSFORM = TransformRule.rulesOf(
            when(stageExact(SolarStage.STAGE_1).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    setBlockState(net.minecraft.world.level.block.Blocks.WATER.defaultBlockState())),

            when(stageExact(SolarStage.STAGE_2).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    removeIce(BlockSpreadUtils.OFFSETS_4H)),
            when(stageExact(SolarStage.STAGE_2).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    spreadIce4H(net.minecraft.world.level.block.Blocks.WATER.defaultBlockState())),
            when(stageExact(SolarStage.STAGE_2).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    spreadIce4H(net.minecraft.world.level.block.Blocks.ICE.defaultBlockState())),
            when(stageExact(SolarStage.STAGE_2).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    removeIce(BlockSpreadUtils.OFFSETS_8H)),

            when(stageExact(SolarStage.STAGE_3).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    removeIce(BlockSpreadUtils.OFFSETS_4H)),
            when(stageExact(SolarStage.STAGE_3).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    spreadIce4H(net.minecraft.world.level.block.Blocks.PACKED_ICE.defaultBlockState())),
            when(stageExact(SolarStage.STAGE_3).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    spreadIce4H(net.minecraft.world.level.block.Blocks.ICE.defaultBlockState())),
            when(stageExact(SolarStage.STAGE_3).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    spreadIce4H(net.minecraft.world.level.block.Blocks.WATER.defaultBlockState())),

            when(stageExact(SolarStage.STAGE_4).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    removeIce(BlockSpreadUtils.OFFSETS_8H)),
            when(stageExact(SolarStage.STAGE_4).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    spreadIce8H(net.minecraft.world.level.block.Blocks.ICE.defaultBlockState())),

            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()),
                    removeIce(BlockSpreadUtils.OFFSETS_17))
    );

    private IceMelt() {}
}
