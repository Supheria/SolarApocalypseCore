package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 在第六阶段白天降低光照等级
 */
@Mixin(Level.class)
public class LightLevelMixin {

	/**
	 * 修改 getSkyDarken 返回值
	 * 在第六阶段白天时返回较高的黑暗值
	 */
	@Inject(method = "getSkyDarken", at = @At("RETURN"), cancellable = true)
	private void modifySkyDarken(CallbackInfoReturnable<Integer> cir) {
		Level level = (Level) (Object) this;

		try {
			SolarStage currentStage = SolarModVariables.MapVariables.get(level).getSolarStage();

			if (currentStage != SolarStage.STAGE_6) {
				return;
			}

			if (SolarStageHelper.isDaytime(level.getDayTime())) {
				int original = cir.getReturnValue();
				int modifiedDarkness = Math.max(original, SolarStageConfig.getCollapseMinSkyDarken());
				if (modifiedDarkness != original) {
					cir.setReturnValue(modifiedDarkness);
				}
			}
		} catch (Exception e) {
			// silent fail
		}
	}
}
