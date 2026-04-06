package com.supheria.solar_apocalypse_core.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.handlers.StageTickHandler;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;

@Mod.EventBusSubscriber
public class SolarCommand {

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("solar")
                .then(Commands.literal("stage")
                        .then(Commands.literal("set").requires(source -> source.hasPermission(3))
                                .then(Commands.argument("stage", IntegerArgumentType.integer(1, 6))
                                        .executes(context -> setStage(
                                                context.getSource(),
                                                getStageByNumber(IntegerArgumentType.getInteger(context, "stage"))))))
                        .then(Commands.literal("current")
                                .executes(context -> showCurrentStage(context.getSource())))));
    }

    private static int setStage(CommandSourceStack source, SolarStage stage) {
        MinecraftServer server = source.getServer();
        long targetDayTime = getStageStartTime(stage);

        server.getCommands().performPrefixedCommand(source.withSuppressedOutput(), "time set " + targetDayTime);

        SolarModVariables.MapVariables mapVariables = SolarModVariables.MapVariables.get(source.getLevel());
        mapVariables.setCurrentStage(stage);
        mapVariables.currentDay = targetDayTime / 24000d;
        mapVariables.currentLunarDay = Math.floor(mapVariables.currentDay + 0.25d);
        mapVariables.currentTimeOfDay = 0;
        mapVariables.syncData(source.getLevel());

        for (ServerLevel level : server.getAllLevels()) {
            StageTickHandler.applyPhaseRules(level, stage);
        }

        source.sendSuccess(() -> Component.translatable(
                "message.solar_apocalypse_core.command.set_stage",
                stage.getDisplayName(),
                targetDayTime / 24000L), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int showCurrentStage(CommandSourceStack source) {
        SolarModVariables.MapVariables mapVariables = SolarModVariables.MapVariables.get(source.getLevel());
        source.sendSuccess(() -> Component.translatable(
                "message.solar_apocalypse_core.command.current",
                mapVariables.getCurrentStage().getDisplayName(),
                formatNumber(mapVariables.currentDay),
                formatNumber(mapVariables.currentTimeOfDay)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static long getStageStartTime(SolarStage stage) {
        return switch (stage) {
            case STAGE_1, NONE -> 0L;
            case STAGE_2 -> SolarStageConfig.getStage2StartTime();
            case STAGE_3 -> SolarStageConfig.getStage3StartTime();
            case STAGE_4 -> SolarStageConfig.getStage4StartTime();
            case STAGE_5 -> SolarStageConfig.getStage5StartTime();
            case STAGE_6 -> SolarStageConfig.getStage6StartTime();
        };
    }

    private static SolarStage getStageByNumber(int stageNumber) {
        return switch (stageNumber) {
            case 1 -> SolarStage.STAGE_1;
            case 2 -> SolarStage.STAGE_2;
            case 3 -> SolarStage.STAGE_3;
            case 4 -> SolarStage.STAGE_4;
            case 5 -> SolarStage.STAGE_5;
            case 6 -> SolarStage.STAGE_6;
            default -> throw new IllegalArgumentException("Unsupported solar stage: " + stageNumber);
        };
    }

    private static String formatNumber(double value) {
        if (value == Math.rint(value)) {
            return Long.toString((long) value);
        }
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
