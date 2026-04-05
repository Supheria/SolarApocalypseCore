package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 防止COLLAPSE阶段的积雪融化
 */
@Mixin(SnowLayerBlock.class)
public abstract class SnowLayerMixin {

	/**
	 * 拦截积雪能否存在的检查
	 * 在COLLAPSE阶段，积雪可以在任何地方存在而不融化
	 */
	@Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
	private void onCanSurvive(BlockState blockState, LevelReader world, BlockPos pos,
			CallbackInfoReturnable<Boolean> cir) {
		// 检查是否在COLLAPSE阶段
		if (world instanceof net.minecraft.server.level.ServerLevel serverLevel) {
			SolarStage currentPhase = SolarModVariables.MapVariables.get(serverLevel).getCurrentStage();

			// 在COLLAPSE阶段，积雪总是可以存在
			if (currentPhase == SolarStage.STAGE_6) {
				cir.setReturnValue(true);
			}
		}
	}
}
