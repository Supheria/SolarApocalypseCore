package com.supheria.solar_apocalypse_core.procedures.stats;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class DrinkFinishProcedure {
    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (event != null && event.getEntity() != null) {
            execute(event, event.getEntity());
        }
    }

    public static void execute(Entity entity) {
        execute(null, entity);
    }

    private static void execute(@Nullable Event event, Entity entity) {
        if (entity == null)
            return;
        if (entity.getPersistentData().getBoolean("RightUse") == true || entity.getPersistentData().getBoolean("LeftUse") == true) {
            entity.getPersistentData().putBoolean("RightUse", false);
            entity.getPersistentData().putBoolean("LeftUse", false);
            entity.getPersistentData().putDouble("WaterStack", (entity.getPersistentData().getDouble("WaterStack") + 1200));
        }
    }
}
