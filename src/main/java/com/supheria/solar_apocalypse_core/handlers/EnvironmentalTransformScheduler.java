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
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
    private static final int BASE_VIEW_DISTANCE = 6;
    private static final int BASE_KEEPALIVE_RADIUS = 1;
    private static final int BASE_VISIBLE_CHUNK_SIDE = 6;
    private static final int BASE_VISIBLE_CHUNK_SAMPLE_LIMIT = 12;
    private static final int INDEX_INTERVAL_TICKS = 20;
    private static final int REFILL_INTERVAL_TICKS = 4;
    private static final int MIN_CHUNK_SAMPLES_PER_TICK = 8;
    private static final int MAX_CHUNK_SAMPLES_PER_PLAYER = 12;
    private static final int POSITIONS_PER_CHUNK_SAMPLE = 4;
    private static final int SUBSURFACE_SAMPLE_DEPTH = 12;
    private static final int CANOPY_UNDER_SAMPLE_DEPTH = 3;
    private static final int MAX_QUEUE_MULTIPLIER = 16;

    private static final Map<ResourceKey<Level>, LinkedHashSet<Long>> CANDIDATE_CHUNKS = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, EnumMap<EnvironmentalWorkType, LinkedHashSet<Long>>> POSITION_QUEUES = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, Long> LAST_INDEX_TICKS = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, Long> LAST_REFILL_TICKS = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> CLIENT_RENDER_DISTANCES = new ConcurrentHashMap<>();

    public static void updateClientRenderDistance(ServerPlayer player, int renderDistance) {
        CLIENT_RENDER_DISTANCES.put(player.getUUID(), Math.max(2, renderDistance));
    }

    @SubscribeEvent
    public static void onPlayerLogout(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        CLIENT_RENDER_DISTANCES.remove(event.getEntity().getUUID());
    }

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
        processPositionQueues(level, players, stage);
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
        int targetSize = Math.max(MIN_INDEXED_CHUNKS_PER_PLAYER * players.size(), totalVisibleChunkArea(level, players));
        keepAlivePlayerChunks(level, players, chunks);
        int refreshCount = Math.max(players.size() * CHUNK_REFRESHES_PER_PLAYER, targetSize / 4);
        int visibleRefreshCount = Math.max(totalVisibleChunkSamples(level, players), targetSize / 3);
        int attempts = Math.max(targetSize, refreshCount + visibleRefreshCount) * 3;
        while (attempts-- > 0 && (chunks.size() < targetSize || refreshCount > 0 || visibleRefreshCount > 0)) {
            ServerPlayer player = players.get(random.nextInt(players.size()));
            boolean useVisibleSample = visibleRefreshCount > 0 && (refreshCount <= 0 || random.nextBoolean());
            long candidate = useVisibleSample
                    ? sampleVisibleChunk(player, level, random)
                    : sampleNearChunk(player, keepAliveRadius(level, player), random);
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

            if (useVisibleSample) {
                visibleRefreshCount--;
            } else if (refreshCount > 0) {
                refreshCount--;
            }
        }

        trimLinkedSet(chunks, targetSize * 2);
    }

    private static void keepAlivePlayerChunks(ServerLevel level, List<ServerPlayer> players, LinkedHashSet<Long> chunks) {
        for (ServerPlayer player : players) {
            int keepAliveRadius = keepAliveRadius(level, player);
            int baseChunkX = player.chunkPosition().x;
            int baseChunkZ = player.chunkPosition().z;
            for (int dx = -keepAliveRadius; dx <= keepAliveRadius; dx++) {
                for (int dz = -keepAliveRadius; dz <= keepAliveRadius; dz++) {
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

    private static long sampleVisibleChunk(ServerPlayer player, ServerLevel level, RandomSource random) {
        int side = visibleChunkSide(level, player);
        int minOffset = -side / 2;
        int chunkX = player.chunkPosition().x + minOffset + random.nextInt(side);
        int chunkZ = player.chunkPosition().z + minOffset + random.nextInt(side);
        return chunkKey(chunkX, chunkZ);
    }

    private static void refillPositionQueues(ServerLevel level, int playerCount, SolarStage stage, RandomSource random) {
        LinkedHashSet<Long> chunks = CANDIDATE_CHUNKS.get(level.dimension());
        if (chunks == null || chunks.isEmpty()) {
            return;
        }

        EnumMap<EnvironmentalWorkType, LinkedHashSet<Long>> byType = POSITION_QUEUES.computeIfAbsent(level.dimension(), ignored -> new EnumMap<>(EnvironmentalWorkType.class));
        int maxPerQueue = maxQueueSize(stage, playerCount);
        int targetChunkSamples = Math.max(MIN_CHUNK_SAMPLES_PER_TICK, totalVisibleChunkSamples(level, level.players()));
        int cappedChunkSamples = Math.max(MIN_CHUNK_SAMPLES_PER_TICK, playerCount * MAX_CHUNK_SAMPLES_PER_PLAYER);
        int chunkSamples = Math.min(chunks.size(), Math.min(targetChunkSamples, cappedChunkSamples));
        ArrayList<Long> chunkList = new ArrayList<>(chunks);
        for (int i = 0; i < chunkSamples; i++) {
            long chunkKey = chunkList.get(Math.max(0, chunkList.size() - 1 - random.nextInt(chunkList.size())));
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

            ArrayList<BlockPos> samplePositions = new ArrayList<>(5);

            if (canopyY >= minY) {
                samplePositions.add(new BlockPos(x, canopyY, z));

                int underCanopyY = Math.max(minY, canopyY - random.nextInt(CANOPY_UNDER_SAMPLE_DEPTH));
                if (underCanopyY != canopyY) {
                    samplePositions.add(new BlockPos(x, underCanopyY, z));
                }
            }

            if (surfaceY >= minY && surfaceY != canopyY) {
                samplePositions.add(new BlockPos(x, surfaceY, z));
            }

            int oceanFloorY = level.getHeight(Heightmap.Types.OCEAN_FLOOR, x, z) - 1;
            if (oceanFloorY >= minY && oceanFloorY != surfaceY && oceanFloorY != canopyY) {
                samplePositions.add(new BlockPos(x, oceanFloorY, z));
            }

            int baseSurfaceY = Math.max(surfaceY, canopyY);
            int subsurfaceY = Math.max(minY, baseSurfaceY - random.nextInt(SUBSURFACE_SAMPLE_DEPTH));
            samplePositions.add(new BlockPos(x, subsurfaceY, z));

            int randomY = minY + random.nextInt(Math.max(1, baseSurfaceY - minY + 1));
            samplePositions.add(new BlockPos(x, randomY, z));

            samplePositions.sort(Comparator.comparingInt(BlockPos::getY));
            for (BlockPos samplePos : samplePositions) {
                enqueuePosition(level, samplePos, byType, maxPerQueue);
            }
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
        if (workType == EnvironmentalWorkType.WATER && shouldSkipDeepWaterSample(level, pos, state)) {
            return;
        }
        LinkedHashSet<Long> queue = byType.computeIfAbsent(workType, ignored -> new LinkedHashSet<>());
        if (queue.size() >= maxPerQueue) {
            return;
        }
        queue.remove(pos.asLong());
        queue.add(pos.asLong());
    }

    private static boolean shouldSkipDeepWaterSample(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
            return false;
        }
        if (state.getBlock() instanceof BubbleColumnBlock) {
            return false;
        }
        return state.getFluidState().is(FluidTags.WATER) && !com.supheria.solar_apocalypse_core.transforms.util.BlockSpreadUtils.isSurfaceWater(level, pos);
    }

    private static void processPositionQueues(ServerLevel level, List<ServerPlayer> players, SolarStage stage) {
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
            ArrayList<Long> snapshot = new ArrayList<>(queue);
            for (int index = snapshot.size() - 1; index >= 0 && remaining > 0; index--) {
                long posKey = snapshot.get(index);
                if (!queue.remove(posKey)) {
                    continue;
                }
                BlockPos pos = BlockPos.of(posKey);
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

    private static int keepAliveRadius(ServerLevel level, ServerPlayer player) {
        int viewDistance = effectiveViewDistance(level, player);
        return Math.max(1, Math.round((float) viewDistance / BASE_VIEW_DISTANCE * BASE_KEEPALIVE_RADIUS));
    }

    private static int visibleChunkSide(ServerLevel level, ServerPlayer player) {
        int viewDistance = effectiveViewDistance(level, player);
        return Math.max(1, Math.round((float) viewDistance / BASE_VIEW_DISTANCE * BASE_VISIBLE_CHUNK_SIDE));
    }

    private static int visibleChunkSampleLimit(ServerLevel level, ServerPlayer player) {
        int visibleChunks = visibleChunkSide(level, player) * visibleChunkSide(level, player);
        int scaledBaseline = Math.max(BASE_VISIBLE_CHUNK_SAMPLE_LIMIT,
                Math.round((float) effectiveViewDistance(level, player) / BASE_VIEW_DISTANCE * BASE_VISIBLE_CHUNK_SAMPLE_LIMIT));
        return Math.min(visibleChunks, Math.max(1, Math.max(scaledBaseline, (visibleChunks + 2) / 3)));
    }

    private static int totalVisibleChunkArea(ServerLevel level, List<ServerPlayer> players) {
        int total = 0;
        for (ServerPlayer player : players) {
            int side = visibleChunkSide(level, player);
            total += side * side;
        }
        return total;
    }

    private static int totalVisibleChunkSamples(ServerLevel level, List<ServerPlayer> players) {
        int total = 0;
        for (ServerPlayer player : players) {
            total += visibleChunkSampleLimit(level, player);
        }
        return total;
    }

    private static int effectiveViewDistance(ServerLevel level, ServerPlayer player) {
        int serverViewDistance = Math.max(2, level.getServer().getPlayerList().getViewDistance());
        int clientViewDistance = CLIENT_RENDER_DISTANCES.getOrDefault(player.getUUID(), serverViewDistance);
        return Math.max(2, Math.min(serverViewDistance, clientViewDistance));
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
