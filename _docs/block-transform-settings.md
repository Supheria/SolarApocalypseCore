# 方块转化设定说明

本文记录 `SolarApocalypseCore` 当前版本已经落地的方块转化系统。当前系统按六大类组织，`SimpleDecay` 已被拆除，不再作为独立类别存在。

对应核心代码：

- `src/main/java/com/supheria/solar_apocalypse_core/SolarApocalypseCoreMod.java`
- `src/main/java/com/supheria/solar_apocalypse_core/handlers/EnvironmentalTransformScheduler.java`
- `src/main/java/com/supheria/solar_apocalypse_core/environment/EnvironmentalDirtyTracker.java`
- `src/main/java/com/supheria/solar_apocalypse_core/transforms/**`

---

## 一、统一原则

当前系统统一遵循三条原则：

1. 阶段决定“允许发生什么”
2. 区块调度决定“每 tick 能发生多少”
3. 规则只决定“满足条件后怎么转”

当前不再使用旧的规则级速率门控来控制全局推进速度。

---

## 二、调度规则

### 1. 活动区块

- 以玩家所在区块为中心
- 基准视距 `6` 时，活动区块为 `3 x 3`
- 每 `100 tick` 检查玩家位置，位置变化后重建

### 2. 视距内区块

- 玩家有效视距范围内的所有区块
- 排除活动区块本身
- 有效视距 = `min(服务器视距, 玩家本地视距)`

### 3. 固定处理速率

- 活动区块：`10` 次转化 / tick
- 视距内区块：`5` 次转化 / tick

### 4. 写入预算

真实方块写入同样使用两档预算：

- 活动区块：`10` 次写入 / tick
- 视距内区块：`5` 次写入 / tick

真实火焰仍保留额外限制：

- 每区块最多排队 `8` 个真实火写入
- 每区块每 tick 最多落地 `2` 个真实火写入

---

## 三、六大类最终分类

### 1. 可燃类

定义：

- 主要表现为燃烧、焚毁、凋零、炭化的材料

包含：

- 原木
- 木板及木制结构件
- 竹制建材
- 羊毛、地毯
- 树叶

当前规则：

- `WoodBurn`
- `LeavesWither`

### 2. 融化蒸发类

定义：

- 主要表现为融化、蒸发、去水、失水、相变的材料

包含：

- 雪、雪块
- 冰类
- 水源块
- 含水方块
- 气泡柱
- 海绵、湿海绵

当前规则：

- `SnowMelt`
- `IceMelt`
- `WaterEvaporate`
- `WaterloggedDry`
- `BubbleEvaporate`
- `SpongeDry`

### 3. 小型植物作物物件类

定义：

- 生态性、小体积、非主体结构的植物与生态小物件

包含：

- 树苗
- 竹子、竹笋
- 花、草、灌木
- 睡莲
- 珊瑚及珊瑚扇
- 仙人掌
- 蛙卵
- 非空花盆
- 后续统一归口的作物类

当前规则：

- `BambooDecay`
- `SmallPlantDecay`
- `FlowerPotDecay`
- `PlantObjectDecay`

### 4. 地质类

定义：

- 土壤、砂质、石质、深层地层、熔岩等地质材料

包含：

- 草方块、泥土、粗泥土
- 沙子、粉尘、砂岩
- 苔藓石
- 石头、深板岩
- 圆石、砾石
- 黏土
- 可疑沙、可疑沙砾
- 虫蚀石材
- 岩浆

当前规则：

- `DirtChain`
- `MossyDecay`
- `StoneTo`
- `StoneChain`
- `LavaToObsidian`

### 5. 功能结构类

定义：

- 人工结构、机关、功能方块、特殊结构组件

包含：

- TNT
- 传送门
- 末地传送门框架
- 火把、旗帜、蜡烛、床
- 拉杆、绊线、活塞、铁轨
- 比较器、中继器、红石火把
- 钟、烟熏炉
- 蜂窝块、蜂蜜块、史莱姆块
- 蜘蛛网

当前规则：

- `TntIgnite`
- `ForceDelete`
- `EndFrameClear`
- `FunctionalStructureDecay`

### 6. 排除类

定义：

- 永久不参与常规太阳转化的保护对象

包含：

- `fire_resistance` 标签中的耐火对象
- `needs_diamond_tool` 标签中的高价值硬质对象
- 石链内部专门排除的矿物与资源存储块

说明：

- 命中排除类后，不进入其他五类

---

## 四、分类入口顺序

当前 `SolarApocalypseCoreMod.getBlockTransform(...)` 按以下顺序分发：

1. 排除类
2. 强语义功能结构类
   - 传送门
   - 末地框架
   - TNT
3. 融化蒸发类
4. 可燃类
5. 小型植物作物物件类
6. 地质类
7. 普通功能结构类

这样做的目的：

- 先保证免疫对象不会被误处理
- 再保证强语义结构不会被泛分类截获
- 之后按自然环境链路依次分类

---

## 五、`SimpleDecay` 的最终处理

`SimpleDecay` 已被拆除，原内容被分流到三类：

### 1. 小型植物作物物件类

- 花
- 灌木
- 睡莲
- 珊瑚
- 仙人掌
- 蛙卵

### 2. 地质类

- `suspicious_sand`
- `suspicious_gravel`
- `infested_*`

### 3. 功能结构类

- 火把
- 旗帜
- 蜡烛
- 床
- 铁轨
- 拉杆
- 绊线
- 活塞
- 中继器
- 比较器
- 钟
- 烟熏炉
- 蜂窝块
- 蜂蜜块
- 史莱姆块
- 蜘蛛网
- 煤炭块

---

## 六、当前规则风格

当前规则已完成第二轮统一：

- 不再使用旧的按天数递增随机门控
- 不再依赖规则级速率表推进全局节奏
- 主要保留以下条件组合：
  - 阶段
  - 白天/夜间
  - 是否露天
  - 是否下雨
  - 是否高于安全高度或蒸发高度
  - 是否为水源块 / 含水块 / 相邻岩浆等

也就是说，当前系统已经从“概率触发型规则堆叠”转为“分类明确、条件明确、调度统一”的结构。

---

## 七、后续维护约束

后续新增或调整规则时，应遵守：

1. 新方块必须先归入六大类之一
2. 不允许再新增 `SimpleDecay` 这类杂项桶
3. 不要把全局速率重新塞回单条规则
4. 新规则优先使用阶段条件和环境条件，不再引入新的日数概率门
5. 排除类永远优先于其他所有分类

这样可以保持系统的三个核心特征：

- 分类清楚
- 调度统一
- 行为可推导
