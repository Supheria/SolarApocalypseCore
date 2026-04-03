package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 今日时间变量更新处理器（原 TodayCountProcedure）。
 */
@Mod.EventBusSubscriber
public class DayTimeTickHandler {

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            execute(event.level);
        }
    }

    public static void execute(LevelAccessor world) {
        SapModVariables.MapVariables.get(world).Today = Math.floor(world.dayTime() / 24000);
        SapModVariables.MapVariables.get(world).LunarToday = Math.floor((world.dayTime() / 24000) + 1 / 4);
        if (world.dayTime() / 24000 >= 1) {
            SapModVariables.MapVariables.get(world).TodayTime = world.dayTime() - Math.floor(world.dayTime() / 24000) * 24000;
            SapModVariables.MapVariables.get(world).syncData(world);
        } else {
            SapModVariables.MapVariables.get(world).TodayTime = world.dayTime();
            SapModVariables.MapVariables.get(world).syncData(world);
        }
    }
}
