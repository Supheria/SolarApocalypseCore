
package com.supheria.solar_apocalypse_core.command;

import com.supheria.solar_apocalypse_core.procedures.commands.Set6Procedure;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Step6Command {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("sapstep6").requires(s -> s.hasPermission(3)).executes(arguments -> {
			ServerLevel world = arguments.getSource().getLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null)
				entity = FakePlayerFactory.getMinecraft(world);
			Direction direction = entity.getDirection();

			Set6Procedure.execute(world, entity);
			world.getLevelData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(3, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(true, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_FREEZE_DAMAGE).set(true, world.getServer());
			world.getLevelData().getGameRules().getRule(GameRules.RULE_WATER_SOURCE_CONVERSION).set(true, world.getServer());
			return 0;
		}));
	}
}
