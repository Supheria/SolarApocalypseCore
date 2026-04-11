package com.supheria.solar_apocalypse_core.handlers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 取消所有幼年 Zombie 系实体的生成，保留成年个体。
 * 包括小僵尸、小溺尸、小尸壳和小僵尸村民。
 */
@Mod.EventBusSubscriber
public class BabyZombieSpawnBanHandler {

	@SubscribeEvent
	public static void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
		if (isBabyZombieFamily(event.getEntity())) {
			event.setSpawnCancelled(true);
		}
	}

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinLevelEvent event) {
		if (event.getLevel().isClientSide()) {
			return;
		}

		if (isBabyZombieFamily(event.getEntity())) {
			event.getEntity().remove(Entity.RemovalReason.DISCARDED);
			event.setCanceled(true);
		}
	}

	private static boolean isBabyZombieFamily(Entity entity) {
		return entity instanceof Zombie zombie && zombie.isBaby();
	}
}
