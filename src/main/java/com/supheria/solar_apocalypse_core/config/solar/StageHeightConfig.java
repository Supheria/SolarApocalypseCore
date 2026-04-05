package com.supheria.solar_apocalypse_core.config.solar;

import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
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
		// 高于此高度的方块会受到变换影响
		public final IntValue stage1SafeHeight;
		public final IntValue stage2SafeHeight;
		public final IntValue stage3SafeHeight;
		public final IntValue stage4SafeHeight;
		public final IntValue stage5SafeHeight;
		public final IntValue stage6SafeHeight;

		// ============ 口渴倍率高度阈值 ============
		// 低于此高度的玩家不会受到太阳阶段额外口渴倍率影响
		public final IntValue stage1CozyHeight;
		public final IntValue stage2CozyHeight;
		public final IntValue stage3CozyHeight;
		public final IntValue stage4CozyHeight;
		public final IntValue stage5CozyHeight;
		public final IntValue stage6CozyHeight;

		// ============ 燃烧强度配置 ============
		// 超过安全高度后，实体统一使用这组燃烧秒数与伤害值
		public final IntValue stage1FireSeconds;
		public final IntValue stage2FireSeconds;
		public final IntValue stage3FireSeconds;
		public final IntValue stage4FireSeconds;
		public final IntValue stage5FireSeconds;
		public final IntValue stage6FireSeconds;

		public final DoubleValue stage1FireDamage;
		public final DoubleValue stage2FireDamage;
		public final DoubleValue stage3FireDamage;
		public final DoubleValue stage4FireDamage;
		public final DoubleValue stage5FireDamage;
		public final DoubleValue stage6FireDamage;

		public StageHeightValues(ForgeConfigSpec.Builder builder) {
			builder.comment("===============================");
			builder.comment("Solar Apocalypse Core - Stage Height Configuration");
			builder.comment("===============================");
			builder.comment("Manages safe heights, thirst thresholds, and fire intensity for all 6 stages");
			builder.comment("All values can be adjusted to modify gameplay difficulty");
			builder.comment("");

			// ===== 安全高度配置 =====
			builder.push("safe_heights");
			builder.comment("Safe heights: Below this Y, entities don't take damage");

			stage1SafeHeight = builder
					.comment("Stage 1: No damage, set to -64 (no limit)")
					.defineInRange("stage1SafeHeight", -64, -64, 320);

			stage2SafeHeight = builder
					.comment("Stage 2: Safe below Y=63 (sea level)")
					.defineInRange("stage2SafeHeight", 63, -64, 320);

			stage3SafeHeight = builder
					.comment("Stage 3: Safe below Y=32")
					.defineInRange("stage3SafeHeight", 32, -64, 320);

			stage4SafeHeight = builder
					.comment("Stage 4: Safe below Y=8")
					.defineInRange("stage4SafeHeight", 8, -64, 320);

			stage5SafeHeight = builder
					.comment("Stage 5: Safe below Y=-16 (deep underground only)")
					.defineInRange("stage5SafeHeight", -16, -64, 320);

			stage6SafeHeight = builder
					.comment("Stage 6: No damage, set to -64 (no limit)")
					.defineInRange("stage6SafeHeight", -64, -64, 320);

			builder.pop();

			// ===== 口渴倍率高度配置 =====
			builder.push("sap_accumulation_heights");
			builder.comment("Heights at which solar thirst multipliers apply");

			stage1CozyHeight = builder
					.comment("Stage 1: No extra thirst multiplier")
					.defineInRange("stage1SapHeight", -64, -64, 320);

			stage2CozyHeight = builder
					.comment("Stage 2: Extra thirst multiplier above Y=63")
					.defineInRange("stage2SapHeight", 63, -64, 320);

			stage3CozyHeight = builder
					.comment("Stage 3: Extra thirst multiplier above Y=8")
					.defineInRange("stage3SapHeight", 8, -64, 320);

			stage4CozyHeight = builder
					.comment("Stage 4: Extra thirst multiplier above Y=-16")
					.defineInRange("stage4SapHeight", -16, -64, 320);

			stage5CozyHeight = builder
					.comment("Stage 5: Extra thirst multiplier everywhere (Y=-64 = unlimited)")
					.defineInRange("stage5SapHeight", -64, -64, 320);

			stage6CozyHeight = builder
					.comment("Stage 6: No extra thirst multiplier in COLLAPSE")
					.defineInRange("stage6SapHeight", -64, -64, 320);

			builder.pop();

			// ===== 燃烧强度配置 =====
			builder.push("fire_intensity");
			builder.comment("Fire duration and damage used when entities are above the safe height");

			stage1FireSeconds = builder
					.comment("Stage 1 fire duration in seconds")
					.defineInRange("stage1FireSeconds", 0, 0, 60);

			stage2FireSeconds = builder
					.comment("Stage 2 fire duration in seconds")
					.defineInRange("stage2FireSeconds", 1, 0, 60);

			stage3FireSeconds = builder
					.comment("Stage 3 fire duration in seconds")
					.defineInRange("stage3FireSeconds", 2, 0, 60);

			stage4FireSeconds = builder
					.comment("Stage 4 fire duration in seconds")
					.defineInRange("stage4FireSeconds", 3, 0, 60);

			stage5FireSeconds = builder
					.comment("Stage 5 fire duration in seconds")
					.defineInRange("stage5FireSeconds", 4, 0, 60);

			stage6FireSeconds = builder
					.comment("Stage 6 fire duration in seconds")
					.defineInRange("stage6FireSeconds", 0, 0, 60);

			stage1FireDamage = builder
					.comment("Stage 1 direct fire damage")
					.defineInRange("stage1FireDamage", 0.0, 0.0, 100.0);

			stage2FireDamage = builder
					.comment("Stage 2 direct fire damage")
					.defineInRange("stage2FireDamage", 1.0, 0.0, 100.0);

			stage3FireDamage = builder
					.comment("Stage 3 direct fire damage")
					.defineInRange("stage3FireDamage", 2.0, 0.0, 100.0);

			stage4FireDamage = builder
					.comment("Stage 4 direct fire damage")
					.defineInRange("stage4FireDamage", 3.0, 0.0, 100.0);

			stage5FireDamage = builder
					.comment("Stage 5 direct fire damage")
					.defineInRange("stage5FireDamage", 4.0, 0.0, 100.0);

			stage6FireDamage = builder
					.comment("Stage 6 direct fire damage")
					.defineInRange("stage6FireDamage", 0.0, 0.0, 100.0);

			builder.pop();
		}

		// ============ 辅助方法 ============

		/**
		 * 获取指定阶段的安全高度
		 * 低于此高度的实体不受伤害
		 * 高于此高度的方块会受到变换影响
		 */
		public int getSafeHeight(SolarStage stage) {
			return switch (stage) {
				case STAGE_1 -> stage1SafeHeight.get();
				case STAGE_2 -> stage2SafeHeight.get();
				case STAGE_3 -> stage3SafeHeight.get();
				case STAGE_4 -> stage4SafeHeight.get();
				case STAGE_5 -> stage5SafeHeight.get();
				case STAGE_6 -> stage6SafeHeight.get();
				default -> -64;
			};
		}

		/**
		 * 获取指定阶段的口渴倍率高度阈值
		 * 低于此高度的实体不会受到太阳阶段额外口渴倍率影响
		 */
		public int getCozyHeight(SolarStage stage) {
			return switch (stage) {
				case STAGE_1 -> stage1CozyHeight.get();
				case STAGE_2 -> stage2CozyHeight.get();
				case STAGE_3 -> stage3CozyHeight.get();
				case STAGE_4 -> stage4CozyHeight.get();
				case STAGE_5 -> stage5CozyHeight.get();
				case STAGE_6 -> stage6CozyHeight.get();
				default -> -64;
			};
		}

		public int getFireSeconds(SolarStage stage) {
			return switch (stage) {
				case STAGE_1 -> stage1FireSeconds.get();
				case STAGE_2 -> stage2FireSeconds.get();
				case STAGE_3 -> stage3FireSeconds.get();
				case STAGE_4 -> stage4FireSeconds.get();
				case STAGE_5 -> stage5FireSeconds.get();
				case STAGE_6 -> stage6FireSeconds.get();
				default -> 0;
			};
		}

		public float getFireDamage(SolarStage stage) {
			return switch (stage) {
				case STAGE_1 -> stage1FireDamage.get().floatValue();
				case STAGE_2 -> stage2FireDamage.get().floatValue();
				case STAGE_3 -> stage3FireDamage.get().floatValue();
				case STAGE_4 -> stage4FireDamage.get().floatValue();
				case STAGE_5 -> stage5FireDamage.get().floatValue();
				case STAGE_6 -> stage6FireDamage.get().floatValue();
				default -> 0.0f;
			};
		}
	}

	// ============ 全局访问方法 ============

	public static int getSafeHeight(SolarStage stage) {
		return HEIGHT_VALUES.getSafeHeight(stage);
	}

	public static int getCozyHeight(SolarStage stage) {
		return HEIGHT_VALUES.getCozyHeight(stage);
	}

	public static int getFireSeconds(SolarStage stage) {
		return HEIGHT_VALUES.getFireSeconds(stage);
	}

	public static float getFireDamage(SolarStage stage) {
		return HEIGHT_VALUES.getFireDamage(stage);
	}
}
