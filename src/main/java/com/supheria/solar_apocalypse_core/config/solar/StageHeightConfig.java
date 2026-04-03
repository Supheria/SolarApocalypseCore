package com.supheria.solar_apocalypse_core.config.solar;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import org.apache.commons.lang3.tuple.Pair;

/**
 * 阶段安全高度和伤害倍数配置
 * 管理项目所有y坐标检查的中央配置源
 *
 * 配置文件位置: ${GAME_DIR}/config/solar/solar_apocalypse_core-heights-common.toml
 */
public class StageHeightConfig {
	public static final ForgeConfigSpec SPEC;
	public static final StageHeightValues HEIGHT_VALUES;

	static {
		Pair<StageHeightValues, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
				.configure(StageHeightValues::new);
		SPEC = pair.getRight();
		HEIGHT_VALUES = pair.getLeft();
	}

	public static class StageHeightValues {
		// ============ 安全高度（主伤害触发阈值）============
		// 低于该高度的实体不受该阶段的伤害
		public final IntValue stage1SafeHeight;
		public final IntValue stage2SafeHeight;
		public final IntValue stage3SafeHeight;
		public final IntValue stage4SafeHeight;
		public final IntValue stage5SafeHeight;
		public final IntValue stage6SafeHeight;

		// ============ 额外伤害高度（倍数伤害触发阈值）============
		// 当y超过此高度时，伤害乘以对应的倍数
		// 设为-64表示无额外伤害
		public final IntValue stage1ExtraDamageHeight;
		public final IntValue stage2ExtraDamageHeight;
		public final IntValue stage3ExtraDamageHeight;
		public final IntValue stage4ExtraDamageHeight;
		public final IntValue stage5ExtraDamageHeight;
		public final IntValue stage6ExtraDamageHeight;

		// ============ 伤害倍数 ============
		// 当y超过额外伤害高度时，伤害值乘以此倍数
		public final DoubleValue stage1ExtraDamageMultiplier;
		public final DoubleValue stage2ExtraDamageMultiplier;
		public final DoubleValue stage3ExtraDamageMultiplier;
		public final DoubleValue stage4ExtraDamageMultiplier;
		public final DoubleValue stage5ExtraDamageMultiplier;
		public final DoubleValue stage6ExtraDamageMultiplier;

		// ============ SapStack 积累高度 ============
		// 低于此高度的玩家不会积累太阳热度（SapStack）
		public final IntValue stage1SapHeight;
		public final IntValue stage2SapHeight;
		public final IntValue stage3SapHeight;
		public final IntValue stage4SapHeight;
		public final IntValue stage5SapHeight;
		public final IntValue stage6SapHeight;

		// ============ 水蒸发高度 ============
		// 低于此高度的水不会蒸发消失
		public final IntValue stage1WaterEvapHeight;
		public final IntValue stage2WaterEvapHeight;
		public final IntValue stage3WaterEvapHeight;
		public final IntValue stage4WaterEvapHeight;
		public final IntValue stage5WaterEvapHeight;
		public final IntValue stage6WaterEvapHeight;

		public StageHeightValues(ForgeConfigSpec.Builder builder) {
			builder.comment("===============================");
			builder.comment("Solar Apocalypse Core - Stage Height Configuration");
			builder.comment("===============================");
			builder.comment("Manages safe heights and damage multipliers for all 6 stages");
			builder.comment("All values can be adjusted to modify gameplay difficulty");
			builder.comment("");

			// ===== 安全高度配置 =====
			builder.push("safe_heights");
			builder.comment("Safe heights: Below this Y, entities don't take damage");

			stage1SafeHeight = builder
					.comment("Stage 1 (WARMING): No damage, set to -64 (no limit)")
					.defineInRange("stage1SafeHeight", -64, -64, 320);

			stage2SafeHeight = builder
					.comment("Stage 2 (ACCELERATION): Safe below Y=63 (sea level)")
					.defineInRange("stage2SafeHeight", 63, -64, 320);

			stage3SafeHeight = builder
					.comment("Stage 3 (PEAK): Safe below Y=32")
					.defineInRange("stage3SafeHeight", 32, -64, 320);

			stage4SafeHeight = builder
					.comment("Stage 4 (CRITICAL): Safe below Y=8")
					.defineInRange("stage4SafeHeight", 8, -64, 320);

			stage5SafeHeight = builder
					.comment("Stage 5 (ULTIMATE): Safe below Y=-16 (deep underground only)")
					.defineInRange("stage5SafeHeight", -16, -64, 320);

			stage6SafeHeight = builder
					.comment("Stage 6 (COLLAPSE): No damage, set to -64 (no limit)")
					.defineInRange("stage6SafeHeight", -64, -64, 320);

			builder.pop();

			// ===== 额外伤害高度配置 =====
			builder.push("extra_damage_heights");
			builder.comment("Extra damage heights: Above this Y, damage is multiplied");
			builder.comment("Set to -64 to disable extra damage for that stage");

			stage1ExtraDamageHeight = builder
					.comment("Stage 1: No extra damage")
					.defineInRange("stage1ExtraDamageHeight", -64, -64, 320);

			stage2ExtraDamageHeight = builder
					.comment("Stage 2: No extra damage (use -64 to disable)")
					.defineInRange("stage2ExtraDamageHeight", -64, -64, 320);

			stage3ExtraDamageHeight = builder
					.comment("Stage 3: Extra damage above Y=63")
					.defineInRange("stage3ExtraDamageHeight", 63, -64, 320);

			stage4ExtraDamageHeight = builder
					.comment("Stage 4: Extra damage above Y=32")
					.defineInRange("stage4ExtraDamageHeight", 32, -64, 320);

			stage5ExtraDamageHeight = builder
					.comment("Stage 5: Extra damage above Y=8")
					.defineInRange("stage5ExtraDamageHeight", 8, -64, 320);

			stage6ExtraDamageHeight = builder
					.comment("Stage 6: No extra damage")
					.defineInRange("stage6ExtraDamageHeight", -64, -64, 320);

			builder.pop();

			// ===== 伤害倍数配置 =====
			builder.push("damage_multipliers");
			builder.comment("Multipliers for damage when above extra damage height");
			builder.comment("1.0 = normal, 2.0 = double, 1.5 = 150%");

			stage1ExtraDamageMultiplier = builder
					.comment("Stage 1 multiplier")
					.defineInRange("stage1ExtraDamageMultiplier", 1.0, 0.1, 5.0);

			stage2ExtraDamageMultiplier = builder
					.comment("Stage 2 multiplier: 1.5x (50% bonus damage)")
					.defineInRange("stage2ExtraDamageMultiplier", 1.5, 0.1, 5.0);

			stage3ExtraDamageMultiplier = builder
					.comment("Stage 3 multiplier: 2.0x (double damage)")
					.defineInRange("stage3ExtraDamageMultiplier", 2.0, 0.1, 5.0);

			stage4ExtraDamageMultiplier = builder
					.comment("Stage 4 multiplier: 2.0x (double damage)")
					.defineInRange("stage4ExtraDamageMultiplier", 2.0, 0.1, 5.0);

			stage5ExtraDamageMultiplier = builder
					.comment("Stage 5 multiplier: 2.0x (double damage)")
					.defineInRange("stage5ExtraDamageMultiplier", 2.0, 0.1, 5.0);

			stage6ExtraDamageMultiplier = builder
					.comment("Stage 6 multiplier")
					.defineInRange("stage6ExtraDamageMultiplier", 1.0, 0.1, 5.0);

			builder.pop();

			// ===== SapStack 积累高度配置 =====
			builder.push("sap_accumulation_heights");
			builder.comment("Heights at which SapStack (sun heat) accumulates");

			stage1SapHeight = builder
					.comment("Stage 1: No accumulation")
					.defineInRange("stage1SapHeight", -64, -64, 320);

			stage2SapHeight = builder
					.comment("Stage 2: Accumulate above Y=63")
					.defineInRange("stage2SapHeight", 63, -64, 320);

			stage3SapHeight = builder
					.comment("Stage 3: Accumulate above Y=8")
					.defineInRange("stage3SapHeight", 8, -64, 320);

			stage4SapHeight = builder
					.comment("Stage 4: Accumulate above Y=-16")
					.defineInRange("stage4SapHeight", -16, -64, 320);

			stage5SapHeight = builder
					.comment("Stage 5: Accumulate everywhere (Y=-64 = unlimited)")
					.defineInRange("stage5SapHeight", -64, -64, 320);

			stage6SapHeight = builder
					.comment("Stage 6: No accumulation in COLLAPSE")
					.defineInRange("stage6SapHeight", -64, -64, 320);

			builder.pop();

			// ===== 水蒸发高度配置 =====
			builder.push("water_evaporation_heights");
			builder.comment("Heights at which water evaporates");

			stage1WaterEvapHeight = builder
					.comment("Stage 1: No water evaporation")
					.defineInRange("stage1WaterEvapHeight", -64, -64, 320);

			stage2WaterEvapHeight = builder
					.comment("Stage 2: Water evaporates above Y=63")
					.defineInRange("stage2WaterEvapHeight", 63, -64, 320);

			stage3WaterEvapHeight = builder
					.comment("Stage 3: Water evaporates above Y=32")
					.defineInRange("stage3WaterEvapHeight", 32, -64, 320);

			stage4WaterEvapHeight = builder
					.comment("Stage 4: Water evaporates above Y=8")
					.defineInRange("stage4WaterEvapHeight", 8, -64, 320);

			stage5WaterEvapHeight = builder
					.comment("Stage 5: Water evaporates above Y=-16")
					.defineInRange("stage5WaterEvapHeight", -16, -64, 320);

			stage6WaterEvapHeight = builder
					.comment("Stage 6: Water freezes instead of evaporates")
					.defineInRange("stage6WaterEvapHeight", -64, -64, 320);

			builder.pop();
		}

		// ============ 辅助方法 ============

		/**
		 * 获取指定阶段的安全高度
		 * 低于此高度的实体不受伤害
		 */
		public int getSafeHeight(int stage) {
			return switch (stage) {
				case 1 -> stage1SafeHeight.get();
				case 2 -> stage2SafeHeight.get();
				case 3 -> stage3SafeHeight.get();
				case 4 -> stage4SafeHeight.get();
				case 5 -> stage5SafeHeight.get();
				case 6 -> stage6SafeHeight.get();
				default -> -64;
			};
		}

		/**
		 * 获取指定阶段的额外伤害高度
		 */
		public int getExtraDamageHeight(int stage) {
			return switch (stage) {
				case 1 -> stage1ExtraDamageHeight.get();
				case 2 -> stage2ExtraDamageHeight.get();
				case 3 -> stage3ExtraDamageHeight.get();
				case 4 -> stage4ExtraDamageHeight.get();
				case 5 -> stage5ExtraDamageHeight.get();
				case 6 -> stage6ExtraDamageHeight.get();
				default -> -64;
			};
		}

		/**
		 * 获取指定阶段的伤害倍数
		 */
		public double getExtraDamageMultiplier(int stage) {
			return switch (stage) {
				case 1 -> stage1ExtraDamageMultiplier.get();
				case 2 -> stage2ExtraDamageMultiplier.get();
				case 3 -> stage3ExtraDamageMultiplier.get();
				case 4 -> stage4ExtraDamageMultiplier.get();
				case 5 -> stage5ExtraDamageMultiplier.get();
				case 6 -> stage6ExtraDamageMultiplier.get();
				default -> 1.0;
			};
		}

		/**
		 * 获取指定阶段的SapStack积累高度
		 */
		public int getSapHeight(int stage) {
			return switch (stage) {
				case 1 -> stage1SapHeight.get();
				case 2 -> stage2SapHeight.get();
				case 3 -> stage3SapHeight.get();
				case 4 -> stage4SapHeight.get();
				case 5 -> stage5SapHeight.get();
				case 6 -> stage6SapHeight.get();
				default -> -64;
			};
		}

		/**
		 * 获取指定阶段的水蒸发高度
		 */
		public int getWaterEvapHeight(int stage) {
			return switch (stage) {
				case 1 -> stage1WaterEvapHeight.get();
				case 2 -> stage2WaterEvapHeight.get();
				case 3 -> stage3WaterEvapHeight.get();
				case 4 -> stage4WaterEvapHeight.get();
				case 5 -> stage5WaterEvapHeight.get();
				case 6 -> stage6WaterEvapHeight.get();
				default -> -64;
			};
		}

		/**
		 * 检查实体是否在给定阶段的伤害范围内
		 */
		public boolean isInDamageRange(int stage, double y) {
			int safeHeight = getSafeHeight(stage);
			return y > safeHeight;
		}

		/**
		 * 获取实际伤害倍数（考虑额外高度）
		 */
		public double getDamageMultiplier(int stage, double y) {
			int extraHeight = getExtraDamageHeight(stage);
			if (extraHeight == -64 || y <= extraHeight) {
				return 1.0;
			}
			return getExtraDamageMultiplier(stage);
		}
	}

	// ============ 全局访问方法 ============

	public static int getSafeHeight(int stage) {
		return HEIGHT_VALUES.getSafeHeight(stage);
	}

	public static int getExtraDamageHeight(int stage) {
		return HEIGHT_VALUES.getExtraDamageHeight(stage);
	}

	public static double getExtraDamageMultiplier(int stage) {
		return HEIGHT_VALUES.getExtraDamageMultiplier(stage);
	}

	public static int getSapHeight(int stage) {
		return HEIGHT_VALUES.getSapHeight(stage);
	}

	public static int getWaterEvapHeight(int stage) {
		return HEIGHT_VALUES.getWaterEvapHeight(stage);
	}

	public static boolean isInDamageRange(int stage, double y) {
		return HEIGHT_VALUES.isInDamageRange(stage, y);
	}

	public static double getDamageMultiplier(int stage, double y) {
		return HEIGHT_VALUES.getDamageMultiplier(stage, y);
	}
}
