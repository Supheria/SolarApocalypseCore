package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarPhase;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修改不死生物的燃烧逻辑
 * 在COLLAPSE阶段防止不死生物在白天燃烧
 */
@Mixin(Zombie.class)
public abstract class UndeadMobBurnMixin {

	/**
	 * 拦截Zombie的tick方法
	 * 移除白天燃烧伤害
	 */
	@Inject(method = "tick", at = @At("HEAD"))
	private void onZombieTick(CallbackInfo ci) {
		Zombie zombie = (Zombie) (Object) this;
		Level level = zombie.level();

		if (level == null || level.isClientSide) {
			return;
		}

		SolarPhase currentPhase = SapModVariables.MapVariables.get(level).getCurrentPhase();
		if (currentPhase != SolarPhase.COLLAPSE) {
			return;
		}

		// 检查是否为白天
		long dayTime = level.dayTime();
		long timeOfDay = dayTime % 24000;
		boolean isDaytime = timeOfDay < 12000;

		if (isDaytime) {
			// 移除燃烧效果
			if (zombie.getRemainingFireTicks() > 0) {
				zombie.clearFire();
			}
		}
	}
}
