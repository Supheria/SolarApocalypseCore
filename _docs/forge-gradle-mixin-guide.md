# Forge Mod 开发：Gradle 配置与 Mixin 完整排查指南

> 适用环境：Minecraft 1.20.1 · Forge 47.x (ForgeGradle 6) · MixinGradle 0.7 · Java 17  
> 本文记录了一次从 `SolarApocalypsePlus`（mod_id: `sap`）迁移到 `SolarApocalypseCore`（mod_id: `solar_apocalypse_core`）过程中遭遇的所有问题及其根本原因。

---

## 一、Mixin 不生效的根本原因

### 现象
- 运行时不生成 `.mixin.output` 文件夹
- `@Shadow`、`@Inject`、`@ModifyVariable` 注入均无效
- 太阳贴图无法替换，地形变换逻辑不执行
- 构建过程无任何报错，一切看起来正常

### 诊断方法

查看 `run/logs/latest.log` 开头的启动参数行：

```
# 正常（mixin 会被加载）
ModLauncher running: args [..., --mixin.config, mixins.sap.json]

# 异常（mixin 不会被加载）
ModLauncher running: args [..., --fml.mcpVersion, 20230612.114412]
# 参数列表在此结束，没有 --mixin.config
```

如果启动参数里没有 `--mixin.config`，Mixin 子系统虽然会启动（日志里能看到 `SpongePowered MIXIN Subsystem Version=0.8.5`），但没有任何配置被注册，所有注入都不会发生。

### 根本原因

**使用了 IntelliJ 顶部工具栏的运行按钮，而不是 Gradle 面板的 `runClient` 任务。**

这两种启动方式的本质区别：

| 启动方式 | 启动参数来源 | `--mixin.config` |
|---|---|---|
| Gradle 面板 → `runClient` | Gradle 实时构建，MixinGradle 动态注入 | ✅ 始终正确 |
| IntelliJ 顶部运行按钮 | 读取 `.idea/runConfigurations/runClient.xml` | ⚠️ 取决于 XML 是否是最新的 |

`runClient` 任务**不会**自动将 `--mixin.config` 写入 IntelliJ 的运行配置 XML。如果 XML 是在 `mixin { config '...' }` 添加之前生成的，里面就没有这个参数。

### 解决方案

**方案 A（推荐）：始终用 Gradle 面板启动游戏**

```
IntelliJ 右侧 Gradle 面板
→ Tasks
→ forgegradle runs
→ 双击 runClient
```

**方案 B：更新 IntelliJ 运行配置后再用工具栏**

修改 `mixin {}` 块后，先执行：
```
Gradle 面板 → Tasks → forgegradle runs → genIntellijRuns
```
执行完成后，`.idea/runConfigurations/runClient.xml` 会被更新，之后工具栏的运行按钮也能正确加载 mixin。

**重要：每次修改 `mixin {}` 块的内容后，都需要重新执行 `genIntellijRuns`。**

---

## 二、Mixin 构建链路说明

排查过程中曾怀疑构建配置有问题，实际上构建链路从未出现故障。理解这条链路有助于避免类似的误判。

### 构建期（`compileJava`）

正常工作的构建日志关键输出：

```
MixinGradle did not locate the diffplug APT plugin, skipping eclipse task configuration
Adding source set 'main' to mixin processor
: SpongePowered MIXIN Annotation Processor Version=0.8.5 (MixinGradle Version=0.7.38)
: Writing refmap to .../build/tmp/compileJava/compileJava-refmap.json
: Writing searge composite mappings to .../build/tmp/compileJava/compileJava-mappings.tsrg
```

这四行全部出现 = 构建链路正常，refmap 和 TSRG 映射都在正确生成。

### 运行期（`runClient`）

正常工作的运行日志关键输出：

```
[mixin/]: Selecting config mixins.sap.json
[mixin/]: Preparing mixins.sap.json (2)
[mixin/]: Mixing BlockStateBaseMixin from mixins.sap.json into net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase
[mixin/]: Mixing LevelRendererMixin from mixins.sap.json into net.minecraft.client.renderer.LevelRenderer
```

这四行全部出现 = 两个 mixin 类都成功注入。

### 可以忽略的警告

```
# 开发环境下 refmap 读不到是正常的，不影响功能
[mixin/]: Reference map 'mixins.sap.refmap.json' for mixins.sap.json could not be read.
If this is a development environment you can ignore this message

# Fernflower 不可用时 .mixin.output 不会生成，不代表 mixin 失效
[mixin/]: Fernflower could not be loaded, exported classes will not be decompiled.
NoClassDefFoundError: org/jetbrains/java/decompiler/main/extern/IResultSaver

# @Mutable @Final 写入警告，mixin 仍然执行了写入，不是错误
[mixin/]: Write access to @Mutable @Final field SUN_LOCATION:...
```

### 关于 `.mixin.output` 文件夹

`debug.export = true` 开启后，`.mixin.output` 会在以下条件下生成：
- mixin 配置被正确加载（`--mixin.config` 参数存在）
- Fernflower 反编译器可用（通常需要在 IntelliJ 内部运行）

在命令行 `runClient` 下 Fernflower 不可用，**不生成 `.mixin.output` 不等于 mixin 失效**，需要通过运行日志里的 `Mixing ... into ...` 来判断是否生效。

---

## 三、配置文件标准写法

### 3.1 文件结构

```
项目根目录/
├── settings.gradle
├── gradle.properties
├── build.gradle
└── gradle/wrapper/
    └── gradle-wrapper.properties
```

---

### 3.2 `settings.gradle`

```groovy
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = 'https://maven.minecraftforge.net/' }
        // 使用 Parchment 映射时需要
        // maven { url = 'https://maven.parchmentmc.org' }
    }
}

plugins {
    id 'org.gradle.toolchains.foojay-resolver-convention' version '0.5.0'
}

// 必须设置，否则 Gradle 用文件夹名作为项目名（可能含大写、连字符，不稳定）
rootProject.name = 'solar_apocalypse_core'
```

**常见错误：**
- 遗漏 `rootProject.name`

---

### 3.3 `gradle.properties`

```properties
org.gradle.jvmargs=-Xmx3G
org.gradle.daemon=false

minecraft_version=1.20.1
minecraft_version_range=[1.20.1,1.21)
forge_version=47.4.10
forge_version_range=[47,)
loader_version_range=[47,)

mapping_channel=official
mapping_version=1.20.1

# mod_id 必须全小写，符合 [a-z][a-z0-9_]{1,63}，与 @Mod 注解一致
mod_id=solar_apocalypse_core
mod_name=Solar Apocalypse Core
mod_license=MIT
mod_version=1.0.0-1.20.1-alpha-1.0

# mod_group_id 必须全小写，与 Java 包路径完全一致
# 大小写不一致会导致 Maven 坐标与实际包路径不匹配
mod_group_id=com.supheria.solar_apocalypse_core

mod_authors=Supheria
mod_description=The sun has erupted.
```

**常见错误：**
- `mod_group_id` 大小写与实际 Java 包路径不一致（如 `com.Supheria` vs `com.supheria`）

---

### 3.4 `build.gradle`

```groovy
// ① buildscript 块必须在文件最顶部
//    ForgeGradle 和 MixinGradle 通过此块加载，不走 pluginManagement
buildscript {
    repositories {
        maven { url = 'https://maven.minecraftforge.net/' }
        maven { url = 'https://repo.spongepowered.org/repository/maven-public/' }
    }
    dependencies {
        classpath group: 'net.minecraftforge.gradle', name: 'ForgeGradle', version: '[6.0,6.2)', changing: true
        // MixinGradle 没有 0.8.x 版本，最新为 0.7-SNAPSHOT（对应 0.7.38）
        // 必须从 SpongePowered maven 加载，不能用 plugins DSL 的 Plugin Portal 版本
        classpath group: 'org.spongepowered', name: 'mixingradle', version: '0.7-SNAPSHOT'
    }
}

// ② plugins 块只放不依赖 Forge/Mixin 生态的通用插件
plugins {
    id 'eclipse'
    id 'idea'
    id 'maven-publish'
}

// ③ apply plugin 顺序重要：ForgeGradle 必须在 MixinGradle 之前
//    MixinGradle 启动时检查 'minecraft' 扩展是否存在，FG 未先加载则报错
apply plugin: 'net.minecraftforge.gradle'
apply plugin: 'org.spongepowered.mixin'

version = mod_version
group = mod_group_id  // 注意：group 对应 mod_group_id，version 对应 mod_version，不要对调

base {
    archivesName = mod_id
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

minecraft {
    mappings channel: mapping_channel, version: mapping_version
    copyIdeResources = true

    runs {
        configureEach {
            workingDirectory project.file('run')
            property 'forge.logging.markers', 'REGISTRIES'
            property 'forge.logging.console.level', 'debug'

            mods {
                "${mod_id}" {
                    source sourceSets.main
                }
            }
        }

        client {
            property 'forge.enabledGameTestNamespaces', mod_id
        }

        server {
            property 'forge.enabledGameTestNamespaces', mod_id
            args '--nogui'
        }

        gameTestServer {
            property 'forge.enabledGameTestNamespaces', mod_id
        }

        data {
            workingDirectory project.file('run-data')
            args '--mod', mod_id, '--all',
                 '--output', file('src/generated/resources/'),
                 '--existing', file('src/main/resources/')
        }
    }
}

sourceSets.main.resources { srcDir 'src/generated/resources' }

repositories {}

dependencies {
    minecraft "net.minecraftforge:forge:${minecraft_version}-${forge_version}"
    // Mixin AP：:processor classifier 是包含所有依赖的 fat jar，版本须 >= Forge 内置版本
    annotationProcessor 'org.spongepowered:mixin:0.8.5:processor'
}

// ④ mixin 块：config 指令是关键
//    config 的作用：将 --mixin.config 参数注入到 Gradle 运行配置（runClient 等）
//    缺少 config 行 = 游戏启动时没有 --mixin.config 参数 = mixin 完全不生效
mixin {
    add sourceSets.main, 'mixins.sap.refmap.json'  // refmap 文件名须与 mixins.sap.json 中的 "refmap" 字段一致
    config 'mixins.sap.json'                        // ← 这行是 mixin 能被加载的关键

    debug.verbose = true
    debug.export = true  // 生产环境建议关闭
}

tasks.named('processResources', ProcessResources).configure {
    var replaceProperties = [
            minecraft_version      : minecraft_version,
            minecraft_version_range: minecraft_version_range,
            forge_version          : forge_version,
            forge_version_range    : forge_version_range,
            loader_version_range   : loader_version_range,
            mod_id                 : mod_id,
            mod_name               : mod_name,
            mod_license            : mod_license,
            mod_version            : mod_version,
            mod_authors            : mod_authors,
            mod_description        : mod_description,
    ]
    inputs.properties replaceProperties
    filesMatching(['META-INF/mods.toml', 'pack.mcmeta']) {
        expand replaceProperties + [project: project]
    }
}

tasks.named('jar', Jar).configure {
    manifest {
        attributes([
                'Specification-Title'     : mod_id,
                'Specification-Vendor'    : mod_authors,
                'Specification-Version'   : '1',
                'Implementation-Title'    : project.name,
                'Implementation-Version'  : project.jar.archiveVersion,
                'Implementation-Vendor'   : mod_authors,
                'Implementation-Timestamp': new Date().format("yyyy-MM-dd'T'HH:mm:ssZ"),
        ])
    }
    finalizedBy 'reobfJar'
}

publishing {
    publications {
        register('mavenJava', MavenPublication) {
            artifact jar
        }
    }
    repositories {
        maven {
            url "file://${project.projectDir}/mcmodsrepo"
        }
    }
}

tasks.withType(JavaCompile).configureEach {
    options.encoding = 'UTF-8'
}
```

---

### 3.5 `gradle-wrapper.properties`

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.1.1-bin.zip
networkTimeout=10000
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

---

## 四、Gradle 操作坑点速查

### 坑1：修改 `mixin {}` 块后必须重新生成运行配置

**触发场景：** 添加或修改了 `config`、`add` 等 mixin 配置后，用 IntelliJ 工具栏启动游戏，mixin 不生效。

**原因：** IntelliJ 的运行配置 XML 缓存了旧的启动参数，新的 `--mixin.config` 没有写入。

**解决：**
```
Gradle 面板 → Tasks → forgegradle runs → genIntellijRuns
```
或者永久避免此问题：**始终用 Gradle 面板的 `runClient` 任务启动游戏**。

---

### 坑2：`group` 和 `version` 赋值对调

**错误写法（来自部分旧模板）：**
```groovy
group = mod_version   // ← 错误，group 被赋值成了版本号字符串
version = mod_group_id
```

**正确写法：**
```groovy
version = mod_version
group = mod_group_id
```

---

### 坑3：`mod_group_id` 大小写与 Java 包路径不一致

**错误写法：**
```properties
mod_group_id=com.Supheria.solar_apocalypse_core  # S 大写
```

**Java 包路径：**
```java
package com.supheria.solar_apocalypse_core;  # 全小写
```

**后果：** Maven 坐标与实际包路径不匹配，可能导致打包产物的类路径混乱。

---

### 坑4：`settings.gradle` 缺少 `rootProject.name`

**后果：** Gradle 用项目文件夹名（可能含大写字母、连字符、版本号等）作为项目名，影响构建输出文件名和 IDE 识别。

**修复：**
```groovy
rootProject.name = 'solar_apocalypse_core'
```

---

### 坑5：MixinGradle 版本混淆

| Artifact | 版本 | 作用 |
|---|---|---|
| `org.spongepowered:mixingradle` | `0.7-SNAPSHOT`（最高 0.7.38） | **构建插件**，不存在 0.8.x |
| `org.spongepowered:mixin` | `0.8.5` | **运行时库**，做字节码注入 |

两者是完全独立的 artifact，版本号不对应，不要混淆。

---

### 坑6：资源文件中的命名空间引用忘记更新

mod_id 从 `sap` 改为 `solar_apocalypse_core` 后，以下位置都需要同步更新：

**Java 代码中的字符串字面量：**
```java
// ❌ 遗漏
new ResourceLocation("sap:textures/environment/sun_step1.png")
// ✅ 正确
new ResourceLocation("solar_apocalypse_core:textures/environment/sun_step1.png")
```

**JSON 资源文件中的引用（blockstates、models）：**
```json
{ "model": "solar_apocalypse_core:block/dust" }
```

**data 目录下 tag 文件中的方块/物品引用：**
```json
{ "values": ["solar_apocalypse_core:dust"] }
```

**配方文件中的物品引用（最容易遗漏）：**
```json
// ❌ 缺少命名空间，游戏加载时报 Unknown item 错误
{ "item": "redstone_coated_metal" }
// ✅ 正确
{ "item": "solar_apocalypse_core:redstone_coated_metal" }
```

---

### 坑7：资源文件名大小写（Linux/Mac 环境）

Windows 文件系统大小写不敏感，开发时不报错，但部署到 Linux 服务器或打包后会找不到文件。

**示例：** blockstates 引用 `measuring_instrument`，但 model 文件名为 `measuring_Instrument.json`（大写 I），在 Linux 上加载失败，方块变成紫黑块。

**规范：** 所有资源文件名统一使用小写加下划线。

---

### 坑8：Forge 语言文件和 FML 内部 jar 的 WARN 可以忽略

```
Mod file javafmllanguage-1.20.1-47.4.10.jar is missing mods.toml file
Mod file lowcodelanguage-1.20.1-47.4.10.jar is missing mods.toml file
```

这是 Forge 内部组件的正常状态，不是错误，不影响 mod 加载。

---

## 五、迁移时的完整检查清单

从旧 mod_id 迁移到新 mod_id 时，逐项确认：

**Java 源码**
- [ ] 所有 `package` 声明更新到新包路径
- [ ] 所有 `import` 语句更新
- [ ] `@Mod("旧id")` 改为 `@Mod(新Mod类.MOD_ID)`
- [ ] `new ResourceLocation("旧id:...")` 全部替换
- [ ] `DeferredRegister.create(..., "旧id")` 替换为引用 `MOD_ID` 常量

**资源文件**
- [ ] `assets/旧id/` → `assets/新id/`（目录重命名）
- [ ] `data/旧id/` → `data/新id/`（目录重命名）
- [ ] blockstates JSON 内的 `"model": "旧id:..."` 全部替换
- [ ] models JSON 内的 `"parent"` 和 `"textures"` 引用全部替换
- [ ] tag JSON 内引用本 mod 方块/物品的条目全部替换
- [ ] 配方 JSON 内的 `"item"` 引用确认带有完整命名空间

**mixin 配置**
- [ ] `mixins.sap.json` 中的 `"package"` 更新为新包路径
- [ ] `build.gradle` 的 `mixin { config '...' }` 存在且文件名正确
- [ ] 执行 `genIntellijRuns` 更新运行配置

**构建配置**
- [ ] `settings.gradle` 有 `rootProject.name`
- [ ] `gradle.properties` 的 `mod_group_id` 全小写且与 Java 包路径一致
- [ ] `build.gradle` 的 `version = mod_version` 和 `group = mod_group_id` 没有对调

---

## 六、版本参考

| 组件 | 版本 |
|---|---|
| Minecraft | 1.20.1 |
| Forge | 47.4.10 |
| ForgeGradle | `[6.0,6.2)` |
| MixinGradle | `0.7-SNAPSHOT`（0.7.38） |
| Mixin 运行时 | `0.8.5` |
| Gradle | `8.1.1` |
| Java | 17 |
