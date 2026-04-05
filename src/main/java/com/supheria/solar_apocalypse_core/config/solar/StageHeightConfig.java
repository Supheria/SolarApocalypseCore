package com.supheria.solar_apocalypse_core.config.solar;

import com.supheria.solar_apocalypse_core.world.SolarStage;

/**
 * 阶段安全高度和伤害常量。
 */
public final class StageHeightConfig {
	private static final int DEFAULT_SAFE_HEIGHT = -64;
	private static final int DEFAULT_FIRE_SECONDS = 0;
	private static final float DEFAULT_FIRE_DAMAGE = 0.0f;

	public static final int STAGE_1_SAFE_HEIGHT = -64;
	public static final int STAGE_2_SAFE_HEIGHT = 63;
	public static final int STAGE_3_SAFE_HEIGHT = 32;
	public static final int STAGE_4_SAFE_HEIGHT = 8;
	public static final int STAGE_5_SAFE_HEIGHT = -16;
	public static final int STAGE_6_SAFE_HEIGHT = -64;

	public static final int STAGE_1_COZY_HEIGHT = -64;
	public static final int STAGE_2_COZY_HEIGHT = 63;
	public static final int STAGE_3_COZY_HEIGHT = 8;
	public static final int STAGE_4_COZY_HEIGHT = -16;
	public static final int STAGE_5_COZY_HEIGHT = -64;
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
