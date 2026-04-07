package com.supheria.solar_apocalypse_core.config.solar;

import com.supheria.solar_apocalypse_core.world.SolarStage;

/**
 * 各太阳阶段对应的高度阈值与灼烧伤害配置。
 *
 * <p>这里把“地表安全线”“相对舒适层”和火焰惩罚统一按阶段集中定义，
 * 供方块规则、环境伤害和玩家生存判定共享同一套高度语义。
 */
public final class StageHeightConfig {
	private static final int DEFAULT_SAFE_HEIGHT = -64;
	private static final int DEFAULT_FIRE_SECONDS = 0;
	private static final float DEFAULT_FIRE_DAMAGE = 0.0f;

	public static final int STAGE_1_SAFE_HEIGHT = 512;
	public static final int STAGE_2_SAFE_HEIGHT = 64;
	public static final int STAGE_3_SAFE_HEIGHT = 32;
	public static final int STAGE_4_SAFE_HEIGHT = 8;
	public static final int STAGE_5_SAFE_HEIGHT = -16;
	public static final int STAGE_6_SAFE_HEIGHT = -64;

	public static final int STAGE_1_COZY_HEIGHT = 512;
	public static final int STAGE_2_COZY_HEIGHT = 48;
	public static final int STAGE_3_COZY_HEIGHT = 8;
	public static final int STAGE_4_COZY_HEIGHT = -16;
	public static final int STAGE_5_COZY_HEIGHT = -128;
	public static final int STAGE_6_COZY_HEIGHT = -64;

	public static final int STAGE_1_FIRE_SECONDS = 0;
	public static final int STAGE_2_FIRE_SECONDS = 1;
	public static final int STAGE_3_FIRE_SECONDS = 2;
	public static final int STAGE_4_FIRE_SECONDS = 3;
	public static final int STAGE_5_FIRE_SECONDS = 4;
	public static final int STAGE_6_FIRE_SECONDS = 0;

	public static final float STAGE_1_FIRE_DAMAGE = 0.0f;
	public static final float STAGE_2_FIRE_DAMAGE = 1.0f;
	public static final float STAGE_3_FIRE_DAMAGE = 2.0f;
	public static final float STAGE_4_FIRE_DAMAGE = 3.0f;
	public static final float STAGE_5_FIRE_DAMAGE = 4.0f;
	public static final float STAGE_6_FIRE_DAMAGE = 0.0f;

	/**
	 * 返回当前阶段的地表安全高度阈值。
	 * 高于该高度通常意味着更容易受到太阳灾变影响，也是多条地表退化规则的判定边界。
	 */
	public static int getSafeHeight(SolarStage stage) {
		return switch (stage) {
			case STAGE_1 -> STAGE_1_SAFE_HEIGHT;
			case STAGE_2 -> STAGE_2_SAFE_HEIGHT;
			case STAGE_3 -> STAGE_3_SAFE_HEIGHT;
			case STAGE_4 -> STAGE_4_SAFE_HEIGHT;
			case STAGE_5 -> STAGE_5_SAFE_HEIGHT;
			case STAGE_6 -> STAGE_6_SAFE_HEIGHT;
			default -> DEFAULT_SAFE_HEIGHT;
		};
	}

	/**
	 * 返回当前阶段相对舒适的高度层。
	 * 它比安全高度更严格，用于表达“仍可维持宜居感”的垂直范围。
	 */
	public static int getCozyHeight(SolarStage stage) {
		return switch (stage) {
			case STAGE_1 -> STAGE_1_COZY_HEIGHT;
			case STAGE_2 -> STAGE_2_COZY_HEIGHT;
			case STAGE_3 -> STAGE_3_COZY_HEIGHT;
			case STAGE_4 -> STAGE_4_COZY_HEIGHT;
			case STAGE_5 -> STAGE_5_COZY_HEIGHT;
			case STAGE_6 -> STAGE_6_COZY_HEIGHT;
			default -> DEFAULT_SAFE_HEIGHT;
		};
	}

	/**
	 * 返回当前阶段命中实体时附加的点燃秒数。
	 * 第六阶段回落为 0，表示坍缺期的生存压力不再来自高温灼烧。
	 */
	public static int getFireSeconds(SolarStage stage) {
		return switch (stage) {
			case STAGE_1 -> STAGE_1_FIRE_SECONDS;
			case STAGE_2 -> STAGE_2_FIRE_SECONDS;
			case STAGE_3 -> STAGE_3_FIRE_SECONDS;
			case STAGE_4 -> STAGE_4_FIRE_SECONDS;
			case STAGE_5 -> STAGE_5_FIRE_SECONDS;
			case STAGE_6 -> STAGE_6_FIRE_SECONDS;
			default -> DEFAULT_FIRE_SECONDS;
		};
	}

	/**
	 * 返回当前阶段的基础火焰伤害。
	 * 数值随喷发阶段逐步抬升，到坍缺阶段再次清零，与阶段主题保持一致。
	 */
	public static float getFireDamage(SolarStage stage) {
		return switch (stage) {
			case STAGE_1 -> STAGE_1_FIRE_DAMAGE;
			case STAGE_2 -> STAGE_2_FIRE_DAMAGE;
			case STAGE_3 -> STAGE_3_FIRE_DAMAGE;
			case STAGE_4 -> STAGE_4_FIRE_DAMAGE;
			case STAGE_5 -> STAGE_5_FIRE_DAMAGE;
			case STAGE_6 -> STAGE_6_FIRE_DAMAGE;
			default -> DEFAULT_FIRE_DAMAGE;
		};
	}

	private StageHeightConfig() {}
}
