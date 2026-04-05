package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
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
        long dayTime = world.dayTime();
        SapModVariables.MapVariables mapVariables = SapModVariables.MapVariables.get(world);
        double dayIndex = SolarStageHelper.getDayIndex(dayTime);

        mapVariables.Today = dayIndex;
        mapVariables.LunarToday = Math.floor(dayIndex + 1 / 4d);
        mapVariables.TodayTime = SolarStageHelper.getTimeOfDay(dayTime);
        mapVariables.syncData(world);
    }
}
