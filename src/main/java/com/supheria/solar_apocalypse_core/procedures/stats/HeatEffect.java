package com.supheria.solar_apocalypse_core.procedures.stats;

import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
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

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        execute(event.getEntity().level(), event.getEntity().getY(), event.getEntity());
    }

    public static void execute(LevelAccessor world, double y, Entity entity) {
        if (entity == null || isCreativeOrSpectator(entity)) {
            return;
        }

        SolarStage stage = SapModVariables.MapVariables.get(world).getCurrentStage();
        if (!stage.isAtLeast(SolarStage.STAGE_2) || stage.isAtLeast(SolarStage.STAGE_6)) {
            return;
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
