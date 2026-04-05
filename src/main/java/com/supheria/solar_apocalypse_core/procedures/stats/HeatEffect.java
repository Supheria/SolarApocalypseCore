package com.supheria.solar_apocalypse_core.procedures.stats;

import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.thirst.SolarThirstHelper;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
    private static final int DEHYDRATION_REFRESH_INTERVAL = 40;
    private static final int DEHYDRATION_DURATION = 100;

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        execute(event.getEntity().level(), event.getEntity().getY(), event.getEntity());
    }

    public static void execute(LevelAccessor world, double y, Entity entity) {
        if (entity == null || world.isClientSide() || isCreativeOrSpectator(entity)) {
            return;
        }

        SolarStage stage = SapModVariables.MapVariables.get(world).getCurrentStage();
        if (!SolarThirstHelper.isDehydrationStage(stage)) {
            return;
        }

        if (entity instanceof LivingEntity livingEntity && SolarThirstHelper.isDehydrationActive(stage, y)) {
            applyDehydrationEffects(livingEntity, stage);
        }

        if (y <= StageHeightConfig.getSafeHeight(stage)) {
            return;
        }

        int fireSeconds = StageHeightConfig.getFireSeconds(stage);
        float fireDamage = StageHeightConfig.getFireDamage(stage);

        if (fireSeconds <= 0 || fireDamage <= 0.0f) {
            return;
        }

        entity.setSecondsOnFire(fireSeconds);
        entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.ON_FIRE)), fireDamage);
    }

    private static void applyDehydrationEffects(LivingEntity entity, SolarStage stage) {
        if (entity.tickCount % DEHYDRATION_REFRESH_INTERVAL != 0) {
            return;
        }

        int amplifier = getDehydrationAmplifier(stage);
        refreshEffect(entity, MobEffects.MOVEMENT_SLOWDOWN, amplifier);
        refreshEffect(entity, MobEffects.WEAKNESS, amplifier);
        refreshEffect(entity, MobEffects.DIG_SLOWDOWN, amplifier);
    }

    private static void refreshEffect(LivingEntity entity, MobEffect effect, int amplifier) {
        MobEffectInstance current = entity.getEffect(effect);
        if (current != null && current.getAmplifier() >= amplifier && current.getDuration() > DEHYDRATION_REFRESH_INTERVAL) {
            return;
        }
        entity.addEffect(new MobEffectInstance(effect, DEHYDRATION_DURATION, amplifier, false, true, true));
    }

    private static int getDehydrationAmplifier(SolarStage stage) {
        return switch (stage) {
            case STAGE_2 -> 0;
            case STAGE_3 -> 1;
            case STAGE_4 -> 2;
            case STAGE_5 -> 3;
            default -> 0;
        };
    }

    private static boolean isCreativeOrSpectator(Entity entity) {
        return checkGamemode(entity, GameType.CREATIVE) || checkGamemode(entity, GameType.SPECTATOR);
    }

    private static boolean checkGamemode(Entity entity, GameType gameType) {
        if (entity instanceof ServerPlayer serverPlayer) {
            return serverPlayer.gameMode.getGameModeForPlayer() == gameType;
        }

        if (entity.level().isClientSide() && entity instanceof Player player && Minecraft.getInstance().getConnection() != null) {
            var playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId());
            return playerInfo != null && playerInfo.getGameMode() == gameType;
        }

        return false;
    }
}
