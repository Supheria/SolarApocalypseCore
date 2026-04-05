package com.supheria.solar_apocalypse_core.procedures.commands;

import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class SetStage4 {

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null)
            return;
        SolarModVariables.MapVariables.get(world).setCurrentStage(SolarStage.STAGE_4);
        SolarModVariables.MapVariables.get(world).syncData(world);
        if (entity instanceof Player _player && !_player.level().isClientSide())
            _player.displayClientMessage(Component.literal(("Step:" + SolarModVariables.MapVariables.get(world).getCurrentStage().getDisplayName().getString())), false);
        if (world instanceof ServerLevel _level)
            _level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(), "time set 21d");
    }
}
