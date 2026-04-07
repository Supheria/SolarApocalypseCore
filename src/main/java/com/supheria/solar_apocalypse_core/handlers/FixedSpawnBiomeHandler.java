package com.supheria.solar_apocalypse_core.handlers;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import java.util.function.Predicate;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;


/**
 * 把主世界公共出生点固定到附近的丛林系生物群系。
 *
 * <p>优先使用世界生成器的 biome source 定位目标群系，
 * 成功后只在局部范围内寻找安全落点；失败时回退到原版出生点逻辑。</p>
 */
@Mod.EventBusSubscriber
public final class FixedSpawnBiomeHandler {
    private static final int SEARCH_RADIUS_BLOCKS = 2048;
    private static final int SEARCH_HORIZONTAL_STEP = 32;
    private static final int SEARCH_VERTICAL_STEP = 64;
    private static final int SAFE_SPAWN_RADIUS = 8;
    private static final Predicate<Holder<Biome>> TARGET_BIOMES = biome -> isTargetBiome(biome, Biomes.JUNGLE)
            || isTargetBiome(biome, Biomes.SPARSE_JUNGLE)
            || isTargetBiome(biome, Biomes.BAMBOO_JUNGLE);

    private FixedSpawnBiomeHandler() {
    }

    @SubscribeEvent
    public static void onCreateSpawnPosition(LevelEvent.CreateSpawnPosition event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) {
            return;
        }

        BlockPos biomeCenter = findNearestTargetBiome(level);
        if (biomeCenter == null) {
            return;
        }

        BlockPos spawnPos = findSafeSpawnNear(level, biomeCenter);
        if (spawnPos == null) {
            return;
        }

        level.setDefaultSpawnPos(spawnPos, 0.0F);
        event.getSettings().setSpawn(spawnPos, 0.0F);
        event.setCanceled(true);
    }

    private static @Nullable BlockPos findNearestTargetBiome(ServerLevel level) {
        BiomeSource biomeSource = level.getChunkSource().getGenerator().getBiomeSource();
        Pair<BlockPos, Holder<Biome>> located = biomeSource.findClosestBiome3d(
                level.getSharedSpawnPos(),
                SEARCH_RADIUS_BLOCKS,
                SEARCH_HORIZONTAL_STEP,
                SEARCH_VERTICAL_STEP,
                TARGET_BIOMES,
                level.getChunkSource().randomState().sampler(),
                level);
        return located != null ? located.getFirst() : null;
    }

    private static boolean isTargetBiome(Holder<Biome> biome, ResourceKey<Biome> key) {
        return biome.is(key);
    }

    private static @Nullable BlockPos findSafeSpawnNear(ServerLevel level, BlockPos center) {
        for (int radius = 0; radius <= SAFE_SPAWN_RADIUS; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                BlockPos north = findSafeSpawnAt(level, center.offset(dx, 0, -radius));
                if (north != null) {
                    return north;
                }

                if (radius == 0) {
                    continue;
                }

                BlockPos south = findSafeSpawnAt(level, center.offset(dx, 0, radius));
                if (south != null) {
                    return south;
                }
            }

            for (int dz = -radius + 1; dz <= radius - 1; dz++) {
                BlockPos west = findSafeSpawnAt(level, center.offset(-radius, 0, dz));
                if (west != null) {
                    return west;
                }

                BlockPos east = findSafeSpawnAt(level, center.offset(radius, 0, dz));
                if (east != null) {
                    return east;
                }
            }
        }

        return null;
    }

    private static @Nullable BlockPos findSafeSpawnAt(ServerLevel level, BlockPos pos) {
        BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
        BlockPos spawnPos = surfacePos.above();
        return isSafeSpawn(level, spawnPos) ? spawnPos : null;
    }

    private static boolean isSafeSpawn(ServerLevel level, BlockPos spawnPos) {
        BlockPos belowPos = spawnPos.below();
        BlockState belowState = level.getBlockState(belowPos);
        BlockState feetState = level.getBlockState(spawnPos);
        BlockState headState = level.getBlockState(spawnPos.above());

        return belowState.blocksMotion()
                && !belowState.getCollisionShape(level, belowPos).isEmpty()
                && level.getFluidState(spawnPos).isEmpty()
                && level.getFluidState(spawnPos.above()).isEmpty()
                && feetState.getCollisionShape(level, spawnPos).isEmpty()
                && headState.getCollisionShape(level, spawnPos.above()).isEmpty();
    }
}
