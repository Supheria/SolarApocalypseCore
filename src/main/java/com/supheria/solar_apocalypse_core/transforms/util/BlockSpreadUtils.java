package com.supheria.solar_apocalypse_core.transforms.util;

import com.supheria.solar_apocalypse_core.environment.EnvironmentalDirtyTracker;
import com.supheria.solar_apocalypse_core.environment.EnvironmentalWorkType;
import com.supheria.solar_apocalypse_core.integration.minecollapse.MineCollapseBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

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

    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private static final BlockState ICE = Blocks.ICE.defaultBlockState();

    /** 主世界 biome tag，所有 TC 类共享，避免重复创建对象。 */
    public static final TagKey<Biome> IS_OVERWORLD =
            TagKey.create(Registries.BIOME, ResourceLocation.parse("minecraft:is_overworld"));

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

    /** 以中心为圆心的 5×5 水平面（含中心），用于坍缺期海面小片冻结。 */
    public static final int[][] OFFSETS_SURFACE_WATER_5X5 = buildSameLayerOffsets5x5();

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

    /**
     * 水蒸发阶段4扩散范围：同层5×5（去中心24格）+ 下方4层各5×5共100格，合计124格。
     */
    public static final int[][] OFFSETS_WATER_5X5_4 = buildWaterSpread(4);

    /**
     * 水蒸发阶段5扩散范围：同层5×5（去中心24格）+ 下方10层各5×5共250格，合计274格。
     */
    public static final int[][] OFFSETS_WATER_5X5_10 = buildWaterSpread(10);

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
        spreadBlockLimited(world, cx, cy, cz, target, neighborPredicate, offsets, offsets.length);
    }

    public static void spreadBlockLimited(LevelAccessor world, double cx, double cy, double cz,
                                          BlockState target, Predicate<BlockState> neighborPredicate,
                                          int[][] offsets, int budget) {
        BlockPos center = BlockPos.containing(cx, cy, cz);
        setBlockIfChanged(world, center, target);
        visitLimitedOffsets(world, center, offsets, budget, pos -> neighborPredicate.test(world.getBlockState(pos)),
                pos -> queueDelayedBlockChange(world, pos, target, inferWorkType(target)));
    }

    public static void spreadBlockSameLayerFirstLimited(LevelAccessor world, double cx, double cy, double cz,
                                                        BlockState target, Predicate<BlockState> neighborPredicate,
                                                        int[][] offsets, int budget) {
        BlockPos center = BlockPos.containing(cx, cy, cz);
        setBlockIfChanged(world, center, target);
        visitLimitedOffsetsSameLayerFirst(world, center, offsets, budget,
                pos -> neighborPredicate.test(world.getBlockState(pos)),
                pos -> queueDelayedBlockChange(world, pos, target, inferWorkType(target)));
    }

    public static void spreadBlockLimited(LevelAccessor world, double cx, double cy, double cz,
                                          BlockState target, Predicate<BlockState> neighborPredicate,
                                          int[][] offsets) {
        spreadBlockLimited(world, cx, cy, cz, target, neighborPredicate, offsets, offsets.length);
    }

    public static void spreadNeighborsLimited(LevelAccessor world, double cx, double cy, double cz,
                                              BlockState target, Predicate<BlockState> neighborPredicate,
                                              int[][] offsets, int budget) {
        BlockPos center = BlockPos.containing(cx, cy, cz);
        visitLimitedOffsets(world, center, offsets, budget, pos -> neighborPredicate.test(world.getBlockState(pos)),
                pos -> queueDelayedBlockChange(world, pos, target, inferWorkType(target)));
    }

    public static void spreadNeighborsLimited(LevelAccessor world, double cx, double cy, double cz,
                                              BlockState target, Predicate<BlockState> neighborPredicate,
                                              int[][] offsets) {
        spreadNeighborsLimited(world, cx, cy, cz, target, neighborPredicate, offsets, offsets.length);
    }

    @FunctionalInterface
    private interface PositionConsumer {
        void accept(BlockPos pos);
    }

    @FunctionalInterface
    private interface PositionPredicate {
        boolean test(BlockPos pos);
    }

    private static void visitLimitedOffsets(LevelAccessor world, BlockPos center, int[][] offsets, int budget,
                                            PositionPredicate predicate, PositionConsumer consumer) {
        if (budget <= 0 || offsets.length == 0) {
            return;
        }

        int limit = Math.min(budget, offsets.length);
        int startIndex = getOffsetStartIndex(world, center, offsets.length);
        int visited = 0;
        for (int i = 0; i < offsets.length && visited < limit; i++) {
            int[] offset = offsets[(startIndex + i) % offsets.length];
            BlockPos candidate = center.offset(offset[0], offset[1], offset[2]);
            if (!predicate.test(candidate)) {
                continue;
            }
            consumer.accept(candidate);
            visited++;
        }
    }

    private static void visitLimitedOffsetsSameLayerFirst(LevelAccessor world, BlockPos center, int[][] offsets, int budget,
                                                          PositionPredicate predicate, PositionConsumer consumer) {
        if (budget <= 0 || offsets.length == 0) {
            return;
        }

        int remaining = Math.min(budget, offsets.length);
        remaining = visitOffsetsForLayer(world, center, offsets, 0, remaining, predicate, consumer);
        if (remaining > 0) {
            for (int dy = -1; remaining > 0; dy--) {
                boolean hasLayer = false;
                for (int[] offset : offsets) {
                    if (offset[1] == dy) {
                        hasLayer = true;
                        break;
                    }
                }
                if (!hasLayer) {
                    break;
                }
                remaining = visitOffsetsForLayer(world, center, offsets, dy, remaining, predicate, consumer);
            }
        }
    }

    private static int visitOffsetsForLayer(LevelAccessor world, BlockPos center, int[][] offsets, int dy, int budget,
                                            PositionPredicate predicate, PositionConsumer consumer) {
        if (budget <= 0) {
            return 0;
        }

        int layerLength = countOffsetsForLayer(offsets, dy);
        if (layerLength == 0) {
            return budget;
        }

        int startIndex = getOffsetStartIndex(world, center, layerLength);
        int matched = 0;
        for (int i = 0; i < layerLength && matched < budget; i++) {
            int[] offset = getLayerOffsetAt(offsets, dy, (startIndex + i) % layerLength);
            if (offset == null) {
                continue;
            }
            BlockPos candidate = center.offset(offset[0], offset[1], offset[2]);
            if (!predicate.test(candidate)) {
                continue;
            }
            consumer.accept(candidate);
            matched++;
        }
        return budget - matched;
    }

    private static int countOffsetsForLayer(int[][] offsets, int dy) {
        int count = 0;
        for (int[] offset : offsets) {
            if (offset[1] == dy) {
                count++;
            }
        }
        return count;
    }

    private static int[] getLayerOffsetAt(int[][] offsets, int dy, int targetIndex) {
        int currentIndex = 0;
        for (int[] offset : offsets) {
            if (offset[1] != dy) {
                continue;
            }
            if (currentIndex == targetIndex) {
                return offset;
            }
            currentIndex++;
        }
        return null;
    }

    private static int getOffsetStartIndex(LevelAccessor world, BlockPos center, int length) {
        RandomSource random = world.getRandom();
        return Math.floorMod(center.hashCode() + random.nextInt(length), length);
    }

    /**
     * 仅将 offsets 所指各位置中匹配 neighborPredicate 的方块设为 target（不修改中心方块）。
     * 用于冰融化等需要先单独设置中心、再扩散邻居的场景。
     */
    public static void spreadNeighbors(LevelAccessor world, double cx, double cy, double cz,
                                       BlockState target, Predicate<BlockState> neighborPredicate,
                                       int[][] offsets) {
        BlockPos center = BlockPos.containing(cx, cy, cz);
        for (int[] o : offsets) {
            BlockPos neighbor = center.offset(o[0], o[1], o[2]);
            if (neighborPredicate.test(world.getBlockState(neighbor))) {
                queueDelayedBlockChange(world, neighbor, target, inferWorkType(target));
            }
        }
    }

    /**
     * 将中心及 offsets 所指各位置中有水流体的方块全部设为 AIR。
     * 用于水蒸发类过程（WaterEvaporate）。
     */
    public static void spreadWater(LevelAccessor world, double cx, double cy, double cz, int[][] offsets) {
        spreadWaterLimited(world, cx, cy, cz, offsets, offsets.length);
    }

    public static void spreadWaterLimited(LevelAccessor world, double cx, double cy, double cz, int[][] offsets, int budget) {
        BlockPos center = BlockPos.containing(cx, cy, cz);
        setBlockIfChanged(world, center, AIR);
        visitLimitedOffsets(world, center, offsets, budget, pos -> world.getFluidState(pos).is(FluidTags.WATER),
                pos -> queueDelayedBlockChange(world, pos, AIR, EnvironmentalWorkType.WATER));
    }

    public static void spreadWaterLimited(LevelAccessor world, double cx, double cy, double cz, int[][] offsets) {
        spreadWaterLimited(world, cx, cy, cz, offsets, offsets.length);
    }

    public static void freezeSurfaceWaterLimited(LevelAccessor world, BlockPos center, int[][] offsets, int budget) {
        if (isSurfaceWater(world, center)) {
            setBlockIfChanged(world, center, ICE);
        }
        visitLimitedOffsets(world, center, offsets, budget, pos -> isSurfaceWater(world, pos),
                pos -> queueDelayedBlockChange(world, pos, ICE, EnvironmentalWorkType.ICE));
    }

    public static void freezeSurfaceWaterLimited(LevelAccessor world, BlockPos center, int[][] offsets) {
        freezeSurfaceWaterLimited(world, center, offsets, offsets.length);
    }

    public static int countMatchedOffsets(LevelAccessor world, BlockPos center, int[][] offsets,
                                          Predicate<BlockPos> predicate, int limit) {
        if (limit <= 0) {
            return 0;
        }

        int matches = 0;
        int startIndex = getOffsetStartIndex(world, center, offsets.length);
        for (int i = 0; i < offsets.length && matches < limit; i++) {
            int[] offset = offsets[(startIndex + i) % offsets.length];
            if (predicate.test(center.offset(offset[0], offset[1], offset[2]))) {
                matches++;
            }
        }
        return matches;
    }

    public static int countSurfaceWater(LevelAccessor world, BlockPos center, int[][] offsets, int limit) {
        int matches = isSurfaceWater(world, center) ? 1 : 0;
        if (matches >= limit) {
            return matches;
        }
        return matches + countMatchedOffsets(world, center, offsets, pos -> isSurfaceWater(world, pos), limit - matches);
    }

    public static int countWaterNeighbors(LevelAccessor world, BlockPos center, int[][] offsets, int limit) {
        int matches = world.getFluidState(center).is(FluidTags.WATER) ? 1 : 0;
        if (matches >= limit) {
            return matches;
        }
        return matches + countMatchedOffsets(world, center, offsets, pos -> world.getFluidState(pos).is(FluidTags.WATER), limit - matches);
    }

    public static int countMatchingNeighbors(LevelAccessor world, BlockPos center, int[][] offsets,
                                             Predicate<BlockState> predicate, int limit) {
        int matches = predicate.test(world.getBlockState(center)) ? 1 : 0;
        if (matches >= limit) {
            return matches;
        }
        return matches + countMatchedOffsets(world, center, offsets, pos -> predicate.test(world.getBlockState(pos)), limit - matches);
    }

    // -------------------------------------------------------------------------
    // 便利方法：常用世界状态检测
    // -------------------------------------------------------------------------

    /** 判断当前坐标是否在主世界。 */
    public static boolean isOverworld(LevelAccessor world, double x, double y, double z) {
        return world.getBiome(BlockPos.containing(x, y, z)).is(IS_OVERWORLD);
    }

    /** 判断六面相邻位置中是否有岩浆（石头/卵石 TC 扩散条件）。 */
    public static boolean hasAdjacentLava(LevelAccessor world, double x, double y, double z) {
        return world.getBlockState(BlockPos.containing(x, y + 1, z)).getBlock() == Blocks.LAVA
                || world.getBlockState(BlockPos.containing(x, y - 1, z)).getBlock() == Blocks.LAVA
                || world.getBlockState(BlockPos.containing(x + 1, y, z)).getBlock() == Blocks.LAVA
                || world.getBlockState(BlockPos.containing(x - 1, y, z)).getBlock() == Blocks.LAVA
                || world.getBlockState(BlockPos.containing(x, y, z + 1)).getBlock() == Blocks.LAVA
                || world.getBlockState(BlockPos.containing(x, y, z - 1)).getBlock() == Blocks.LAVA;
    }

    /** 判断六面相邻位置中是否有水（熔岩→黑曜石转换加速条件）。 */
    public static boolean hasAdjacentWater(LevelAccessor world, double x, double y, double z) {
        return world.getFluidState(BlockPos.containing(x + 1, y, z)).is(FluidTags.WATER)
                || world.getFluidState(BlockPos.containing(x - 1, y, z)).is(FluidTags.WATER)
                || world.getFluidState(BlockPos.containing(x, y + 1, z)).is(FluidTags.WATER)
                || world.getFluidState(BlockPos.containing(x, y - 1, z)).is(FluidTags.WATER)
                || world.getFluidState(BlockPos.containing(x, y, z + 1)).is(FluidTags.WATER)
                || world.getFluidState(BlockPos.containing(x, y, z - 1)).is(FluidTags.WATER);
    }

    public static void setBlockIfChanged(LevelAccessor world, BlockPos pos, BlockState target) {
        if (world.getBlockState(pos).equals(target)) {
            return;
        }

        if (target.is(Blocks.FIRE)) {
            queueDelayedBlockChange(world, pos, target, EnvironmentalWorkType.FIRE);
            return;
        }

        MineCollapseBridge.setBlockWithCurrentSource(world, pos, target);
    }

    public static void queueDelayedBlockChange(LevelAccessor world, BlockPos pos, BlockState target, EnvironmentalWorkType workType) {
        EnvironmentalDirtyTracker.queueBlockChange(world, pos, target, workType);
    }

    public static EnvironmentalWorkType inferWorkType(BlockState target) {
        if (target.is(Blocks.WATER) || target.isAir()) {
            return EnvironmentalWorkType.SURFACE;
        }
        if (target.is(Blocks.ICE) || target.is(Blocks.PACKED_ICE) || target.is(Blocks.BLUE_ICE) || target.is(Blocks.FROSTED_ICE)) {
            return EnvironmentalWorkType.ICE;
        }
        if (target.is(Blocks.FIRE)) {
            return EnvironmentalWorkType.FIRE;
        }
        if (target.is(Blocks.COBBLESTONE) || target.is(Blocks.COBBLED_DEEPSLATE) || target.is(Blocks.GRAVEL)
                || target.is(Blocks.LAVA) || target.is(Blocks.TERRACOTTA) || target.is(Blocks.OBSIDIAN)) {
            return EnvironmentalWorkType.STONE;
        }
        return EnvironmentalWorkType.SURFACE;
    }

    /**
     * 判断给定位置是否为最上层的静止水面。
     * 仅允许处理完整水源块，避免把流动中的边缘水或水下层错误冻成冰。
     */
    public static boolean isSurfaceWater(LevelAccessor world, BlockPos pos) {
        FluidState fluidState = world.getFluidState(pos);
        if (!fluidState.is(FluidTags.WATER) || !fluidState.isSource()) {
            return false;
        }
        return !world.getFluidState(pos.above()).is(FluidTags.WATER);
    }

    /**
     * 在同一高度平面内，把中心及 offsets 指向的表层水源块冻结为冰。
     */
    public static void freezeSurfaceWater(LevelAccessor world, BlockPos center, int[][] offsets) {
        if (isSurfaceWater(world, center)) {
            setBlockIfChanged(world, center, ICE);
        }
        for (int[] o : offsets) {
            BlockPos neighbor = center.offset(o[0], o[1], o[2]);
            if (isSurfaceWater(world, neighbor)) {
                setBlockIfChanged(world, neighbor, ICE);
            }
        }
    }

    // -------------------------------------------------------------------------
    // 私有构建方法
    // -------------------------------------------------------------------------

    private static int[][] buildSameLayerOffsets5x5() {
        int[][] offsets = new int[25][3];
        int i = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                offsets[i++] = new int[]{dx, 0, dz};
            }
        }
        return offsets;
    }

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

    /**
     * 构建水蒸发扩散偏移数组：同层5×5去中心（24格）+ 下方 lowerLayers 层各5×5（每层25格）。
     */
    private static int[][] buildWaterSpread(int lowerLayers) {
        int[][] offsets = new int[24 + lowerLayers * 25][3];
        int i = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (dx != 0 || dz != 0) offsets[i++] = new int[]{dx, 0, dz};
            }
        }
        for (int dy = -1; dy >= -lowerLayers; dy--) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    offsets[i++] = new int[]{dx, dy, dz};
                }
            }
        }
        return offsets;
    }

    private BlockSpreadUtils() {}
}
