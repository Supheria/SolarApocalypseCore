package com.supheria.solar_apocalypse_core.procedures.stats;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 饮水完成事件处理器（原 DrinkFinishProcedure）。
 */
@Mod.EventBusSubscriber
public class DrinkFinishHandler {

    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (event != null && event.getEntity() != null) {
            execute(event.getEntity());
        }
    }

    public static void execute(Entity entity) {
        if (entity == null)
            return;
        if (entity.getPersistentData().getBoolean("RightUse") == true || entity.getPersistentData().getBoolean("LeftUse") == true) {
            entity.getPersistentData().putBoolean("RightUse", false);
            entity.getPersistentData().putBoolean("LeftUse", false);
            entity.getPersistentData().putDouble("WaterStack", (entity.getPersistentData().getDouble("WaterStack") + 1200));
        }
    }
}
