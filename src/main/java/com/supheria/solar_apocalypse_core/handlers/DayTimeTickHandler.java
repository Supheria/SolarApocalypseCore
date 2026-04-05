package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 今日时间变量更新处理器（原 currentDayCountProcedure）。
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
        SolarModVariables.MapVariables mapVariables = SolarModVariables.MapVariables.get(world);
        double dayIndex = SolarStageHelper.getDayIndex(dayTime);

        mapVariables.currentDay = dayIndex;
        mapVariables.currentLunarDay = Math.floor(dayIndex + 1 / 4d);
        mapVariables.currentTimeOfDay = SolarStageHelper.getTimeOfDay(dayTime);
        mapVariables.syncData(world);
    }
}
