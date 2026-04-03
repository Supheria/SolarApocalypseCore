
package com.supheria.solar_apocalypse_core.command;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.common.util.FakePlayerFactory;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.commands.Commands;

import com.supheria.solar_apocalypse_core.procedures.commands.SetStage5;

@Mod.EventBusSubscriber
public class Step5Command {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("solar_stage5").requires(s -> s.hasPermission(3)).executes(arguments -> {
			ServerLevel world = arguments.getSource().getLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity entity = arguments.getSource().getEntity();
			if (entity == null)
				entity = FakePlayerFactory.getMinecraft(world);
			Direction direction = entity.getDirection();

			SetStage5.execute(world, x, y, z, entity);
			return 0;
		}));
	}
}
