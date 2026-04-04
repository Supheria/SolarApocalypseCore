package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.client.CollapsePhaseState;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 在COLLAPSE阶段修改生物群系温度和降水量
 * 所有地方都返回寒冷温度和降雪，使Minecraft认为是下雪的环境
 */
@OnlyIn(Dist.CLIENT)
@Mixin(Biome.class)
public abstract class BiomeGetTemperatureMixin {

	/**
	 * 拦截getTemperature方法
	 * 在COLLAPSE阶段返回寒冷温度（< 0.15）
	 */
	@Inject(method = "getTemperature(Lnet/minecraft/core/BlockPos;)F", at = @At("HEAD"), cancellable = true)
	private void onGetTemperature(BlockPos pos, CallbackInfoReturnable<Float> cir) {
		try {
			if (CollapsePhaseState.isInCollapse()) {
				cir.setReturnValue(-0.5f);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 拦截hasPrecipitation方法
	 * 在COLLAPSE阶段强制返回true，使所有生物群系都能产生降水
	 */
	@Inject(method = "hasPrecipitation()Z", at = @At("HEAD"), cancellable = true)
	private void onHasPrecipitation(CallbackInfoReturnable<Boolean> cir) {
		try {
			if (CollapsePhaseState.isInCollapse()) {
				cir.setReturnValue(true);
			}
		} catch (Exception e) {
		}
	}
}
