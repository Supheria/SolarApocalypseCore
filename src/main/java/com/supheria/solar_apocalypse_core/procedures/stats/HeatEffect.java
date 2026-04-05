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

        int fireSeconds = switch (stage) {
            case STAGE_2 -> 1;
            case STAGE_3 -> 2;
            case STAGE_4 -> 3;
            case STAGE_5 -> 4;
            default -> 0;
        };

        if (fireSeconds <= 0) {
            return;
        }

        entity.setSecondsOnFire(fireSeconds);
        entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.ON_FIRE)), (float) (world.dayTime() / 48000));
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
