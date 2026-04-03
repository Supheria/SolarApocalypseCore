package com.supheria.solar_apocalypse_core.procedures.stats;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 饮水中断事件处理器（原 DrinkStopProcedure）。
 */
@Mod.EventBusSubscriber
public class DrinkStopHandler {

    @SubscribeEvent
    public static void onUseItemStop(LivingEntityUseItemEvent.Stop event) {
        if (event != null && event.getEntity() != null) {
            execute(event.getEntity());
        }
    }

    public static void execute(Entity entity) {
        if (entity == null)
            return;
        entity.getPersistentData().putBoolean("RightUse", false);
        entity.getPersistentData().putBoolean("LeftUse", false);
    }
}
