package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import com.supheria.solar_apocalypse_core.world.SolarStageHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 在第六阶段（恒冬期）白天绕过光照检查，允许所有 Monster 子类在地表生成
 * 注入 isDarkEnoughToSpawn，仅跳过光照判断，难度检查（Peaceful）在上层 checkMonsterSpawnRules 保留
 */
@Mixin(Monster.class)
public class MobSpawnRulesMixin {

	@Inject(method = "isDarkEnoughToSpawn", at = @At("HEAD"), cancellable = true)
	private static void allowDaytimeSpawnInStage6(
			ServerLevelAccessor level,
			BlockPos pos,
			RandomSource random,
			CallbackInfoReturnable<Boolean> cir) {

		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}

		SolarStage stage = SolarModVariables.MapVariables.get(serverLevel).getSolarStage();
		if (stage != SolarStage.STAGE_6) {
			return;
		}

		if (SolarStageHelper.isDaytime(serverLevel.dayTime())) {
			cir.setReturnValue(true);
		}
	}
}
