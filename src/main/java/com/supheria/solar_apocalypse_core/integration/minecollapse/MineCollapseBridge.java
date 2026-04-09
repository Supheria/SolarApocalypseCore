package com.supheria.solar_apocalypse_core.integration.minecollapse;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

public final class MineCollapseBridge {
    public static final String SOURCE_SOLAR_RANDOM_TICK = "SOLAR_RANDOM_TICK";
    public static final String SOURCE_SOLAR_SPREAD = "SOLAR_SPREAD";
    private static final long SOLAR_DIRTY_MARK_COOLDOWN_TICKS = 4L;

    private static final ThreadLocal<String> ACTIVE_SOURCE = new ThreadLocal<>();
    private static final Map<ResourceKey<Level>, Map<Long, Long>> LAST_SOLAR_DIRTY_MARK_TICKS = new ConcurrentHashMap<>();
    private static boolean lookupAttempted;
    private static Class<?> accessClass;
    private static Method pushSourceMethod;
    private static Method popSourceMethod;
    private static Method markDirtyMethod;
    private static Method tryImmediatePlayerBreakResponseMethod;

    public static void withSolarSource(String source, Runnable action) {
        String previous = ACTIVE_SOURCE.get();
        ACTIVE_SOURCE.set(source);
        try {
            action.run();
        } finally {
            if (previous == null) {
                ACTIVE_SOURCE.remove();
            } else {
                ACTIVE_SOURCE.set(previous);
            }
        }
    }

    @Nullable
    public static String getActiveSolarSource() {
        return ACTIVE_SOURCE.get();
    }

    public static boolean setBlockWithCurrentSource(LevelAccessor world, BlockPos pos, BlockState target) {
        BlockState previous = world.getBlockState(pos);
        if (previous.equals(target)) {
            return false;
        }

        String source = ACTIVE_SOURCE.get();
        pushMineCollapseSource(source);
        boolean changed;
        try {
            changed = world.setBlock(pos, target, 3);
        } finally {
            popMineCollapseSource(source);
        }
        if (changed) {
            notifyMineCollapse(world, pos, source, previous, target);
        }
        return changed;
    }

    public static boolean tryImmediatePlayerBreakResponse(ServerLevel level, BlockPos pos) {
        if (!lookupAccessClass() || tryImmediatePlayerBreakResponseMethod == null) {
            return false;
        }

        try {
            Object result = tryImmediatePlayerBreakResponseMethod.invoke(null, level, pos, "PLAYER_ACTION");
            return result instanceof Boolean value && value;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static void notifyMineCollapse(LevelAccessor world, BlockPos pos, String source, BlockState previous, BlockState target) {
        if (!(world instanceof ServerLevel serverLevel) || source == null || !hasCollapseImpact(previous) && !hasCollapseImpact(target) && !target.isAir()) {
            return;
        }
        if (isSolarSource(source) && !shouldMarkDirty(serverLevel, pos)) {
            return;
        }
        if (lookupAccessClass() && markDirtyMethod != null) {
            try {
                markDirtyMethod.invoke(null, serverLevel, pos, source);
            } catch (ReflectiveOperationException ignored) {
            }
        }
    }

    private static boolean hasCollapseImpact(BlockState state) {
        return state.is(BlockTags.SAND)
                || state.is(Tags.Blocks.GRAVEL)
                || state.is(BlockTags.DIRT)
                || state.is(Blocks.COBBLESTONE)
                || state.is(Blocks.MOSSY_COBBLESTONE)
                || state.is(Blocks.COBBLED_DEEPSLATE)
                || state.is(Blocks.CLAY)
                || state.is(Blocks.MUD)
                || state.is(Blocks.FARMLAND)
                || state.is(Blocks.GRASS_BLOCK)
                || state.isAir();
    }

    private static boolean isSolarSource(String source) {
        return SOURCE_SOLAR_RANDOM_TICK.equals(source) || SOURCE_SOLAR_SPREAD.equals(source);
    }

    private static boolean shouldMarkDirty(ServerLevel level, BlockPos pos) {
        long tick = level.getGameTime();
        long chunkKey = chunkKey(pos);
        Map<Long, Long> byChunk = LAST_SOLAR_DIRTY_MARK_TICKS.computeIfAbsent(level.dimension(), ignored -> new ConcurrentHashMap<>());
        Long lastTick = byChunk.get(chunkKey);
        if (lastTick != null && tick - lastTick < SOLAR_DIRTY_MARK_COOLDOWN_TICKS) {
            return false;
        }
        byChunk.put(chunkKey, tick);
        return true;
    }

    private static long chunkKey(BlockPos pos) {
        return (((long) pos.getX() >> 4) << 32) ^ (((long) pos.getZ() >> 4) & 0xffffffffL);
    }

    private static void pushMineCollapseSource(String source) {
        if (source == null || !lookupAccessClass() || pushSourceMethod == null) {
            return;
        }
        try {
            pushSourceMethod.invoke(null, source);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private static void popMineCollapseSource(String source) {
        if (source == null || !lookupAccessClass() || popSourceMethod == null) {
            return;
        }
        try {
            popSourceMethod.invoke(null);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private static boolean lookupAccessClass() {
        if (lookupAttempted) {
            return accessClass != null;
        }
        lookupAttempted = true;
        try {
            accessClass = Class.forName("com.supheria.minecollapsesolarcore.api.CollapseSchedulingAccess");
            pushSourceMethod = accessClass.getMethod("pushActiveSource", String.class);
            popSourceMethod = accessClass.getMethod("popActiveSource");
            markDirtyMethod = accessClass.getMethod("markLandslideRegionDirty", net.minecraft.world.level.Level.class, BlockPos.class, String.class);
            tryImmediatePlayerBreakResponseMethod = accessClass.getMethod("tryImmediatePlayerBreakResponse", Level.class, BlockPos.class, String.class);
            return true;
        } catch (ReflectiveOperationException ignored) {
            accessClass = null;
            pushSourceMethod = null;
            popSourceMethod = null;
            markDirtyMethod = null;
            tryImmediatePlayerBreakResponseMethod = null;
            return false;
        }
    }

    private MineCollapseBridge() {}
}
