package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.level.Level;

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
			SolarStage currentStage = SapModVariables.MapVariables.get(level).getCurrentStage();

			if (currentStage != SolarStage.STAGE_6) {
				return;
			}

			long timeOfDay = level.getDayTime() % 24000;
			if (timeOfDay < 12000) {
				int original = cir.getReturnValue();
				int modifiedDarkness = Math.max(original, 11);
				if (modifiedDarkness != original) {
					cir.setReturnValue(modifiedDarkness);
				}
			}
		} catch (Exception e) {
			// silent fail
		}
	}
}
