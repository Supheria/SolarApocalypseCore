package com.supheria.solar_apocalypse_core.procedures.stats;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.GameType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.TagKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.init.SapModMobEffects;
import com.supheria.solar_apocalypse_core.init.SapModItems;

@Mod.EventBusSubscriber
public class SapHotProcedure {
	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingTickEvent event) {
		execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity.getPersistentData().getDouble("SapStack") >= 200 && entity.getPersistentData().getDouble("WaterStack") <= 0
				&& (entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(SapModMobEffects.DEHYDRATION.get()) ? _livEnt.getEffect(SapModMobEffects.DEHYDRATION.get()).getDuration() : 0) < entity.getPersistentData().getDouble("SapStack")) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(SapModMobEffects.DEHYDRATION.get(), (int) entity.getPersistentData().getDouble("SapStack"), 0));
		}
		if (entity.getPersistentData().getDouble("SapStack") > 1200) {
			entity.getPersistentData().putDouble("SapStack", 1200);
		}
		if (entity.getPersistentData().getDouble("SapStack") < 0) {
			entity.getPersistentData().putDouble("SapStack", 0);
		}
		if (entity.getPersistentData().getDouble("WaterStack") > 6000) {
			entity.getPersistentData().putDouble("WaterStack", 6000);
		}
		if (entity.getPersistentData().getDouble("WaterStack") < 0) {
			entity.getPersistentData().putDouble("WaterStack", 0);
		}
		if (entity.getPersistentData().getDouble("WaterStack") > 0) {
			entity.getPersistentData().putDouble("WaterStack", (entity.getPersistentData().getDouble("WaterStack") - 1));
			entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") - 1));
		}
		if (new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
				}
				return false;
			}
		}.checkGamemode(entity) || new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR;
				}
				return false;
			}
		}.checkGamemode(entity)) {
			entity.getPersistentData().putDouble("SapStack", 0);
		}
		if (SapModVariables.MapVariables.get(world).SolarFlare == 1 && !(new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
				}
				return false;
			}
		}.checkGamemode(entity) && new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR;
				}
				return false;
			}
		}.checkGamemode(entity)) && world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld")))
				&& !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450) && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z)) && !world.getLevelData().isRaining()
				&& !entity.isInWaterRainOrBubble() && !((entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == SapModItems.UV_UMBRELLA.get())
				&& !((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == SapModItems.UV_UMBRELLA.get())) {
			if (entity.getPersistentData().getDouble("SapStack") < 1200 && entity.getPersistentData().getDouble("WaterStack") <= 0) {
				entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") + 1));
			}
		}
		if (SapModVariables.MapVariables.get(world).SolarFlare == 1
				&& (SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450 || !world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z)) || world.getLevelData().isRaining()
				|| entity.isInWaterRainOrBubble() || (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == SapModItems.UV_UMBRELLA.get()
				|| (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == SapModItems.UV_UMBRELLA.get()
				|| !world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"))))) {
			if (entity.getPersistentData().getDouble("SapStack") > 0) {
				entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") - 1));
			}
		}
		if (SapModVariables.MapVariables.get(world).SolarFlare == 2 && !(new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
				}
				return false;
			}
		}.checkGamemode(entity) && new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR;
				}
				return false;
			}
		}.checkGamemode(entity)) && world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"))) && !world.getLevelData().isRaining() && !entity.isInWaterRainOrBubble()
				&& !((entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == SapModItems.UV_UMBRELLA.get())
				&& !((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == SapModItems.UV_UMBRELLA.get())) {
			if (!(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450) && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))) {
				entity.setSecondsOnFire(1);
				entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.ON_FIRE)), (float) (world.dayTime() / 48000));
			}
			if (y >= 63) {
				if (entity.getPersistentData().getDouble("SapStack") < 1200 && entity.getPersistentData().getDouble("WaterStack") <= 0) {
					entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") + 1));
				}
			}
		}
		if (SapModVariables.MapVariables.get(world).SolarFlare == 2 && (!world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
				&& (y < 63 || world.getLevelData().isRaining() || entity.isInWaterRainOrBubble() || (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == SapModItems.UV_UMBRELLA.get()
				|| (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == SapModItems.UV_UMBRELLA.get())
				|| !world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"))))) {
			if (entity.getPersistentData().getDouble("SapStack") > 0) {
				entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") - 1));
			}
		}
		if (SapModVariables.MapVariables.get(world).SolarFlare == 3 && !(new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
				}
				return false;
			}
		}.checkGamemode(entity) && new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR;
				}
				return false;
			}
		}.checkGamemode(entity)) && world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"))) && !entity.isInWaterRainOrBubble()) {
			if (y >= 63 || world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))) {
				entity.setSecondsOnFire(2);
				entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.ON_FIRE)), (float) (world.dayTime() / 48000));
			}
			if (y >= 8) {
				if (entity.getPersistentData().getDouble("SapStack") < 1200 && entity.getPersistentData().getDouble("WaterStack") <= 0) {
					entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") + 1));
				}
			}
		}
		if (SapModVariables.MapVariables.get(world).SolarFlare == 3 && (!world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z)) && (y < 8 || entity.isInWaterRainOrBubble())
				|| !world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"))))) {
			if (entity.getPersistentData().getDouble("SapStack") > 0) {
				entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") - 1));
			}
		}
		if (SapModVariables.MapVariables.get(world).SolarFlare == 4 && !(new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
				}
				return false;
			}
		}.checkGamemode(entity) && new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR;
				}
				return false;
			}
		}.checkGamemode(entity)) && world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"))) && !entity.isInWaterRainOrBubble()) {
			if (y >= 32 || world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))) {
				entity.setSecondsOnFire(3);
				entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.ON_FIRE)), (float) (world.dayTime() / 48000));
			}
			if (y >= -16) {
				if (entity.getPersistentData().getDouble("SapStack") < 1200 && entity.getPersistentData().getDouble("WaterStack") <= 0) {
					entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") + 1));
				}
			}
		}
		if (SapModVariables.MapVariables.get(world).SolarFlare == 4 && (!world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z)) && (y < -16 || entity.isInWaterRainOrBubble())
				|| !world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"))))) {
			if (entity.getPersistentData().getDouble("SapStack") > 0) {
				entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") - 1));
			}
		}
		if (SapModVariables.MapVariables.get(world).SolarFlare == 5 && !(new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
				}
				return false;
			}
		}.checkGamemode(entity) && new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR;
				}
				return false;
			}
		}.checkGamemode(entity)) && world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"))) && !entity.isInWaterRainOrBubble()) {
			if (y >= 8 || world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))) {
				entity.setSecondsOnFire(4);
				entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.ON_FIRE)), (float) (world.dayTime() / 48000));
			}
			if (entity.getPersistentData().getDouble("SapStack") < 1200 && entity.getPersistentData().getDouble("WaterStack") <= 0) {
				entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") + 1));
			}
		}
		if (SapModVariables.MapVariables.get(world).SolarFlare == 5 && (!world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z)) && entity.isInWaterRainOrBubble()
				|| !world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"))))) {
			if (entity.getPersistentData().getDouble("SapStack") > 0) {
				entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") - 1));
			}
		}
	}
}
