package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.SolarApocalypseCoreMod;
import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.environment.EnvironmentalWorkType;
import com.supheria.solar_apocalypse_core.init.SolarModTags;
import com.supheria.solar_apocalypse_core.integration.minecollapse.MineCollapseBridge;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统一的太阳环境转变调度器。
 *
 * <p>把环境转变拆成三段：候选区块索引、待执行位置队列、主线程预算提交。
 * 这样既能维持大范围环境演化，又能把每 tick 的 CPU 开销与世界写入次数稳定限制在预算内。</p>
 */
@Mod.EventBusSubscriber
public final class EnvironmentalTransformScheduler {

    private static final int MIN_INDEXED_CHUNKS_PER_PLAYER = 18;
    private static final int CHUNK_INDEX_BUDGET_MULTIPLIER = 5;
    private static final int CHUNK_REFRESHES_PER_PLAYER = 6;
    private static final int FAR_CHUNK_REFRESHES_PER_PLAYER = 8;
    private static final int PLAYER_KEEPALIVE_RADIUS = 1;
    private static final int MIN_CHUNK_RADIUS = 2;
    private static final int MAX_CHUNK_RADIUS = 6;
    private static final int FAR_RADIUS_MULTIPLIER = 3;
    private static final int FAR_RADIUS_PADDING = 4;
    private static final int FAR_RING_THICKNESS = 4;
    private static final int FAR_TIME_SLICE_TICKS = 200;
    private static final int INDEX_INTERVAL_TICKS = 20;
    private static final int REFILL_INTERVAL_TICKS = 4;
    private static final int MIN_CHUNK_SAMPLES_PER_TICK = 8;
    private static final int CHUNK_SAMPLES_PER_PLAYER = 4;
    private static final int POSITIONS_PER_CHUNK_SAMPLE = 4;
    private static final int SUBSURFACE_SAMPLE_DEPTH = 12;
    private static final int CANOPY_UNDER_SAMPLE_DEPTH = 3;
    private static final int MAX_QUEUE_MULTIPLIER = 16;

    private static final Map<ResourceKey<Level>, LinkedHashSet<Long>> CANDIDATE_CHUNKS = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, EnumMap<EnvironmentalWorkType, LinkedHashSet<Long>>> POSITION_QUEUES = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, Long> LAST_INDEX_TICKS = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, Long> LAST_REFILL_TICKS = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) {
            return;
        }

        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) {
            clearDimension(level.dimension());
            return;
        }

        SolarStage stage = SolarModVariables.MapVariables.get(level).getSolarStage();
        if (stage == null || !stage.isAtLeast(SolarStage.STAGE_1)) {
            clearDimension(level.dimension());
            return;
        }

        RandomSource random = level.getRandom();
        long gameTime = level.getGameTime();
        if (shouldRunIndexPass(level.dimension(), gameTime)) {
            indexCandidateChunks(level, players, stage, random);
        }
        if (shouldRunRefillPass(level.dimension(), gameTime)) {
            refillPositionQueues(level, players.size(), stage, random);
        }
        processPositionQueues(level, stage);
    }

    private static void clearDimension(ResourceKey<Level> dimension) {
        CANDIDATE_CHUNKS.remove(dimension);
        POSITION_QUEUES.remove(dimension);
        LAST_INDEX_TICKS.remove(dimension);
        LAST_REFILL_TICKS.remove(dimension);
    }

    private static boolean shouldRunIndexPass(ResourceKey<Level> dimension, long gameTime) {
        Long lastTick = LAST_INDEX_TICKS.get(dimension);
        if (lastTick != null && gameTime - lastTick < INDEX_INTERVAL_TICKS) {
            return false;
        }
        LAST_INDEX_TICKS.put(dimension, gameTime);
        return true;
    }

    private static boolean shouldRunRefillPass(ResourceKey<Level> dimension, long gameTime) {
        Long lastTick = LAST_REFILL_TICKS.get(dimension);
        if (lastTick != null && gameTime - lastTick < REFILL_INTERVAL_TICKS) {
            return false;
        }
        LAST_REFILL_TICKS.put(dimension, gameTime);
        return true;
    }

    private static void indexCandidateChunks(ServerLevel level, List<ServerPlayer> players, SolarStage stage, RandomSource random) {
        LinkedHashSet<Long> chunks = CANDIDATE_CHUNKS.computeIfAbsent(level.dimension(), ignored -> new LinkedHashSet<>());
        int radius = chunkRadius(stage);
        int targetSize = Math.max(MIN_INDEXED_CHUNKS_PER_PLAYER * players.size(), stageBudget(stage) * CHUNK_INDEX_BUDGET_MULTIPLIER * players.size());
        keepAlivePlayerChunks(level, players, chunks);
        int refreshCount = Math.max(players.size() * CHUNK_REFRESHES_PER_PLAYER, targetSize / 4);
        int farRefreshCount = Math.max(players.size() * FAR_CHUNK_REFRESHES_PER_PLAYER, targetSize / 3);
        int attempts = Math.max(targetSize, refreshCount + farRefreshCount) * 3;
        while (attempts-- > 0 && (chunks.size() < targetSize || refreshCount > 0 || farRefreshCount > 0)) {
            ServerPlayer player = players.get(random.nextInt(players.size()));
            boolean useFarSample = farRefreshCount > 0 && (refreshCount <= 0 || random.nextBoolean());
            long candidate = useFarSample
                    ? sampleFarChunk(level, player, radius, level.getGameTime(), random)
                    : sampleNearChunk(player, radius, random);
            int chunkX = unpackChunkX(candidate);
            int chunkZ = unpackChunkZ(candidate);
            if (!level.hasChunk(chunkX, chunkZ)) {
                continue;
            }

            boolean isNew = chunks.add(candidate);
            if (!isNew) {
                // 重新插入已存在区块，让热点区块保持活跃，同时给新采样区块腾出淘汰顺序。
                chunks.remove(candidate);
                chunks.add(candidate);
            } else if (chunks.size() > targetSize) {
                removeOldestChunk(chunks);
            }

            if (useFarSample) {
                farRefreshCount--;
            } else if (refreshCount > 0) {
                refreshCount--;
            }
        }

        trimLinkedSet(chunks, targetSize * 2);
    }

    private static void keepAlivePlayerChunks(ServerLevel level, List<ServerPlayer> players, LinkedHashSet<Long> chunks) {
        for (ServerPlayer player : players) {
            int baseChunkX = player.chunkPosition().x;
            int baseChunkZ = player.chunkPosition().z;
            for (int dx = -PLAYER_KEEPALIVE_RADIUS; dx <= PLAYER_KEEPALIVE_RADIUS; dx++) {
                for (int dz = -PLAYER_KEEPALIVE_RADIUS; dz <= PLAYER_KEEPALIVE_RADIUS; dz++) {
                    int chunkX = baseChunkX + dx;
                    int chunkZ = baseChunkZ + dz;
                    if (!level.hasChunk(chunkX, chunkZ)) {
                        continue;
                    }
                    long key = chunkKey(chunkX, chunkZ);
                    chunks.remove(key);
                    chunks.add(key);
                }
            }
        }
    }

    private static long sampleNearChunk(ServerPlayer player, int radius, RandomSource random) {
        int chunkX = player.chunkPosition().x + random.nextInt(radius * 2 + 1) - radius;
        int chunkZ = player.chunkPosition().z + random.nextInt(radius * 2 + 1) - radius;
        return chunkKey(chunkX, chunkZ);
    }

    private static long sampleFarChunk(ServerLevel level, ServerPlayer player, int radius, long gameTime, RandomSource random) {
        int farRadius = Math.min(MAX_CHUNK_RADIUS * FAR_RADIUS_MULTIPLIER, radius * FAR_RADIUS_MULTIPLIER + FAR_RADIUS_PADDING);
        int minFarRadius = Math.max(radius + 1, farRadius - FAR_RING_THICKNESS);
        int timeSlice = (int) (gameTime / FAR_TIME_SLICE_TICKS);
        int angleBucket = Math.floorMod(timeSlice + player.getId(), 8);
        int[] direction = switch (angleBucket) {
            case 0 -> new int[]{1, 0};
            case 1 -> new int[]{1, 1};
            case 2 -> new int[]{0, 1};
            case 3 -> new int[]{-1, 1};
            case 4 -> new int[]{-1, 0};
            case 5 -> new int[]{-1, -1};
            case 6 -> new int[]{0, -1};
            default -> new int[]{1, -1};
        };

        int distance = minFarRadius + random.nextInt(Math.max(1, farRadius - minFarRadius + 1));
        int chunkX = player.chunkPosition().x + direction[0] * distance + random.nextInt(FAR_RING_THICKNESS * 2 + 1) - FAR_RING_THICKNESS;
        int chunkZ = player.chunkPosition().z + direction[1] * distance + random.nextInt(FAR_RING_THICKNESS * 2 + 1) - FAR_RING_THICKNESS;
        return chunkKey(chunkX, chunkZ);
    }

    private static void refillPositionQueues(ServerLevel level, int playerCount, SolarStage stage, RandomSource random) {
        LinkedHashSet<Long> chunks = CANDIDATE_CHUNKS.get(level.dimension());
        if (chunks == null || chunks.isEmpty()) {
            return;
        }

        EnumMap<EnvironmentalWorkType, LinkedHashSet<Long>> byType = POSITION_QUEUES.computeIfAbsent(level.dimension(), ignored -> new EnumMap<>(EnvironmentalWorkType.class));
        int maxPerQueue = maxQueueSize(stage, playerCount);
        int chunkSamples = Math.max(MIN_CHUNK_SAMPLES_PER_TICK, playerCount * CHUNK_SAMPLES_PER_PLAYER);
        ArrayList<Long> chunkList = new ArrayList<>(chunks);
        for (int i = 0; i < chunkSamples; i++) {
            long chunkKey = chunkList.get(random.nextInt(chunkList.size()));
            int chunkX = unpackChunkX(chunkKey);
            int chunkZ = unpackChunkZ(chunkKey);
            if (!level.hasChunk(chunkX, chunkZ)) {
                continue;
            }
            enqueueChunkSamples(level, chunkX, chunkZ, random, byType, maxPerQueue);
        }

        for (EnvironmentalWorkType workType : EnvironmentalWorkType.values()) {
            trimLinkedSet(byType.computeIfAbsent(workType, ignored -> new LinkedHashSet<>()), maxPerQueue);
        }
    }

    private static void enqueueChunkSamples(ServerLevel level, int chunkX, int chunkZ, RandomSource random,
                                            EnumMap<EnvironmentalWorkType, LinkedHashSet<Long>> byType, int maxPerQueue) {
        int minY = level.getMinBuildHeight();
        for (int i = 0; i < POSITIONS_PER_CHUNK_SAMPLE; i++) {
            int x = (chunkX << 4) + random.nextInt(16);
            int z = (chunkZ << 4) + random.nextInt(16);
            int canopyY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
            int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
            if (surfaceY < minY && canopyY < minY) {
                continue;
            }

            if (canopyY >= minY) {
                enqueuePosition(level, new BlockPos(x, canopyY, z), byType, maxPerQueue);

                int underCanopyY = Math.max(minY, canopyY - random.nextInt(CANOPY_UNDER_SAMPLE_DEPTH));
                if (underCanopyY != canopyY) {
                    enqueuePosition(level, new BlockPos(x, underCanopyY, z), byType, maxPerQueue);
                }
            }

            if (surfaceY >= minY && surfaceY != canopyY) {
                enqueuePosition(level, new BlockPos(x, surfaceY, z), byType, maxPerQueue);
            }

            int oceanFloorY = level.getHeight(Heightmap.Types.OCEAN_FLOOR, x, z) - 1;
            if (oceanFloorY >= minY && oceanFloorY != surfaceY && oceanFloorY != canopyY) {
                enqueuePosition(level, new BlockPos(x, oceanFloorY, z), byType, maxPerQueue);
            }

            int baseSurfaceY = Math.max(surfaceY, canopyY);
            int subsurfaceY = Math.max(minY, baseSurfaceY - random.nextInt(SUBSURFACE_SAMPLE_DEPTH));
            enqueuePosition(level, new BlockPos(x, subsurfaceY, z), byType, maxPerQueue);

            int randomY = minY + random.nextInt(Math.max(1, baseSurfaceY - minY + 1));
            enqueuePosition(level, new BlockPos(x, randomY, z), byType, maxPerQueue);
        }
    }

    private static void enqueuePosition(ServerLevel level, BlockPos pos,
                                        EnumMap<EnvironmentalWorkType, LinkedHashSet<Long>> byType, int maxPerQueue) {
        BlockState state = level.getBlockState(pos);
        BlockTransform transform = SolarApocalypseCoreMod.getCachedBlockTransform(state);
        if (transform == null) {
            return;
        }

        EnvironmentalWorkType workType = classifyWorkType(state);
        LinkedHashSet<Long> queue = byType.computeIfAbsent(workType, ignored -> new LinkedHashSet<>());
        if (queue.size() >= maxPerQueue) {
            return;
        }
        queue.add(pos.asLong());
    }

    private static void processPositionQueues(ServerLevel level, SolarStage stage) {
        EnumMap<EnvironmentalWorkType, LinkedHashSet<Long>> byType = POSITION_QUEUES.get(level.dimension());
        if (byType == null || byType.isEmpty()) {
            return;
        }

        for (EnvironmentalWorkType workType : EnvironmentalWorkType.values()) {
            LinkedHashSet<Long> queue = byType.get(workType);
            if (queue == null || queue.isEmpty()) {
                continue;
            }

            int remaining = executionBudget(stage, workType);
            Iterator<Long> iterator = queue.iterator();
            while (iterator.hasNext() && remaining > 0) {
                BlockPos pos = BlockPos.of(iterator.next());
                iterator.remove();
                if (!level.isLoaded(pos)) {
                    continue;
                }

                BlockState state = level.getBlockState(pos);
                BlockTransform transform = SolarApocalypseCoreMod.getCachedBlockTransform(state);
                if (transform == null) {
                    continue;
                }

                MineCollapseBridge.withSolarSource(MineCollapseBridge.SOURCE_SOLAR_RANDOM_TICK,
                        () -> transform.call(level, pos.getX(), pos.getY(), pos.getZ()));
                remaining--;
            }
        }
    }

    private static EnvironmentalWorkType classifyWorkType(BlockState state) {
        Block block = state.getBlock();
        if (state.is(Blocks.WATER)
                || block instanceof BubbleColumnBlock
                || state.getFluidState().is(FluidTags.WATER)
                || state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
            return EnvironmentalWorkType.WATER;
        }
        if (state.is(BlockTags.ICE)
                || state.is(Blocks.SNOW_BLOCK)
                || block instanceof SnowLayerBlock) {
            return EnvironmentalWorkType.ICE;
        }
        if (state.is(BlockTags.LOGS)
                || state.is(BlockTags.PLANKS)
                || state.is(BlockTags.LEAVES)
                || block instanceof LeavesBlock
                || state.is(BlockTags.WOOL)
                || state.is(BlockTags.WOOL_CARPETS)
                || state.is(SolarModTags.Blocks.SIMPLE_DELETE)) {
            return EnvironmentalWorkType.FIRE;
        }
        if (state.is(SolarModTags.Blocks.COBBLESTONE)
                || state.is(SolarModTags.Blocks.CLAY)
                || state.is(SolarModTags.Blocks.SANDSTONE)
                || state.is(Tags.Blocks.GRAVEL)
                || state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            return EnvironmentalWorkType.STONE;
        }
        return EnvironmentalWorkType.SURFACE;
    }

    private static int executionBudget(SolarStage stage, EnvironmentalWorkType workType) {
        return switch (workType) {
            case WATER -> SolarStageConfig.getWaterSpreadBudget(stage) * 2;
            case ICE -> SolarStageConfig.getStageSpreadBudget(stage) * 2;
            case FIRE -> Math.max(2, SolarStageConfig.getStageSpreadBudget(stage));
            case STAGE6_SURFACE -> Math.max(4,
                    SolarStageConfig.getCollapseSnowStepBudget() * SolarStageConfig.getCollapseSnowSampleCount());
            case STONE -> Math.max(2, SolarStageConfig.getStageSpreadBudget(stage) * 2);
            case SURFACE -> SolarStageConfig.getStageSpreadBudget(stage) * 3;
        };
    }

    private static int stageBudget(SolarStage stage) {
        return SolarStageConfig.getStageSpreadBudget(stage);
    }

    private static int maxQueueSize(SolarStage stage, int playerCount) {
        return Math.max(32, stageBudget(stage) * MAX_QUEUE_MULTIPLIER * Math.max(1, playerCount));
    }

    private static int chunkRadius(SolarStage stage) {
        int radius = 1 + stageBudget(stage) / 2;
        return Math.max(MIN_CHUNK_RADIUS, Math.min(MAX_CHUNK_RADIUS, radius));
    }

    private static long chunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX << 32) ^ (chunkZ & 0xffffffffL);
    }

    private static int unpackChunkX(long chunkKey) {
        return (int) (chunkKey >> 32);
    }

    private static int unpackChunkZ(long chunkKey) {
        return (int) chunkKey;
    }

    private static <T> void trimLinkedSet(LinkedHashSet<T> values, int maxSize) {
        if (values.size() <= maxSize) {
            return;
        }

        Iterator<T> iterator = values.iterator();
        while (values.size() > maxSize && iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    private static void removeOldestChunk(LinkedHashSet<Long> chunks) {
        Iterator<Long> iterator = chunks.iterator();
        if (!iterator.hasNext()) {
            return;
        }
        iterator.next();
        iterator.remove();
    }

    private EnvironmentalTransformScheduler() {}
}
