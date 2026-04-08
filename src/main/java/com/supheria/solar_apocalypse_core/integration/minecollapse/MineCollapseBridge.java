package com.supheria.solar_apocalypse_core.integration.minecollapse;

import java.lang.reflect.Method;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

public final class MineCollapseBridge {
    public static final String SOURCE_SOLAR_RANDOM_TICK = "SOLAR_RANDOM_TICK";
    public static final String SOURCE_SOLAR_SPREAD = "SOLAR_SPREAD";

    private static final ThreadLocal<String> ACTIVE_SOURCE = new ThreadLocal<>();
    private static boolean lookupAttempted;
    private static Class<?> accessClass;
    private static Method pushSourceMethod;
    private static Method popSourceMethod;
    private static Method markDirtyMethod;

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

    private static void notifyMineCollapse(LevelAccessor world, BlockPos pos, String source, BlockState previous, BlockState target) {
        if (!(world instanceof ServerLevel serverLevel) || source == null || !hasCollapseImpact(previous) && !hasCollapseImpact(target) && !target.isAir()) {
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
            accessClass = Class.forName("net.zerodind.minecollapsesolarcore.api.CollapseSchedulingAccess");
            pushSourceMethod = accessClass.getMethod("pushActiveSource", String.class);
            popSourceMethod = accessClass.getMethod("popActiveSource");
            markDirtyMethod = accessClass.getMethod("markLandslideRegionDirty", net.minecraft.world.level.Level.class, BlockPos.class, String.class);
            return true;
        } catch (ReflectiveOperationException ignored) {
            accessClass = null;
            return false;
        }
    }

    private MineCollapseBridge() {}
}
