package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 统一管理主世界怪物、村民与动物的生成来源限制。
 */
@Mod.EventBusSubscriber
public class SpawnRestrictionHandler {
	private static final float NATURAL_ZOMBIE_VILLAGER_CHANCE = 0.25f;

	@SubscribeEvent
	public static void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
		ServerLevel serverLevel = event.getLevel().getLevel();
		Entity entity = event.getEntity();
		MobSpawnType spawnType = event.getSpawnType();

		if (replaceNaturalZombieWithZombieVillager(serverLevel, entity, spawnType)) {
			event.setSpawnCancelled(true);
			return;
		}

		if (shouldBlockOverworldMonster(serverLevel, entity, spawnType)
				|| shouldBlockVillagerNaturalSpawn(entity, spawnType)
				|| shouldBlockAnimalNaturalSpawn(serverLevel, entity, spawnType)) {
			event.setSpawnCancelled(true);
		}
	}

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinLevelEvent event) {
		if (event.getLevel().isClientSide() || !(event.getLevel() instanceof ServerLevel serverLevel)) {
			return;
		}

		if (shouldRemoveOverworldMonsterOnJoin(serverLevel, event.getEntity())) {
			event.getEntity().remove(Entity.RemovalReason.DISCARDED);
			event.setCanceled(true);
		}
	}

	private static boolean replaceNaturalZombieWithZombieVillager(ServerLevel serverLevel, Entity entity, MobSpawnType spawnType) {
		if (serverLevel.dimension() != Level.OVERWORLD
				|| entity.getType() != EntityType.ZOMBIE
				|| !isNaturalSpawn(spawnType)
				|| serverLevel.random.nextFloat() >= NATURAL_ZOMBIE_VILLAGER_CHANCE) {
			return false;
		}

		ZombieVillager zombieVillager = EntityType.ZOMBIE_VILLAGER.create(serverLevel);
		if (zombieVillager == null) {
			return false;
		}

		BlockPos spawnPos = entity.blockPosition();
		zombieVillager.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
		zombieVillager.finalizeSpawn(
				eventLevelAccessor(serverLevel),
				serverLevel.getCurrentDifficultyAt(spawnPos),
				spawnType,
				(SpawnGroupData) null,
				null
		);
		serverLevel.addFreshEntity(zombieVillager);
		return true;
	}

	private static boolean shouldBlockOverworldMonster(ServerLevel serverLevel, Entity entity, MobSpawnType spawnType) {
		SolarStage stage = SolarModVariables.MapVariables.get(serverLevel).getSolarStage();
		return serverLevel.dimension() == Level.OVERWORLD
				&& entity instanceof Monster
				&& !isAllowedOverworldMonster(stage, entity)
				&& !(entity instanceof Zombie)
				&& (isNaturalSpawn(spawnType) || spawnType == MobSpawnType.SPAWNER);
	}

	private static boolean shouldRemoveOverworldMonsterOnJoin(ServerLevel serverLevel, Entity entity) {
		SolarStage stage = SolarModVariables.MapVariables.get(serverLevel).getSolarStage();
		return serverLevel.dimension() == Level.OVERWORLD
				&& entity instanceof Monster
				&& !isAllowedOverworldMonster(stage, entity);
	}

	private static boolean isAllowedOverworldMonster(SolarStage stage, Entity entity) {
		return entity instanceof Zombie || stage == SolarStage.STAGE_6 && entity.getType() == EntityType.ENDERMAN;
	}

	private static boolean shouldBlockVillagerNaturalSpawn(Entity entity, MobSpawnType spawnType) {
		return entity instanceof Villager && isNaturalSpawn(spawnType);
	}

	private static boolean shouldBlockAnimalNaturalSpawn(ServerLevel serverLevel, Entity entity, MobSpawnType spawnType) {
		SolarStage stage = SolarModVariables.MapVariables.get(serverLevel).getSolarStage();
		return stage.isAtLeast(SolarStage.STAGE_3)
				&& isNaturalSpawn(spawnType)
				&& isAnimal(entity);
	}

	private static boolean isNaturalSpawn(MobSpawnType spawnType) {
		return spawnType == MobSpawnType.NATURAL || spawnType == MobSpawnType.CHUNK_GENERATION;
	}

	private static boolean isAnimal(Entity entity) {
		return entity instanceof Animal || entity instanceof WaterAnimal || entity instanceof AmbientCreature;
	}

	private static ServerLevel eventLevelAccessor(ServerLevel serverLevel) {
		return serverLevel;
	}
}
