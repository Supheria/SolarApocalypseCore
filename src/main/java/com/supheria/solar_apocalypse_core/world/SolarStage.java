package com.supheria.solar_apocalypse_core.world;

import net.minecraft.network.chat.Component;

/**
 * 太阳灾变进程的离散阶段枚举。
 *
 * <p>该顺序同时承担两层语义：一是阶段本身的时间推进顺序，二是若干比较辅助方法与
 * SavedData 序列化时使用的 ordinal 顺序。因此调整枚举排列时必须同时考虑存档兼容性。
 */
public enum SolarStage {
    /**
     * 未初始化
     */
    NONE("solarstage.none"),
    /**
     * 爆发初期
     */
    STAGE_1("solarstage.stage1"),
    /**
     * 爆发前期
     */
    STAGE_2("solarstage.stage2"),
    /**
     * 爆发中期
     */
    STAGE_3("solarstage.stage3"),
    /**
     * 爆发后期
     */
    STAGE_4("solarstage.stage4"),
    /**
     * 终极期
     */
    STAGE_5("solarstage.stage5"),
    /**
     * 恒冬期（坍缩）
     */
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

//    /**
//     * 获取爆发等级 (1-5)
//     * - NONE 返回 0
//     * - STAGE_1 - STAGE_5 返回对应级别
//     * - STAGE_6 返回 5
//     * 用于伤害计算和太阳渲染进度
//     */
//    public int getEruptionLevel() {
//        return Math.min(this.ordinal(), 5);
//    }

    /**
     * 从持久化保存的 ordinal 恢复阶段枚举。
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
     * 判断当前阶段是否至少推进到指定阶段。
     * 这里依赖枚举声明顺序进行比较，因此顺序本身就是语义的一部分。
     */
    public boolean isAtLeast(SolarStage other) {
        return this.compareTo(other) >= 0;
    }

    /**
     * 判断当前阶段是否仍早于指定阶段。
     * 常用于把阶段语义映射成 gamerule 或伤害阈值开关。
     */
    public boolean isBefore(SolarStage other) {
        return this.compareTo(other) < 0;
    }

//    /**
//     * 判断爆发等级是否至少达到指定等级
//     * 用于替代 getEruptionLevel() >= level 的写法
//     *
//     * @param level 要比较的等级（0-5）
//     * @return true 如果当前爆发等级 >= level
//     */
//    public boolean isEruptionLevelAtLeast(int level) {
//        return this.getEruptionLevel() >= level;
//    }
//
//    /**
//     * 判断爆发等级是否小于指定等级
//     *
//     * @param level 要比较的等级（0-5）
//     * @return true 如果当前爆发等级 < level
//     */
//    public boolean isEruptionLevelBelow(int level) {
//        return this.getEruptionLevel() < level;
//    }
}
