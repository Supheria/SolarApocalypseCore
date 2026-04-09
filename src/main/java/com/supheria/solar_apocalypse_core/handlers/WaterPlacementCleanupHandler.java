package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.init.SolarModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 玩家放置水后，清理附近被蒸发水体留下的假空气占位。
 */
@Mod.EventBusSubscriber
public final class WaterPlacementCleanupHandler {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        Item item = event.getItemStack().getItem();
        boolean isWaterBucket = item == Items.WATER_BUCKET;
        boolean isBlockItem = item instanceof BlockItem;
        if (!isWaterBucket && !isBlockItem) {
            return;
        }

        BlockPos targetPos = resolvePlacementTarget(level, event.getPos(), event.getFace());
        if (targetPos == null) {
            return;
        }

        clearPlacementTargetIfEvaporated(level, targetPos);
        if (isWaterBucket) {
            clearNearbyEvaporatedVoid(level, targetPos);
        }
    }

    private static BlockPos resolvePlacementTarget(ServerLevel level, BlockPos clickedPos, net.minecraft.core.Direction face) {
        BlockState clickedState = level.getBlockState(clickedPos);
        if (clickedState.is(SolarModBlocks.EVAPORATED_VOID.get())) {
            return clickedPos;
        }

        BlockPos adjacentPos = clickedPos.relative(face);
        if (level.getBlockState(adjacentPos).is(SolarModBlocks.EVAPORATED_VOID.get())) {
            return adjacentPos;
        }

        return clickedState.canBeReplaced() ? clickedPos : adjacentPos;
    }

    private static void clearPlacementTargetIfEvaporated(ServerLevel level, BlockPos targetPos) {
        if (level.getBlockState(targetPos).is(SolarModBlocks.EVAPORATED_VOID.get())) {
            level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private static void clearNearbyEvaporatedVoid(ServerLevel level, BlockPos center) {
        int cleanupRadius = Math.max(1, SolarStageConfig.getEvaporatedVoidWaterCleanupRadius());
        int cleanupRadiusSqr = cleanupRadius * cleanupRadius;
        BlockState evaporatedVoid = SolarModBlocks.EVAPORATED_VOID.get().defaultBlockState();
        for (int dx = -cleanupRadius; dx <= cleanupRadius; dx++) {
            for (int dy = -cleanupRadius; dy <= cleanupRadius; dy++) {
                for (int dz = -cleanupRadius; dz <= cleanupRadius; dz++) {
                    if (dx * dx + dy * dy + dz * dz > cleanupRadiusSqr) {
                        continue;
                    }

                    BlockPos pos = center.offset(dx, dy, dz);
                    if (!level.isLoaded(pos)) {
                        continue;
                    }
                    if (!level.getBlockState(pos).equals(evaporatedVoid)) {
                        continue;
                    }

                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    private WaterPlacementCleanupHandler() {}
}
