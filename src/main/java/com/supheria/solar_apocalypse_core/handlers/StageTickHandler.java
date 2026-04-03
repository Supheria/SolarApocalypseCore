package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 太阳阶段切换处理器（原 StartProcedure）。
 * 每 tick 检查 dayTime 是否触发阶段变化，并应用相应游戏规则。
 */
@Mod.EventBusSubscriber
public class StageTickHandler {

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            execute(event.level);
        }
    }

    public static void execute(LevelAccessor world) {
        long dayTime = world.dayTime();
        SapModVariables.MapVariables mapVars = SapModVariables.MapVariables.get(world);

        SolarStage targetPhase = SolarStageHelper.getPhaseByDayTime(dayTime);
        SolarStage currentPhase = mapVars.getCurrentStage();

        if (currentPhase != targetPhase) {
            mapVars.setCurrentStage(targetPhase);
            mapVars.syncData(world);
            applyPhaseRules(world, targetPhase);
        }
    }

    private static void applyPhaseRules(LevelAccessor world, SolarStage phase) {
        int randomTickingLevel = SolarStageConfig.getRandomTickingLevel(phase);
        boolean allowWeather = phase.getEruptionLevel() <= 2;
        boolean allowFreeze = phase.getEruptionLevel() <= 2;

        world.getLevelData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(randomTickingLevel, world.getServer());
        world.getLevelData().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(allowWeather, world.getServer());
        world.getLevelData().getGameRules().getRule(GameRules.RULE_FREEZE_DAMAGE).set(allowFreeze, world.getServer());
        world.getLevelData().getGameRules().getRule(GameRules.RULE_WATER_SOURCE_CONVERSION).set(allowWeather, world.getServer());

        if (phase.getEruptionLevel() >= 3 && !phase.isCollapsePhase() && world instanceof ServerLevel _level) {
            _level.getServer().getCommands().performPrefixedCommand(
                    new CommandSourceStack(CommandSource.NULL, new Vec3(0, 0, 0), Vec2.ZERO, _level, 4, "",
                    Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
                    "weather clear");
        }

        if (phase.isCollapsePhase()) {
            world.getLevelData().setRaining(true);
        }
    }
}
