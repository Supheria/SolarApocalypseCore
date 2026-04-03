package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修改mob生成规则
 * 在第六阶段允许僵尸等不死生物在白天生成
 */
@Mixin(Mob.class)
public abstract class MobSpawnRulesMixin {

	@Inject(method = "checkMobSpawnRules", at = @At("HEAD"), cancellable = true)
	private static void onCheckMobSpawnRules(
			EntityType<?> entityType,
			LevelAccessor level,
			net.minecraft.world.entity.MobSpawnType spawnType,
			net.minecraft.core.BlockPos blockPos,
			RandomSource random,
			CallbackInfoReturnable<Boolean> cir) {

		SolarStage currentPhase = SapModVariables.MapVariables.get(level).getCurrentStage();

		// 只在COLLAPSE阶段处理
		if (currentPhase != SolarStage.STAGE_6) {
			return;
		}

		// 检查是否为白天
		if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
			long dayTime = serverLevel.dayTime();
			long timeOfDay = dayTime % 24000;
			boolean isDaytime = timeOfDay < 12000;

			// 白天允许不死生物（僵尸等）生成
			if (isDaytime && isUndeadMob(entityType)) {
				cir.setReturnValue(true);
			}
		}
	}

	/**
	 * 检查是否为不死系mob
	 */
	private static boolean isUndeadMob(EntityType<?> entityType) {
		String name = entityType.toString().toLowerCase();
		return name.contains("zombie") || name.contains("skeleton") || name.contains("wither") ||
				name.contains("husk") || name.contains("drowned");
	}
}
