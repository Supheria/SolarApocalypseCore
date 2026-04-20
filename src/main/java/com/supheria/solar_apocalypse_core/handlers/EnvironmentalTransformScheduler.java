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
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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

    private static final int BASE_VIEW_DISTANCE = 6;
    private static final int VISIBLE_TRANSFORMS_PER_TICK = 15;
    private static final int VISIBLE_CHUNK_SAMPLE_LIMIT = 12;
    private static final int POSITIONS_PER_CHUNK_SAMPLE = 4;
    private static final int SUBSURFACE_SAMPLE_DEPTH = 12;
    private static final int CANOPY_UNDER_SAMPLE_DEPTH = 3;
    private static final int MAX_QUEUE_MULTIPLIER = 16;

    private static final Map<ResourceKey<Level>, LinkedHashSet<Long>> VISIBLE_CHUNKS = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, EnumMap<EnvironmentalWorkType, LinkedHashSet<Long>>> POSITION_QUEUES = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> CLIENT_RENDER_DISTANCES = new ConcurrentHashMap<>();

    public enum ChunkZone {
        VISIBLE,
        NONE
    }

    public static void updateClientRenderDistance(ServerPlayer player, int renderDistance) {
        CLIENT_RENDER_DISTANCES.put(player.getUUID(), Math.max(2, renderDistance));
        refreshVisibleChunks(player.serverLevel());
    }

    @SubscribeEvent
    public static void onPlayerLogout(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        CLIENT_RENDER_DISTANCES.remove(event.getEntity().getUUID());
        if (event.getEntity() instanceof ServerPlayer player) {
            refreshVisibleChunks(player.serverLevel());
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            refreshVisibleChunks(player.serverLevel());
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            refreshVisibleChunks(player.serverLevel());
        }
    }

    @SubscribeEvent
    public static void onPlayerEnteringSection(EntityEvent.EnteringSection event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !event.didChunkChange()) {
            return;
        }
        refreshVisibleChunks(player.serverLevel());
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) {
            return;
        }

        if (level.dimension() != Level.OVERWORLD) {
            clearDimension(level.dimension());
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
        if (!VISIBLE_CHUNKS.containsKey(level.dimension())) {
            refreshPlayerChunkZones(level, players);
        }
        refillPositionQueues(level, players, random);
        processPositionQueues(level);
    }

    private static void clearDimension(ResourceKey<Level> dimension) {
        VISIBLE_CHUNKS.remove(dimension);
        POSITION_QUEUES.remove(dimension);
    }

    public static ChunkZone getChunkZone(ServerLevel level, BlockPos pos) {
        long key = chunkKey(pos.getX() >> 4, pos.getZ() >> 4);
        LinkedHashSet<Long> visibleChunks = VISIBLE_CHUNKS.get(level.dimension());
        if (visibleChunks != null && visibleChunks.contains(key)) {
            return ChunkZone.VISIBLE;
        }
        return ChunkZone.NONE;
    }

    private static void refreshPlayerChunkZones(ServerLevel level, List<ServerPlayer> players) {
        LinkedHashSet<Long> visibleChunks = new LinkedHashSet<>();
        for (ServerPlayer player : players) {
            int visibleRadius = visibleChunkRadius(level, player);
            addChunkSquare(level, visibleChunks, player.chunkPosition().x, player.chunkPosition().z, visibleRadius);
        }

        VISIBLE_CHUNKS.put(level.dimension(), visibleChunks);
    }

    private static void refreshVisibleChunks(ServerLevel level) {
        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) {
            clearDimension(level.dimension());
            return;
        }
        refreshPlayerChunkZones(level, players);
    }

    private static void addChunkSquare(ServerLevel level, LinkedHashSet<Long> chunks, int centerChunkX, int centerChunkZ, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int chunkX = centerChunkX + dx;
                int chunkZ = centerChunkZ + dz;
                if (!level.hasChunk(chunkX, chunkZ)) {
                    continue;
                }
                chunks.add(chunkKey(chunkX, chunkZ));
            }
        }
    }

    private static void refillPositionQueues(ServerLevel level, List<ServerPlayer> players, RandomSource random) {
        EnumMap<EnvironmentalWorkType, LinkedHashSet<Long>> byType = POSITION_QUEUES.computeIfAbsent(level.dimension(), ignored -> new EnumMap<>(EnvironmentalWorkType.class));
        int maxPerQueue = maxQueueSize(players.size());
        sampleChunkZone(level, VISIBLE_CHUNKS.get(level.dimension()), VISIBLE_CHUNK_SAMPLE_LIMIT, random, byType, maxPerQueue);
        for (EnvironmentalWorkType workType : EnvironmentalWorkType.values()) {
            trimLinkedSet(byType.computeIfAbsent(workType, ignored -> new LinkedHashSet<>()), maxPerQueue);
        }
    }

    private static void sampleChunkZone(ServerLevel level, LinkedHashSet<Long> chunks, int sampleLimit, RandomSource random,
                                        EnumMap<EnvironmentalWorkType, LinkedHashSet<Long>> byType, int maxPerQueue) {
        if (chunks == null || chunks.isEmpty()) {
            return;
        }
        int chunkSamples = Math.min(chunks.size(), sampleLimit);
        ArrayList<Long> chunkList = new ArrayList<>(chunks);
        for (int i = 0; i < chunkSamples; i++) {
            long key = chunkList.get(random.nextInt(chunkList.size()));
            int chunkX = unpackChunkX(key);
            int chunkZ = unpackChunkZ(key);
            if (!level.hasChunk(chunkX, chunkZ)) {
                continue;
            }
            enqueueChunkSamples(level, chunkX, chunkZ, random, byType, maxPerQueue);
        }
    }

    private static void processPositionQueues(ServerLevel level) {
        EnumMap<EnvironmentalWorkType, LinkedHashSet<Long>> byType = POSITION_QUEUES.get(level.dimension());
        if (byType == null || byType.isEmpty()) {
            return;
        }
        processZoneQueues(level, byType, ChunkZone.VISIBLE, VISIBLE_TRANSFORMS_PER_TICK);
    }

    private static void processZoneQueues(ServerLevel level, EnumMap<EnvironmentalWorkType, LinkedHashSet<Long>> byType,
                                          ChunkZone zone, int budget) {
        int remaining = budget;
        for (EnvironmentalWorkType workType : EnvironmentalWorkType.values()) {
            if (remaining <= 0) {
                return;
            }
            LinkedHashSet<Long> queue = byType.get(workType);
            if (queue == null || queue.isEmpty()) {
                continue;
            }

            ArrayList<Long> snapshot = new ArrayList<>(queue);
            for (int index = snapshot.size() - 1; index >= 0 && remaining > 0; index--) {
                long posKey = snapshot.get(index);
                BlockPos pos = BlockPos.of(posKey);
                if (getChunkZone(level, pos) != zone) {
                    continue;
                }
                if (!queue.remove(posKey)) {
                    continue;
                }
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

    private static int visibleChunkRadius(ServerLevel level, ServerPlayer player) {
        return effectiveViewDistance(level, player);
    }

    private static int effectiveViewDistance(ServerLevel level, ServerPlayer player) {
        int serverViewDistance = Math.max(2, level.getServer().getPlayerList().getViewDistance());
        int clientViewDistance = CLIENT_RENDER_DISTANCES.getOrDefault(player.getUUID(), serverViewDistance);
        return Math.max(2, Math.min(serverViewDistance, clientViewDistance));
    }

    private static int maxQueueSize(int playerCount) {
        return Math.max(64, VISIBLE_TRANSFORMS_PER_TICK * MAX_QUEUE_MULTIPLIER * Math.max(1, playerCount));
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
                || state.is(BlockTags.WOOL_CARPETS)) {
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

    private EnvironmentalTransformScheduler() {}
}
