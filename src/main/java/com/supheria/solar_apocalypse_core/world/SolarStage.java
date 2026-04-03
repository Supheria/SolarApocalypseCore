package com.supheria.solar_apocalypse_core.world;

import net.minecraft.network.chat.Component;

/**
 * 太阳爆发阶段枚举
 * 定义了7个阶段及其属性和方法
 *
 * 阶段说明：
 * - NONE：未初始化
 * - STAGE_1 - STAGE_5：爆发阶段（等级 1-5）
 * - STAGE_6：坍缺阶段（恒冬期）
 */
public enum SolarStage {
    NONE("solarstage.none"),
    STAGE_1("solarstage.stage1"),
    STAGE_2("solarstage.stage2"),
    STAGE_3("solarstage.stage3"),
    STAGE_4("solarstage.stage4"),
    STAGE_5("solarstage.stage5"),
    STAGE_6("solarstage.stage6");

    private final String i18nKey;

    SolarStage(String i18nKey) {
        this.i18nKey = i18nKey;
    }

    /**
     * 获取国际化翻译键
     */
    public String getI18nKey() {
        return i18nKey;
    }

    /**
     * 获取本地化显示名称
     * 返回可翻译的 Component
     */
    public Component getDisplayName() {
        return Component.translatable(i18nKey);
    }

    /**
     * 判断是否为爆发阶段（STAGE_1 - STAGE_5）
     */
    public boolean isEruptionPhase() {
        return this != NONE && this != STAGE_6;
    }

    /**
     * 判断是否为坍缺阶段（STAGE_6）
     */
    public boolean isCollapsePhase() {
        return this == STAGE_6;
    }

    /**
     * 获取爆发等级 (1-5)
     * - NONE 返回 0
     * - STAGE_1 - STAGE_5 返回对应级别
     * - STAGE_6 返回 5
     * 用于伤害计算和太阳渲染进度
     */
    public int getEruptionLevel() {
        return Math.min(this.ordinal(), 5);
    }

    /**
     * 从序数值获取枚举
     * 用于从 NBT 或配置中还原枚举值
     *
     * @param ordinal 序数值（0-6）
     * @return 对应的 SolarStage，超出范围返回 NONE
     */
    public static SolarStage getByOrdinal(int ordinal) {
        if (ordinal >= 0 && ordinal < values().length) {
            return values()[ordinal];
        }
        return NONE;
    }

    /**
     * 判断是否至少达到指定阶段
     * 基于序数比较
     *
     * @param other 要比较的阶段
     * @return true 如果当前阶段 >= other
     */
    public boolean isAtLeast(SolarStage other) {
        return this.compareTo(other) >= 0;
    }

    /**
     * 判断是否在指定阶段之前
     *
     * @param other 要比较的阶段
     * @return true 如果当前阶段 < other
     */
    public boolean isBefore(SolarStage other) {
        return this.compareTo(other) < 0;
    }

    /**
     * 判断爆发等级是否至少达到指定等级
     * 用于替代 getEruptionLevel() >= level 的写法
     *
     * @param level 要比较的等级（0-5）
     * @return true 如果当前爆发等级 >= level
     */
    public boolean isEruptionLevelAtLeast(int level) {
        return this.getEruptionLevel() >= level;
    }

    /**
     * 判断爆发等级是否小于指定等级
     *
     * @param level 要比较的等级（0-5）
     * @return true 如果当前爆发等级 < level
     */
    public boolean isEruptionLevelBelow(int level) {
        return this.getEruptionLevel() < level;
    }
}
