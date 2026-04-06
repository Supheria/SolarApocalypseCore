package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.SolarApocalypseCoreMod;
import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;
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
 * 太阳阶段推进的服务端入口。
 *
 * <p>该处理器每个世界 tick 读取当前 {@code dayTime}，据此推导目标太阳阶段，
 * 再把变化写回全局 {@link SolarModVariables.MapVariables}，并同步应用阶段级 gamerule 与天气约束。
 */
@Mod.EventBusSubscriber
public class StageTickHandler {

    private static final int RANDOM_TICK_RAMP_INTERVAL = 20;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            execute(event.level);
        }
    }

    /**
     * 用当前世界时间驱动太阳阶段切换。
     * 只有阶段真的发生变化时，才会写入全局状态并触发后续同步与规则刷新。
     */
    public static void execute(LevelAccessor world) {
        long dayTime = world.dayTime();
        SolarModVariables.MapVariables mapVars = SolarModVariables.MapVariables.get(world);

        SolarStage targetPhase = SolarStageHelper.getPhaseByDayTime(dayTime);
        SolarStage currentPhase = mapVars.getCurrentStage();

        if (currentPhase != targetPhase) {
            mapVars.setCurrentStage(targetPhase);
            mapVars.syncData(world);
            applyPhaseRules(world, targetPhase);
        }
    }

    /**
     * 把阶段语义映射为全局世界规则。
     *
     * <p>这里调整的是整张世界的环境行为，而不是单个方块的转换链：
     * random tick 频率、天气循环、冻伤、无限水源等都会随阶段统一切换。
     */
    public static void applyPhaseRules(LevelAccessor world, SolarStage phase) {
        int targetRandomTickingLevel = SolarStageConfig.getRandomTickingLevel(phase);
        boolean allowWeather = phase.isBefore(SolarStage.STAGE_2);
        boolean allowFreeze = phase.isAtLeast(SolarStage.STAGE_6);

        setRandomTickingLevelGradually(world, targetRandomTickingLevel);
        world.getLevelData().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(allowWeather, world.getServer());
        world.getLevelData().getGameRules().getRule(GameRules.RULE_FREEZE_DAMAGE).set(allowFreeze, world.getServer());

        // 第二阶段起强制清空天气，并关闭天气循环，确保喷发期维持稳定的高温晴空环境。
        if (phase.isEruptionPhase() && phase.isAtLeast(SolarStage.STAGE_2) && world instanceof ServerLevel _level) {
            _level.getServer().getCommands().performPrefixedCommand(
                    new CommandSourceStack(CommandSource.NULL, new Vec3(0, 0, 0), Vec2.ZERO, _level, 4, "",
                    Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
                    "weather clear");
        }

        // 第六阶段转入坍缺后强制降水，与冻伤和水源恢复规则共同塑造恒冬环境。
        if (phase.isCollapsePhase()) {
            world.getLevelData().setRaining(true);
        }
    }

    private static void setRandomTickingLevelGradually(LevelAccessor world, int targetLevel) {
        int currentLevel = world.getLevelData().getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        if (currentLevel == targetLevel) {
            return;
        }

        int nextLevel = currentLevel < targetLevel ? currentLevel + 1 : targetLevel;
        world.getLevelData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(nextLevel, world.getServer());
        if (nextLevel < targetLevel) {
            SolarApocalypseCoreMod.queueServerWork(RANDOM_TICK_RAMP_INTERVAL,
                    () -> setRandomTickingLevelGradually(world, targetLevel));
        }
    }
}
