package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarPhase;
import com.supheria.solar_apocalypse_core.world.SolarPhaseHelper;
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

        SolarPhase targetPhase = SolarPhaseHelper.getPhaseByDayTime(dayTime);
        SolarPhase currentPhase = mapVars.getCurrentPhase();

        if (currentPhase != targetPhase) {
            mapVars.setPhase(targetPhase);
            mapVars.syncData(world);
            applyPhaseRules(world, targetPhase);
        }
    }

    private static void applyPhaseRules(LevelAccessor world, SolarPhase phase) {
        int randomTickingLevel = SolarStageConfig.getRandomTickingLevel(phase.ordinal());
        boolean allowWeather = phase.ordinal() <= 2;
        boolean allowFreeze = phase.ordinal() <= 2;

        world.getLevelData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(randomTickingLevel, world.getServer());
        world.getLevelData().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(allowWeather, world.getServer());
        world.getLevelData().getGameRules().getRule(GameRules.RULE_FREEZE_DAMAGE).set(allowFreeze, world.getServer());
        world.getLevelData().getGameRules().getRule(GameRules.RULE_WATER_SOURCE_CONVERSION).set(allowWeather, world.getServer());

        if (phase.ordinal() >= 3 && phase != SolarPhase.COLLAPSE && world instanceof ServerLevel _level) {
            _level.getServer().getCommands().performPrefixedCommand(
                    new CommandSourceStack(CommandSource.NULL, new Vec3(0, 0, 0), Vec2.ZERO, _level, 4, "",
                    Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
                    "weather clear");
        }

        if (phase == SolarPhase.COLLAPSE) {
            world.getLevelData().setRaining(true);
        }
    }
}
