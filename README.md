# SolarApocalypseCore

`SolarApocalypseCore` 是为整合包《烈焰升腾》定制开发的核心模组，目标是承载太阳爆发、生存惩罚、世界演化与相关联动逻辑。

## 项目定位

- 面向 Minecraft `1.20.1`
- 基于 Forge `47.4.10`
- 使用 Java `17`
- 当前仓库定位为整合包专用核心实现，不以通用模组或公共开发库为目标

## 开发说明

### 环境要求

- Java 17
- 建议使用 IntelliJ IDEA
- 首次导入后执行一次 `gradlew genIntellijRuns`
- 开发运行优先使用 Gradle 的 `runClient` / `runServer`

常用命令：

```bash
./gradlew runClient
./gradlew runServer
./gradlew build
```

Windows 下可改用：

```powershell
.\gradlew.bat runClient
.\gradlew.bat runServer
.\gradlew.bat build
```

### 依赖模组

项目依赖了经过定制修改的 `Thirst` 模组，而不是上游原版仓库。当前使用的 fork 分支为：

- `https://github.com/Supheria/Thirst-Mod/tree/1.20.1-solar`

本仓库中本地开发使用的依赖包位于：

- `libs/ThirstWasTaken-1.20.1-1.3.15-solar.jar`

如需升级或重新构建该依赖，请以以上 fork 分支为准，不要直接按原始 `Thirst` 仓库行为做兼容性判断。

### API 与配置约束

- 当前模组没有公开 API
- 当前模组没有面向玩家或外部开发者开放的可配置文件
- 现有逻辑以整合包内容、数值平衡和内部实现需求为先

这意味着：

- 对外兼容性不是当前阶段的设计目标
- 开发时可以优先进行面向整合包场景的定制实现
- 若后续需要开放 API 或配置，应视为独立设计任务，而不是默认延伸现有实现

## 代码结构

- `src/main/java/com/supheria/solar_apocalypse_core/`：核心源码
- `src/main/resources/`：资源、语言文件、数据包内容、Mixin 配置
- `_docs/`：内部开发文档与排查记录
- `libs/`：本地依赖 Jar

当前代码大致按职责拆分为：

- `world/`：太阳阶段与世界级规则
- `handlers/`：事件驱动逻辑
- `transforms/`：方块与环境演化逻辑
- `mixin/`：对原版或依赖模组行为的注入
- `thirst/`：与口渴系统联动的逻辑
- `config/`：内部使用的规则与参数定义

## 相关文档

- `README.txt`：Forge MDK 默认说明
- `_docs/solar-stage-rules.md`：太阳阶段规则
- `_docs/environment-scheduler-architecture.md`：环境调度架构
- `_docs/thirst-integration.md`：Thirst 联动说明
- `_docs/forge-gradle-mixin-guide.md`：ForgeGradle / Mixin 排查

## 维护约定

- 优先保持实现简单直接，避免为了“通用化”提前抽象
- 如果改动涉及太阳阶段、环境惩罚或口渴联动，建议同步更新 `_docs/` 中对应文档
- 若 `Mixin` 行为异常，优先检查是否通过 Gradle 生成和启动运行配置
