package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
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
 * 根据太阳阶段调整原版钓鱼上钩节奏：
 * 第 1-4 阶段逐步拉长等待时间，第 5 阶段完全不上钩，第 6 阶段保持原版。
 */
@Mixin(FishingHook.class)
public abstract class FishingHookMixin {

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

        SolarStage stage = SolarModVariables.MapVariables.get(serverLevel).getCurrentStage();
        if (stage.isCollapsePhase() || stage == SolarStage.NONE) {
            return;
        }

        if (stage == SolarStage.STAGE_5) {
            this.nibble = 0;
            this.timeUntilHooked = 0;
            if (this.timeUntilLured <= 0) {
                this.timeUntilLured = 600;
            }
            hook.getEntityData().set(DATA_BITING, false);
            return;
        }

        if (this.timeUntilLured > 0) {
            this.timeUntilLured += getAdditionalLureDelay(stage, serverLevel);
        }
    }

    private int getAdditionalLureDelay(SolarStage stage, ServerLevel serverLevel) {
        return switch (stage) {
            case STAGE_1 -> serverLevel.random.nextFloat() < 0.25F ? 1 : 0;
            case STAGE_2 -> serverLevel.random.nextFloat() < 0.5F ? 1 : 0;
            case STAGE_3 -> 1;
            case STAGE_4 -> 2;
            default -> 0;
        };
    }
}
