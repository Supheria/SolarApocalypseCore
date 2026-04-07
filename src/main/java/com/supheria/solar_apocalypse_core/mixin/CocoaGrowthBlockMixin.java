package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.world.HeightZoneHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 仅允许舒适高度的可可自然成熟，保留骨粉催熟。
 */
@Mixin(CocoaBlock.class)
public class CocoaGrowthBlockMixin {

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void solar$disableNaturalGrowth(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!HeightZoneHelper.allowsNaturalGrowth(level, pos)) {
            ci.cancel();
        }
    }
}
