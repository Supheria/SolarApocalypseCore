package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修改所有敌对生物的燃烧逻辑
 * 在第六阶段白天防止所有敌对生物（怪物类别）燃烧
 */
@Mixin(Mob.class)
public abstract class HostileMobBurnMixin {

	/**
	 * 拦截 Mob 的 tick 方法
	 * 在第六阶段白天移除所有敌对生物的燃烧伤害
	 */
	@Inject(method = "tick", at = @At("HEAD"))
	private void onMobTick(CallbackInfo ci) {
		Mob mob = (Mob) (Object) this;
		Level level = mob.level();

		if (level == null || level.isClientSide) {
			return;
		}

		// 只处理敌对生物（通过 getType().getCategory() 检查）
		try {
			if (mob.getType().getCategory() != MobCategory.MONSTER) {
				return;
			}
		} catch (Exception e) {
			// 如果检查失败，跳过此生物
			return;
		}

		SolarStage currentPhase = SapModVariables.MapVariables.get(level).getCurrentStage();
		if (currentPhase != SolarStage.STAGE_6) {
			return;
		}

		// 检查是否为白天
		long dayTime = level.dayTime();
		long timeOfDay = dayTime % 24000;
		boolean isDaytime = timeOfDay < 12000;

		if (isDaytime) {
			// 移除燃烧效果
			if (mob.getRemainingFireTicks() > 0) {
				mob.clearFire();
			}
		}
	}
}
