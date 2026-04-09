package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.SolarApocalypseCoreMod;
import com.supheria.solar_apocalypse_core.integration.minecollapse.MineCollapseBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class MineCollapseImmediateFallHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        BlockPos unsupportedPos = event.getPos().above().immutable();
        SolarApocalypseCoreMod.queueServerWork(1, () -> {
            if (!level.isLoaded(unsupportedPos)) {
                return;
            }
            MineCollapseBridge.tryImmediatePlayerBreakResponse(level, unsupportedPos);
        });
    }

    private MineCollapseImmediateFallHandler() {}
}
