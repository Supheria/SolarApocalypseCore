package com.supheria.solar_apocalypse_core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 禁止南瓜藤和西瓜藤自然成熟，保留骨粉催熟。
 */
@Mixin(StemBlock.class)
public class StemGrowthBlockMixin {

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void solar$disableNaturalGrowth(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        ci.cancel();
    }
}
