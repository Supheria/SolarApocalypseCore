package com.supheria.solar_apocalypse_core.client;

import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "solar_apocalypse_core", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class SpyglassSolarDisplay {
    private static final int MESSAGE_REFRESH_INTERVAL = 20;
    private static int refreshCooldown = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        Level level = minecraft.level;
        if (player == null || level == null) {
            refreshCooldown = 0;
            return;
        }

        if (!isUsingSpyglass(player) || !isUnderOpenSky(level, player) || !isAimingAtSky(level, player)) {
            refreshCooldown = 0;
            return;
        }

        if (refreshCooldown > 0) {
            refreshCooldown--;
            return;
        }

        player.displayClientMessage(buildStatusMessage(level), true);
        refreshCooldown = MESSAGE_REFRESH_INTERVAL;
    }

    private static boolean isUsingSpyglass(Player player) {
        if (!player.isUsingItem()) {
            return false;
        }
        ItemStack usedItem = player.getUseItem();
        return usedItem.is(Items.SPYGLASS);
    }

    private static boolean isUnderOpenSky(Level level, Player player) {
        BlockPos checkPos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        return level.canSeeSkyFromBelowWater(checkPos);
    }

    private static boolean isAimingAtSky(Level level, Player player) {
        Vec3 lookAngle = player.getLookAngle();
        if (lookAngle.y <= 0.75D) {
            return false;
        }

        Vec3 eyePosition = player.getEyePosition();
        Vec3 target = eyePosition.add(lookAngle.scale(128.0D));
        BlockHitResult hitResult = level.clip(new ClipContext(eyePosition, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return hitResult.getType() == HitResult.Type.MISS;
    }

    private static Component buildStatusMessage(Level level) {
        SolarStage currentStage = SolarModVariables.MapVariables.get(level).getSolarStage();
        if (!SolarStageHelper.hasPhaseEnd(currentStage)) {
            return Component.translatable("message.solar_apocalypse_core.spyglass.status_final", currentStage.getDisplayName());
        }

        long remainingTicks = SolarStageHelper.getRemainingTicksInPhase(level.dayTime(), currentStage);
        return Component.translatable(
                "message.solar_apocalypse_core.spyglass.status",
                currentStage.getDisplayName(),
                formatRemainingTime(remainingTicks)
        );
    }

    private static Component formatRemainingTime(long remainingTicks) {
        long days = remainingTicks / SolarStageHelper.DAY_TICKS;
        long ticksWithinDay = remainingTicks % SolarStageHelper.DAY_TICKS;
        long hours = ticksWithinDay / 1000L;

        if (days > 0) {
            return Component.translatable("message.solar_apocalypse_core.spyglass.time.days_hours", days, hours);
        }

        long minutes = Math.max(0L, Math.round((ticksWithinDay % 1000L) * 60.0D / 1000.0D));
        if (minutes == 60L) {
            hours += 1;
            minutes = 0L;
        }
        return Component.translatable("message.solar_apocalypse_core.spyglass.time.hours_minutes", hours, minutes);
    }

    private SpyglassSolarDisplay() {}
}
