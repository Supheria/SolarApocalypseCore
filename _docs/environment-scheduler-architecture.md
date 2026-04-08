# 环境调度架构

## 目标

- 太阳环境转变不再依赖原版 `randomTick`
- 优先保证实体同步稳定
- 控制大面积环境更新对 FPS 和区块重建的冲击

## 主链路

### 规则缓存

入口：`SolarApocalypseCoreMod`

- `getCachedBlockTransform(BlockState)`
- `getCachedOnPlaceTransform(BlockState)`

普通环境转变由调度器触发，必须立即修正的内容仍走 `onPlace`。

### 区块候选

入口：`handlers/EnvironmentalTransformScheduler.java`

当前同时维护两类候选区块：

- 近域：玩家周围区块持续保活，保证眼前区域稳定推进
- 远域：更大半径内随机选点，并按时间轮换方向，避免近远区域长期割裂

### 位置采样

调度器会从候选区块中采样以下位置：

- 树冠表面
- 无树叶地表
- 海床
- 树冠下方少量位置
- 地表以下浅层
- 地表到最低高度间的随机高度

### 预算执行

调度器只负责选点和触发规则。

真实方块写入由 `EnvironmentalDirtyTracker` 按类型预算分批提交，避免尖峰卡顿。

## 火焰处理

- 真实 `FIRE` 写入统一走 `EnvironmentalDirtyTracker`
- 每个 chunk 有排队上限和每 tick 落地上限
- 树叶、木制类、竹子不再生成真实火方块
- 满足焚毁条件时，按原版掉落后直接销毁为 `AIR`
- TNT 仍保留真实点火路径

## 水处理

- 普通水蒸发只处理水源块
- 水蒸发扩散只处理水源块
- 水预算统计只统计水源块
- 表层冻水只处理表层水源块
- 气泡柱只在底部失去水源连接时才会被清除
- `waterlogged` 方块仍单独走去水逻辑

## 关键参数

### EnvironmentalTransformScheduler

文件：`src/main/java/com/supheria/solar_apocalypse_core/handlers/EnvironmentalTransformScheduler.java`

- `MIN_INDEXED_CHUNKS_PER_PLAYER = 18`
- `CHUNK_INDEX_BUDGET_MULTIPLIER = 5`
- `CHUNK_REFRESHES_PER_PLAYER = 6`
- `FAR_CHUNK_REFRESHES_PER_PLAYER = 8`
- `PLAYER_KEEPALIVE_RADIUS = 1`
- `MIN_CHUNK_RADIUS = 2`
- `MAX_CHUNK_RADIUS = 6`
- `FAR_RADIUS_MULTIPLIER = 3`
- `FAR_RADIUS_PADDING = 4`
- `FAR_RING_THICKNESS = 4`
- `FAR_TIME_SLICE_TICKS = 200`
- `INDEX_INTERVAL_TICKS = 20`
- `REFILL_INTERVAL_TICKS = 4`
- `MIN_CHUNK_SAMPLES_PER_TICK = 8`
- `CHUNK_SAMPLES_PER_PLAYER = 4`
- `POSITIONS_PER_CHUNK_SAMPLE = 4`
- `SUBSURFACE_SAMPLE_DEPTH = 12`
- `CANOPY_UNDER_SAMPLE_DEPTH = 3`
- `MAX_QUEUE_MULTIPLIER = 16`

### EnvironmentalDirtyTracker

文件：`src/main/java/com/supheria/solar_apocalypse_core/environment/EnvironmentalDirtyTracker.java`

- `MAX_FIRE_QUEUE_PER_CHUNK = 8`
- `MAX_FIRE_WRITES_PER_CHUNK_PER_TICK = 2`

预算：

- `WATER = getWaterSpreadBudget(stage) * 4`
- `ICE = getStageSpreadBudget(stage) * 4`
- `FIRE = max(1, getStageSpreadBudget(stage))`
- `STONE = getStageSpreadBudget(stage) * 3`
- `SURFACE = getStageSpreadBudget(stage) * 4`
- `STAGE6_SURFACE = max(4, getCollapseSnowStepBudget() * getCollapseSnowSampleCount())`

### SolarStageConfig

文件：`src/main/java/com/supheria/solar_apocalypse_core/config/solar/SolarStageConfig.java`

- `RANDOM_TICKING_LEVELS = {4, 5, 5, 6, 7, 7}`
- `STAGE_TRANSFORM_RATES = {0.22, 0.35, 0.56, 0.75, 0.9, 1.0}`
- `STAGE_SPREAD_BUDGETS = {2, 3, 5, 7, 9, 10}`
- `WATER_SPREAD_BUDGETS = {3, 4, 7, 10, 13, 16}`

## 当前取舍

收益：

- 实体卡顿明显下降
- 近处区块持续推进，远处区块也会逐步跟进
- 大面积着火和水体变化更可控

代价：

- 环境扩张更平滑，不再是原先那种随机刻爆发式推进
- 树叶和木制类更偏“焚毁”而不是长时间明火燃烧
