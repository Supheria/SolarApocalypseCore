# Thirst 联动说明

本文记录 `SolarApocalypseCore` 与定制版 `Thirst` 模组的当前联动方式，供后续开发、升级依赖和排查问题时参考。

## 一、依赖来源

当前项目依赖的不是上游原版 `Thirst`，而是为本整合包定制维护的 fork。

- GitHub 分支：`https://github.com/Supheria/Thirst-Mod/tree/1.20.1-solar`
- 本地源码目录：`C:\Users\supheria\Documents\Projects\Thirst-Mod`
- 当前 Core 项目使用的本地依赖包：`libs/ThirstWasTaken-1.20.1-1.3.15-solar.jar`

版本对应关系：

- `SolarApocalypseCore/build.gradle` 中声明依赖：`dev.ghen.thirst:ThirstWasTaken:1.20.1-1.3.15-solar`
- `Thirst-Mod/gradle.properties` 中版本：`mod_version=1.20.1-1.3.15-solar`

## 二、联动目标

当前联动的核心目标只有一项：

- 根据太阳阶段和玩家高度，动态放大 `Thirst` 的口渴消耗倍率

这套联动只影响口渴值的消耗速度，不直接改写 `Thirst` 的基础口渴系统、饮水逻辑或物品配置。

同时，`SolarApocalypseCore` 还会独立施加一些与高温/脱水相关的负面效果，但这些效果属于 Core 自己的生存规则，不是通过 `Thirst` 模组实现的。

## 三、当前调用链

### 1. Thirst 侧扩展点

定制版 `Thirst` 在以下位置提供了可被其他模组覆盖的倍率钩子：

- `C:\Users\supheria\Documents\Projects\Thirst-Mod\src\main\java\dev\ghen\thirst\api\ThirstHelper.java`
- 方法：`public static float getExhaustionMultiplier(Player player)`

当前实现返回固定值：

```java
public static float getExhaustionMultiplier(Player player) {
    return 1.0f;
}
```

该方法在 `Thirst` 的口渴累积流程中被调用：

- `C:\Users\supheria\Documents\Projects\Thirst-Mod\src\main\java\dev\ghen\thirst\content\thirst\PlayerThirst.java`
- 方法：`addExhaustion(Player player, float amount)`

关键逻辑：

```java
exhaustion += (amount *
        ThirstHelper.getExhaustionBiomeModifier(player) *
        ThirstHelper.getExhaustionMultiplier(player) *
        ThirstHelper.getExhaustionFireProtModifier(player) *
        ThirstHelper.getExhaustionFireResistanceModifier(player)
);
```

也就是说，`SolarApocalypseCore` 只要改写这个返回值，就能在不侵入 `PlayerThirst` 主逻辑的前提下接管额外倍率。

### 2. Core 侧注入点

`SolarApocalypseCore` 通过 Mixin 注入 `ThirstHelper`：

- `src/main/java/com/supheria/solar_apocalypse_core/mixin/ThirstHelperSolarMixin.java`

注入目标：

- `dev.ghen.thirst.api.ThirstHelper`
- 方法：`getExhaustionMultiplier`

当前逻辑是在原返回值基础上再乘以太阳阶段倍率：

```java
@Inject(method = "getExhaustionMultiplier", at = @At("RETURN"), cancellable = true, remap = false)
private static void onGetExhaustionMultiplier(Player player, CallbackInfoReturnable<Float> cir) {
    float solarMultiplier = SolarThirstHelper.getExhaustionMultiplier(player);
    if (solarMultiplier != 1.0f) {
        cir.setReturnValue(cir.getReturnValue() * solarMultiplier);
    }
}
```

其中 `remap = false` 是必须的，因为目标类来自外部模组 `dev.ghen.thirst.api.ThirstHelper`，不是 Mojang/Forge 映射命名空间中的类。

该 Mixin 已注册在：

- `src/main/resources/mixins.main.json`

## 四、Core 内部判定规则

倍率计算位于：

- `src/main/java/com/supheria/solar_apocalypse_core/thirst/SolarThirstHelper.java`

当前规则：

1. 创造模式、旁观模式、客户端侧直接返回 `1.0`
2. 读取当前世界太阳阶段 `SolarStage`
3. 只有第二到第五阶段才属于“脱水阶段”
4. 只有玩家高度 `y >= cozyHeight` 时，才进入脱水激活区间
5. 在激活区间内按太阳阶段返回额外倍率

阶段倍率如下：

| 太阳阶段 | 额外口渴倍率 |
|---|---:|
| 第一阶段 | `1.0` |
| 第二阶段 | `3.0` |
| 第三阶段 | `5.0` |
| 第四阶段 | `7.0` |
| 第五阶段 | `9.0` |
| 第六阶段 | `1.0` |

这意味着当前设计中：

- 第二至第五阶段使用 `Thirst` 系统来表现高温脱水加速
- 第六阶段不再继续提高 `Thirst` 消耗倍率，而是改由 Core 自己施加常驻生存惩罚

## 五、与热浪负面效果的关系

需要区分两套独立机制：

### 1. Thirst 联动倍率

- 位置：`SolarThirstHelper`
- 作用：放大口渴 exhaustion 累积速度
- 实现方式：Mixin 注入 `ThirstHelper.getExhaustionMultiplier`

### 2. Core 自身热浪惩罚

- 位置：`src/main/java/com/supheria/solar_apocalypse_core/procedures/stats/HeatEffect.java`
- 作用：按太阳阶段和高度施加虚弱、饥饿、燃烧、缓慢等效果
- 实现方式：Forge 事件中直接处理实体 tick

因此，“脱水负面效果”和“口渴值加速消耗”是并行关系，不是同一个系统。

## 六、为什么需要定制版 Thirst

当前联动依赖 `ThirstHelper.getExhaustionMultiplier(Player)` 这个扩展点。该方法在当前 fork 中被明确保留为外部模组可接管的入口，并在 `PlayerThirst.addExhaustion` 中参与最终倍率计算。

如果后续切回上游 `Thirst`，必须先确认以下几点是否仍然成立：

1. `dev.ghen.thirst.api.ThirstHelper` 仍然存在
2. `getExhaustionMultiplier(Player)` 方法签名未变化
3. `PlayerThirst.addExhaustion` 仍然会调用该方法
4. 对应类名、包名、Mixin 注入点和加载时机没有变化

只要其中任意一点改变，当前联动就可能失效或崩溃。

## 七、升级或重建依赖时的检查项

当你更新 `Thirst-Mod` fork 并重新生成 `libs/ThirstWasTaken-*.jar` 时，至少检查以下内容：

1. `ThirstHelper.getExhaustionMultiplier(Player)` 仍然存在
2. `PlayerThirst.addExhaustion` 仍然调用该方法
3. `SolarApocalypseCore` 中 `ThirstHelperSolarMixin` 目标类和目标方法未失效
4. 运行 `runClient` 后日志中没有 Mixin 注入失败、目标方法找不到或类找不到错误
5. 第二至第五阶段高处活动时，口渴条消耗速度确实明显提升

如需排查 Mixin 相关问题，可结合：

- `_docs/forge-gradle-mixin-guide.md`

## 八、如何重新构建并替换 Thirst 依赖

本地 `Thirst` 定制模组源码目录：

- `C:\Users\supheria\Documents\Projects\Thirst-Mod`

### 1. 在 Thirst-Mod 中重新构建

推荐直接在 `Thirst-Mod` 仓库根目录执行：

```powershell
.\gradlew.bat build
```

该项目的 `jar` 任务会自动接 `reobfJar`，因此正常构建后会产出可用于实际运行的 jar。

如果只是想发布到本地 Maven 仓库目录，也可以执行：

```powershell
.\gradlew.bat publish
```

发布目标在 `Thirst-Mod/build.gradle` 中配置为：

- `file://<Thirst-Mod>/mcmodsrepo`

### 2. 找到生成产物

通常优先检查以下位置：

- `C:\Users\supheria\Documents\Projects\Thirst-Mod\build\libs\`

如果走的是 `publish`，则检查：

- `C:\Users\supheria\Documents\Projects\Thirst-Mod\mcmodsrepo\`

版本号应与当前 fork 保持一致，例如：

- `ThirstWasTaken-1.20.1-1.3.15-solar.jar`

### 3. 替换 Core 项目中的本地依赖

`SolarApocalypseCore` 当前通过 `flatDir { dirs 'libs' }` 使用本地 jar，因此实际运行时读取的是：

- `C:\Users\supheria\Documents\Projects\SolarApocalypseCore\libs\ThirstWasTaken-1.20.1-1.3.15-solar.jar`

也就是说，更新依赖时最直接的做法是：

1. 在 `Thirst-Mod` 中重新构建出新 jar
2. 用新 jar 替换 `SolarApocalypseCore/libs/` 中对应文件
3. 如版本号发生变化，同步修改 `SolarApocalypseCore/build.gradle` 中的依赖声明

当前依赖声明位置：

- `SolarApocalypseCore/build.gradle`
- `implementation fg.deobf("dev.ghen.thirst:ThirstWasTaken:1.20.1-1.3.15-solar")`

### 4. 替换后建议执行的验证

在 `SolarApocalypseCore` 根目录至少执行一次：

```powershell
.\gradlew.bat build
```

如需实际验证联动，再执行：

```powershell
.\gradlew.bat runClient
```

重点确认：

1. `ThirstHelperSolarMixin` 没有注入失败
2. 游戏能正常进入世界
3. 第二至第五阶段处于脱水高度时，口渴消耗明显加快
4. 第六阶段不会继续应用当前这套口渴倍率放大逻辑

## 九、当前设计边界

- 当前没有对外公开的 `Thirst` 联动 API
- 当前没有把太阳阶段倍率做成可配置文件
- 当前联动逻辑默认服务于整合包《烈焰升腾》的固定玩法设计

因此，如果后续要支持更多口渴模组、开放兼容层或暴露配置项，应作为单独的系统设计处理，而不是继续在现有 Mixin 基础上叠加临时逻辑。
