package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.transforms.fluid.SnowMelt;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 第六阶段积雪在失去支撑后会向下沉降，而不是悬浮在空中。
 */
@Mixin(SnowLayerBlock.class)
public abstract class SnowLayerMixin {

    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void solar$allowStage6Settling(BlockState state, LevelReader world, BlockPos pos,
            CallbackInfoReturnable<Boolean> cir) {
        if (world instanceof ServerLevel serverLevel && isStage6(serverLevel) && !SnowMelt.canSnowSurviveAt(world, pos)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "updateShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", at = @At("HEAD"), cancellable = true)
    private void solar$settleOnSupportChange(BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (!(level instanceof ServerLevel serverLevel) || !isStage6(serverLevel) || SnowMelt.canSnowSurviveAt(level, pos)) {
            return;
        }

        SnowMelt.triggerVisibleFall(serverLevel, pos, state);
        cir.setReturnValue(level.getBlockState(pos));
    }

    private boolean isStage6(ServerLevel serverLevel) {
        return SolarModVariables.MapVariables.get(serverLevel).getCurrentStage() == SolarStage.STAGE_6;
    }
}
