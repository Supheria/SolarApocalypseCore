# 太阳阶段规则说明

本文记录 SolarApocalypseCore 当前的太阳阶段持续时间与高度惩罚规则，作为后续调参与实现检查的统一说明。

---

## 一、阶段持续时间

当前设定如下：

| 阶段 | 持续天数 | 累计结束天数 | 起始时间（ticks） |
|---|---:|---:|---:|
| 第一阶段 | 5 | 5 | 0 |
| 第二阶段 | 7 | 12 | 120000 |
| 第三阶段 | 8 | 20 | 288000 |
| 第四阶段 | 10 | 30 | 480000 |
| 第五阶段 | 20 | 50 | 720000 |
| 第六阶段 | 无限 | - | 1200000 |

说明：
- Minecraft 每天按 `24000 ticks` 计算。
- 第六阶段从第 50 天结束后开始。
- 当前实现使用独立阶段边界，不再使用“前四阶段平均切分”的旧逻辑。

对应代码：
- `src/main/java/com/supheria/solar_apocalypse_core/config/solar/SolarStageConfig.java`
- `src/main/java/com/supheria/solar_apocalypse_core/world/SolarStageHelper.java`

---

## 二、高度惩罚三段式规则

爆发阶段（第二至第五阶段）统一遵循以下三段式规则：

1. **低于舒适高度（`y < cozyHeight`）**
   - 无负面效果。

2. **高于或等于舒适高度，且不高于安全高度（`cozyHeight <= y <= safeHeight`）**
   - 只有脱水效果。
   - 当前脱水表现为：
     - 虚弱
     - 挖掘疲劳
     - 缓慢

3. **高于安全高度（`y > safeHeight`）**
   - 会着火。
   - 同时也会处于脱水区间，因此依然会受到脱水效果。

说明：
- 第一阶段不属于爆发惩罚阶段，整体应视为安全。
- 第六阶段不再使用这套高温灼烧逻辑。

对应代码：
- `src/main/java/com/supheria/solar_apocalypse_core/thirst/SolarThirstHelper.java`
- `src/main/java/com/supheria/solar_apocalypse_core/procedures/stats/HeatEffect.java`
- `src/main/java/com/supheria/solar_apocalypse_core/config/solar/StageHeightConfig.java`

---

## 三、当前高度配置

| 阶段 | 舒适高度 `cozyHeight` | 安全高度 `safeHeight` | 规则解释 |
|---|---:|---:|---|
| 第一阶段 | 512 | 512 | 舒适高度高于地图正常高度，等于全图都属于舒适区，无负面效果 |
| 第二阶段 | 48 | 63 | 48 以下安全；48~63 脱水；63 以上着火 |
| 第三阶段 | 8 | 32 | 8 以下安全；8~32 脱水；32 以上着火 |
| 第四阶段 | -16 | 8 | -16 以下安全；-16~8 脱水；8 以上着火 |
| 第五阶段 | -128 | -16 | 舒适高度低于地图正常高度，等于地图内几乎不存在舒适区；-128~-16 脱水；-16 以上着火 |
| 第六阶段 | -64 | -64 | 不使用该阶段的高温灼烧规则 |

---

## 四、特殊设计约束

### 1. 第一阶段

第一阶段要求：

- 舒适高度高于地图高度；
- 即所有正常地图高度都应视为舒适高度；
- 因此玩家在第一阶段不应因为高度受到脱水或着火惩罚。

当前通过以下配置实现：
- `STAGE_1_COZY_HEIGHT = 512`
- `STAGE_1_SAFE_HEIGHT = 512`

### 2. 第五阶段

第五阶段要求：

- 舒适高度低于地图高度；
- 即地图内所有正常高度都不再属于舒适区；
- 玩家要么处于脱水区，要么处于危险着火区。

当前通过以下配置实现：
- `STAGE_5_COZY_HEIGHT = -128`
- `STAGE_5_SAFE_HEIGHT = -16`

---

## 五、当前火焰强度配置

| 阶段 | 点燃秒数 | 火焰伤害 |
|---|---:|---:|
| 第一阶段 | 0 | 0.0 |
| 第二阶段 | 1 | 1.0 |
| 第三阶段 | 2 | 2.0 |
| 第四阶段 | 3 | 3.0 |
| 第五阶段 | 4 | 4.0 |
| 第六阶段 | 0 | 0.0 |

对应代码：
- `src/main/java/com/supheria/solar_apocalypse_core/config/solar/StageHeightConfig.java`

---

## 六、实现校验要点

后续如果调整数值，需要确保以下条件始终成立：

- 第二至第五阶段必须满足：`cozyHeight < safeHeight`
- 第一阶段必须满足：`cozyHeight` 高于地图正常高度
- 第五阶段必须满足：`cozyHeight` 低于地图正常高度
- 着火判定必须继续使用：`y > safeHeight`
- 脱水判定必须继续使用：`y >= cozyHeight`

只要这些条件成立，就能保持当前的三段式行为不被破坏。
