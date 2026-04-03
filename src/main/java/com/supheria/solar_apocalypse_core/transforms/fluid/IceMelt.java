package com.supheria.solar_apocalypse_core.transforms.fluid;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

/**
 * 冰类方块融化过程。
 */
public class IceMelt {

    public static final BlockTransform TRANSFORM = IceMelt::transform;

    private static void transform(LevelAccessor world, double x, double y, double z) {
        if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.LAVA) return;

        int stage = (int) SapModVariables.MapVariables.get(world).SolarFlare;
        BlockPos pos = BlockPos.containing(x, y, z);

        if (BlockSpreadUtils.isOverworld(world, x, y, z)) {
            long todayTime = (long) SapModVariables.MapVariables.get(world).TodayTime;
            boolean daytime = !(todayTime > 12566 && todayTime < 23450);

            if (daytime) {
                // --- 普通冰 / 霜冰 ---
                var block = world.getBlockState(pos).getBlock();
                if (block == Blocks.ICE || block == Blocks.FROSTED_ICE) {
                    // 规则A：阶段1-5，概率融化为水
                    if (Mth.nextDouble(RandomSource.create(), 0, 10) <= (world.dayTime() / 24000) / 1.5
                            && stage >= 1 && stage < 6) {
                        world.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
                    }
                    // 规则B/C/D（读取调用时的当前方块状态，可能已由A变为水）
                    if (Mth.nextDouble(RandomSource.create(), 0, 10) <= (world.dayTime() / 24000) / 1.5
                            && stage == 2 && y >= StageHeightConfig.getSafeHeight(2)) {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    } else if (Mth.nextDouble(RandomSource.create(), 0, 10) <= (world.dayTime() / 24000) / 1.5
                            && stage == 2 && y < 63 && y >= 8) {
                        world.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
                    } else if (stage >= 3 && stage < 6 && y >= StageHeightConfig.getSafeHeight(3)) {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        BlockSpreadUtils.spreadNeighbors(world, x, y, z,
                                Blocks.AIR.defaultBlockState(),
                                bs -> bs.getBlock() == Blocks.ICE || bs.getBlock() == Blocks.FROSTED_ICE,
                                BlockSpreadUtils.OFFSETS_4H);
                    }
                }

                // --- 浮冰 ---
                if (world.getBlockState(pos).getBlock() == Blocks.PACKED_ICE) {
                    if (Mth.nextDouble(RandomSource.create(), 0, 15) <= (world.dayTime() / 24000) / 1.5
                            && stage == 2) {
                        world.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                    } else if (stage == 3 && y >= StageHeightConfig.getSafeHeight(2)) {
                        world.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
                        BlockSpreadUtils.spreadNeighbors(world, x, y, z,
                                Blocks.WATER.defaultBlockState(),
                                bs -> bs.getBlock() == Blocks.PACKED_ICE,
                                BlockSpreadUtils.OFFSETS_4H);
                    } else if (stage == 3
                            && y < StageHeightConfig.getSafeHeight(2) && y >= StageHeightConfig.getSafeHeight(4)) {
                        world.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                        BlockSpreadUtils.spreadNeighbors(world, x, y, z,
                                Blocks.ICE.defaultBlockState(),
                                bs -> bs.getBlock() == Blocks.PACKED_ICE,
                                BlockSpreadUtils.OFFSETS_4H);
                    } else if (stage >= 4 && stage < 6 && y >= 8) {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        BlockSpreadUtils.spreadNeighbors(world, x, y, z,
                                Blocks.AIR.defaultBlockState(),
                                bs -> bs.getBlock() == Blocks.PACKED_ICE,
                                BlockSpreadUtils.OFFSETS_8H);
                    }
                }

                // --- 蓝冰 ---
                if (world.getBlockState(pos).getBlock() == Blocks.BLUE_ICE) {
                    if (stage == 3 && y >= 8) {
                        world.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), 3);
                        BlockSpreadUtils.spreadNeighbors(world, x, y, z,
                                Blocks.PACKED_ICE.defaultBlockState(),
                                bs -> bs.getBlock() == Blocks.BLUE_ICE,
                                BlockSpreadUtils.OFFSETS_4H);
                    } else if (stage >= 4 && stage < 6 && y >= 63) {
                        world.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                        BlockSpreadUtils.spreadNeighbors(world, x, y, z,
                                Blocks.PACKED_ICE.defaultBlockState(),
                                bs -> bs.getBlock() == Blocks.BLUE_ICE,
                                BlockSpreadUtils.OFFSETS_8H);
                    } else if (stage >= 4 && stage < 6 && y >= StageHeightConfig.getSafeHeight(4)) {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        BlockSpreadUtils.spreadNeighbors(world, x, y, z,
                                Blocks.AIR.defaultBlockState(),
                                bs -> bs.getBlock() == Blocks.BLUE_ICE,
                                BlockSpreadUtils.OFFSETS_8H);
                    }
                }
            }

            // 阶段5：无需晴天，直接消除+spread17所有冰类
            if (stage == 5 && y >= 8) {
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                BlockSpreadUtils.spreadNeighbors(world, x, y, z,
                        Blocks.AIR.defaultBlockState(),
                        bs -> bs.is(BlockTags.ICE),
                        BlockSpreadUtils.OFFSETS_17);
            }
        }
    }

    private IceMelt() {}
}
