package com.supheria.solar_apocalypse_core.world;

/**
 * 太阳爆发阶段枚举
 * 6个固定阶段，按游戏天数自动转换：
 * - 阶段 1-4: 太阳爆发前四个阶段（自动平均分配时间）
 * - 阶段 5: 终极阶段
 * - 阶段 6: 太阳坍缩，进入永恒冬天
 */
public enum SolarPhase {
    NONE(0, "none"),                         // 未初始化
    WARMING(1, "warming"),                    // 升温期 (阶段1/4)
    ACCELERATION(2, "acceleration"),          // 加速期 (阶段2/4)
    PEAK(3, "peak"),                          // 峰值期 (阶段3/4)
    CRITICAL(4, "critical"),                  // 危机期 (阶段4/4)
    ULTIMATE(5, "ultimate"),                  // 终极期 (stage5StartTime之后)
    COLLAPSE(6, "collapse");                  // 坍缩/永恒冬天 (stage6StartTime之后)

    private final int legacyId;  // 向后兼容的旧数值（0-6）
    private final String displayName;

    SolarPhase(int legacyId, String displayName) {
        this.legacyId = legacyId;
        this.displayName = displayName;
    }

    /**
     * 从旧的数值ID转换为枚举
     * 用于向后兼容旧存档的 SolarFlare 字段
     */
    public static SolarPhase fromLegacyId(int id) {
        for (SolarPhase phase : values()) {
            if (phase.legacyId == id) {
                return phase;
            }
        }
        return NONE;
    }

    public int getLegacyId() {
        return legacyId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
