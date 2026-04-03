package com.supheria.solar_apocalypse_core.procedures.stats;

import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.init.SapModItems;
import com.supheria.solar_apocalypse_core.init.SapModMobEffects;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 太阳热浪效果处理器（原 SapHotProcedure）。
 */
@Mod.EventBusSubscriber
public class HeatEffect {

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        execute(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null)
            return;
        int stage = SapModVariables.MapVariables.get(world).solarStage.getEruptionLevel();
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
        if (SapModVariables.MapVariables.get(world).getCurrentStage() == SolarStage.STAGE_1 && !(new Object() {
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
        if (SapModVariables.MapVariables.get(world).getCurrentStage() == SolarStage.STAGE_1
                && (SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450 || !world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z)) || world.getLevelData().isRaining()
                || entity.isInWaterRainOrBubble() || (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == SapModItems.UV_UMBRELLA.get()
                || (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == SapModItems.UV_UMBRELLA.get()
                || !world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"))))) {
            if (entity.getPersistentData().getDouble("SapStack") > 0) {
                entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") - 1));
            }
        }
        if (SapModVariables.MapVariables.get(world).getCurrentStage() == SolarStage.STAGE_2 && !(new Object() {
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
            if (y >= StageHeightConfig.getSapHeight(stage)) {
                if (entity.getPersistentData().getDouble("SapStack") < 1200 && entity.getPersistentData().getDouble("WaterStack") <= 0) {
                    entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") + 1));
                }
            }
        }
        if (SapModVariables.MapVariables.get(world).getCurrentStage() == SolarStage.STAGE_2 && (!world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))
                && (y < StageHeightConfig.getSapHeight(stage) || world.getLevelData().isRaining() || entity.isInWaterRainOrBubble() || (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == SapModItems.UV_UMBRELLA.get()
                || (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == SapModItems.UV_UMBRELLA.get())
                || !world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"))))) {
            if (entity.getPersistentData().getDouble("SapStack") > 0) {
                entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") - 1));
            }
        }
        if (SapModVariables.MapVariables.get(world).getCurrentStage() == SolarStage.STAGE_3 && !(new Object() {
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
            if (y >= StageHeightConfig.getExtraDamageHeight(stage) || world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))) {
                entity.setSecondsOnFire(2);
                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.ON_FIRE)), (float) (world.dayTime() / 48000));
            }
            if (y >= StageHeightConfig.getSapHeight(stage)) {
                if (entity.getPersistentData().getDouble("SapStack") < 1200 && entity.getPersistentData().getDouble("WaterStack") <= 0) {
                    entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") + 1));
                }
            }
        }
        if (SapModVariables.MapVariables.get(world).getCurrentStage() == SolarStage.STAGE_3 && (!world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z)) && (y < StageHeightConfig.getSapHeight(stage) || entity.isInWaterRainOrBubble())
                || !world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"))))) {
            if (entity.getPersistentData().getDouble("SapStack") > 0) {
                entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") - 1));
            }
        }
        if (SapModVariables.MapVariables.get(world).getCurrentStage() == SolarStage.STAGE_4 && !(new Object() {
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
            if (y >= StageHeightConfig.getExtraDamageHeight(stage) || world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))) {
                entity.setSecondsOnFire(3);
                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.ON_FIRE)), (float) (world.dayTime() / 48000));
            }
            if (y >= StageHeightConfig.getSapHeight(stage)) {
                if (entity.getPersistentData().getDouble("SapStack") < 1200 && entity.getPersistentData().getDouble("WaterStack") <= 0) {
                    entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") + 1));
                }
            }
        }
        if (SapModVariables.MapVariables.get(world).getCurrentStage() == SolarStage.STAGE_4 && (!world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z)) && (y < StageHeightConfig.getSapHeight(stage) || entity.isInWaterRainOrBubble())
                || !world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"))))) {
            if (entity.getPersistentData().getDouble("SapStack") > 0) {
                entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") - 1));
            }
        }
        if (SapModVariables.MapVariables.get(world).getCurrentStage() == SolarStage.STAGE_5 && !(new Object() {
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
            if (y >= StageHeightConfig.getExtraDamageHeight(stage) || world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))) {
                entity.setSecondsOnFire(4);
                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.ON_FIRE)), (float) (world.dayTime() / 48000));
            }
            if (entity.getPersistentData().getDouble("SapStack") < 1200 && entity.getPersistentData().getDouble("WaterStack") <= 0) {
                entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") + 1));
            }
        }
        if (SapModVariables.MapVariables.get(world).getCurrentStage() == SolarStage.STAGE_5 && (!world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z)) && entity.isInWaterRainOrBubble()
                || !world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_overworld"))))) {
            if (entity.getPersistentData().getDouble("SapStack") > 0) {
                entity.getPersistentData().putDouble("SapStack", (entity.getPersistentData().getDouble("SapStack") - 1));
            }
        }
    }
}
