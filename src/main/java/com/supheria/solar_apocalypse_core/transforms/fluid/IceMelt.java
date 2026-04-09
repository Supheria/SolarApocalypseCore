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
            // 阶段1：白天+概率，冰类融化为水
            when(stageExact(SolarStage.STAGE_1).and(sky()).and(daytime()).and(aboveSafeHeight()).and(randomDayRate()),
                    setBlockState(net.minecraft.world.level.block.Blocks.WATER.defaultBlockState())),

            // 阶段2：晴天+高于安全高度，冰类直接消除
            when(stageExact(SolarStage.STAGE_2).and(sky()).and(daytime()).and(aboveSafeHeight()).and(randomDayRate()),
                    removeIce(BlockSpreadUtils.OFFSETS_4H)),
            // 阶段2：晴天+高于安全高度，冰类变水
            when(stageExact(SolarStage.STAGE_2).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    spreadIce4H(net.minecraft.world.level.block.Blocks.WATER.defaultBlockState())),
            // 阶段2：晴天+高于安全高度，浮冰变冰
            when(stageExact(SolarStage.STAGE_2).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    spreadIce4H(net.minecraft.world.level.block.Blocks.ICE.defaultBlockState())),
            // 阶段2：晴天+高于安全高度，浮冰直接消除+spread8H
            when(stageExact(SolarStage.STAGE_2).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    removeIce(BlockSpreadUtils.OFFSETS_8H)),

            // 阶段3：晴天+高于安全高度，冰类直接消除+spread4H
            when(stageExact(SolarStage.STAGE_3).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    removeIce(BlockSpreadUtils.OFFSETS_4H)),
            // 阶段3：晴天+高于安全高度，蓝冰变浮冰+spread4H
            when(stageExact(SolarStage.STAGE_3).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    spreadIce4H(net.minecraft.world.level.block.Blocks.PACKED_ICE.defaultBlockState())),
            // 阶段3：晴天+高于安全高度，浮冰变冰+spread4H
            when(stageExact(SolarStage.STAGE_3).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    spreadIce4H(net.minecraft.world.level.block.Blocks.ICE.defaultBlockState())),
            // 阶段3：晴天+高于安全高度，浮冰变水+spread4H
            when(stageExact(SolarStage.STAGE_3).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    spreadIce4H(net.minecraft.world.level.block.Blocks.WATER.defaultBlockState())),

            // 阶段4：高于安全高度，冰类直接消除+spread8H
            when(stageExact(SolarStage.STAGE_4).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    removeIce(BlockSpreadUtils.OFFSETS_8H)),
            // 阶段4：高于安全高度，蓝冰变冰+spread8H
            when(stageExact(SolarStage.STAGE_4).and(sky()).and(daytime()).and(aboveSafeHeight()),
                    spreadIce8H(net.minecraft.world.level.block.Blocks.ICE.defaultBlockState())),

            // 阶段5：无论白天/夜晚，高于安全高度，直接消除+spread17所有冰类
            when(stageExact(SolarStage.STAGE_5).and(aboveSafeHeight()),
                    removeIce(BlockSpreadUtils.OFFSETS_17))
    );

    private IceMelt() {}
}
