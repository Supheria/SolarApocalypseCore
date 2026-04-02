package com.supheria.solar_apocalypse_core.procedures.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

/**
 * 方块扩散转换工具类。
 *
 * <p>各 TC 类中大量重复的"检测相邻方块 → 批量 setBlock"代码，
 * 统一由本类的 spreadBlock 方法处理。</p>
 *
 * <h3>预定义扩散范围（offsets）</h3>
 * <ul>
 *   <li>{@link #OFFSETS_4H} — 4个水平相邻（东西南北）</li>
 *   <li>{@link #OFFSETS_8H} — 8个水平相邻（含对角）</li>
 *   <li>{@link #OFFSETS_17} — 8H + 下层9格（草/泥土链阶段5用）</li>
 *   <li>{@link #OFFSETS_5X5} — 5×5水平 + 5×5下层（沙子链阶段4-5用）</li>
 * </ul>
 */
public final class BlockSpreadUtils {

    /** 主世界 biome tag，所有 TC 类共享，避免重复创建对象。 */
    public static final TagKey<Biome> IS_OVERWORLD =
            TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"));

    // -------------------------------------------------------------------------
    // 预定义 offset 数组
    // -------------------------------------------------------------------------

    /** 4方向水平相邻 */
    public static final int[][] OFFSETS_4H = {
            {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}
    };

    /** 8方向水平相邻（含对角） */
    public static final int[][] OFFSETS_8H = {
            {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1},
            {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}
    };

    /**
     * 17格范围：8H（同层含对角）+ 下层3×3共9格。
     * 用于草/泥土/粗泥土/碎泥土/沙子链的阶段5大范围消除。
     */
    public static final int[][] OFFSETS_17 = buildOffsets17();

    /**
     * 49格范围：同层5×5（去中心24格）+ 下层5×5共25格。
     * 用于沙子→尘土链的阶段4-5大范围消除。
     */
    public static final int[][] OFFSETS_5X5 = buildOffsets5x5();

    // -------------------------------------------------------------------------
    // 核心方法
    // -------------------------------------------------------------------------

    /**
     * 将中心方块设为 target，并将 offsets 所指各位置中匹配 neighborPredicate 的方块也设为 target。
     *
     * @param world             世界
     * @param cx, cy, cz        中心坐标
     * @param target            目标方块状态
     * @param neighborPredicate 判断邻居是否应转换的条件
     * @param offsets           要检测的相对偏移列表
     */
    public static void spreadBlock(LevelAccessor world, double cx, double cy, double cz,
                                   BlockState target, Predicate<BlockState> neighborPredicate,
                                   int[][] offsets) {
        BlockPos center = BlockPos.containing(cx, cy, cz);
        world.setBlock(center, target, 3);
        for (int[] o : offsets) {
            BlockPos neighbor = center.offset(o[0], o[1], o[2]);
            if (neighborPredicate.test(world.getBlockState(neighbor))) {
                world.setBlock(neighbor, target, 3);
            }
        }
    }

    // -------------------------------------------------------------------------
    // 便利方法：常用主世界检测
    // -------------------------------------------------------------------------

    /** 判断当前坐标是否在主世界。 */
    public static boolean isOverworld(LevelAccessor world, double x, double y, double z) {
        return world.getBiome(BlockPos.containing(x, y, z)).is(IS_OVERWORLD);
    }

    // -------------------------------------------------------------------------
    // 私有构建方法
    // -------------------------------------------------------------------------

    private static int[][] buildOffsets17() {
        int[][] offsets = new int[17][3];
        int i = 0;
        // 同层 3×3 去中心 = 8格
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx != 0 || dz != 0) {
                    offsets[i++] = new int[]{dx, 0, dz};
                }
            }
        }
        // 下层 3×3 = 9格
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                offsets[i++] = new int[]{dx, -1, dz};
            }
        }
        return offsets;
    }

    private static int[][] buildOffsets5x5() {
        int same = 5 * 5 - 1; // 同层去中心
        int below = 5 * 5;    // 下层全部
        int[][] offsets = new int[same + below][3];
        int i = 0;
        // 同层 5×5 去中心
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (dx != 0 || dz != 0) {
                    offsets[i++] = new int[]{dx, 0, dz};
                }
            }
        }
        // 下层 5×5
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                offsets[i++] = new int[]{dx, -1, dz};
            }
        }
        return offsets;
    }

    private BlockSpreadUtils() {}
}
