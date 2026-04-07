package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.world.HeightZoneHelper;
import com.supheria.solar_apocalypse_core.world.HeightZoneHelper.HeightZone;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 根据高度区间调整原版钓鱼上钩节奏。
 */
@Mixin(FishingHook.class)
public abstract class FishingHookMixin {
    private static final int DANGER_FISHING_DELAY = 600;

    @Shadow
    private int nibble;

    @Shadow
    private int timeUntilLured;

    @Shadow
    private int timeUntilHooked;

    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> DATA_BITING;

    @Inject(method = "catchingFish", at = @At("HEAD"))
    private void solar$adjustFishingTiming(BlockPos pos, CallbackInfo ci) {
        FishingHook hook = (FishingHook) (Object) this;
        if (!(hook.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        HeightZone heightZone = HeightZoneHelper.getHeightZone(serverLevel, pos);
        if (heightZone == HeightZone.DANGER) {
            this.nibble = 0;
            this.timeUntilHooked = 0;
            if (this.timeUntilLured <= 0) {
                this.timeUntilLured = DANGER_FISHING_DELAY;
            }
            hook.getEntityData().set(DATA_BITING, false);
            return;
        }

        if (heightZone == HeightZone.SAFE && this.timeUntilLured > 0) {
            this.timeUntilLured += getAdditionalLureDelay(serverLevel);
        }
    }

    private int getAdditionalLureDelay(ServerLevel serverLevel) {
        return serverLevel.random.nextFloat() < 0.5F ? 1 : 0;
    }
}
