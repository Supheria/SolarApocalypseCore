package com.supheria.solar_apocalypse_core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 禁止主世界农作物自然随机刻生长，保留骨粉路径。
 */
@Mixin(CropBlock.class)
public class CropGrowthBlockMixin {

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void solar$disableNaturalGrowth(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        ci.cancel();
    }
}
