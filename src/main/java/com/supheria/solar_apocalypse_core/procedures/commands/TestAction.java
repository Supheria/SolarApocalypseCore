package com.supheria.solar_apocalypse_core.procedures.commands;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public class TestAction {

    public static void execute(LevelAccessor world, Entity entity) {
        if (entity == null)
            return;
        if (entity instanceof Player _player && !_player.level().isClientSide())
            _player.displayClientMessage(Component.literal(("Step:" + SapModVariables.MapVariables.get(world).SolarFlare + " / " + "Today Time:" + SapModVariables.MapVariables.get(world).TodayTime)), false);
        if (entity instanceof Player _player && !_player.level().isClientSide())
            _player.displayClientMessage(Component.literal(("FireStack" + entity.getPersistentData().getDouble("SapStack"))), false);
        if (entity instanceof Player _player && !_player.level().isClientSide())
            _player.displayClientMessage(Component.literal(("WaterStack" + entity.getPersistentData().getDouble("WaterStack"))), false);
    }
}
