package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.world.HeightZoneHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 在危险高度阻止玩家种植受规则管理的植物。
 */
@Mod.EventBusSubscriber
public class PlantingRestrictionHandler {
    private static final Component DANGER_PLANTING_MESSAGE = Component.literal("危险高度无法种植可种植物");

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        BlockPos placePos = event.getPos().relative(event.getFace());
        Block block = blockItem.getBlock();
        if (!HeightZoneHelper.isManagedPlant(block) || !HeightZoneHelper.isDangerHeight(level, placePos)) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(net.minecraft.world.InteractionResult.FAIL);
        if (!level.isClientSide()) {
            player.displayClientMessage(DANGER_PLANTING_MESSAGE, true);
        }
    }

    private PlantingRestrictionHandler() {}
}
