# VMTools → VMFashion 复用清单

> 生成时间：2026-10-28 ｜ 源项目：`/root/vmtools-mod`（mc-26.3 分支，mod_version 4.0.2，Java 源码 6367 行）
> 目标：新工作区 `/root/VMFashion-mod`（已完成骨架落地：构建脚手架 + 第 2 节 8 个基础文件，`vm-jdk25 ./gradlew build` 通过，jar 为 `vmfashion-mc26.3-0.1.0.jar`）

## 0. 结论先行

- **真正值钱的不是业务代码，是「脚手架 + 交互范式」**：构建配置、Screen 骨架、配置/持久化层、Toast/搜索/颜色选择器，这些搬过去能省掉新 mod 前两周的基建工作。
- 基础设施层 **10 个文件约 1268 行**，几乎可以原样搬（改包名 + 文案）；`WaypointScreen` 那套 1391 行的自绘 GUI 是「范式」而非「代码」，建议抽 `BaseScreen` 后复用。
- 业务层（路径点/分组/传送/逃逸/冻结）**不要搬**，形状可以参考，逻辑与时尚模组无关。

## 1. 构建脚手架：100% 复用（改 4 个字段）

| 文件 | 说明 |
|------|------|
| `build.gradle` | Loom `net.fabricmc.fabric-loom` 1.18.4 + `splitEnvironmentSourceSets()` + `release = 25` |
| `settings.gradle` | Fabric / mavenCentral / pluginPortal 仓库配置 |
| `gradle.properties` | 版本矩阵集中处（见下） |
| `gradle/wrapper/*` + `gradlew` + `gradlew.bat` | Gradle 9.7.1 wrapper，jar 已就位，离线可用 |
| `.gitignore` | 现成的 Fabric 项目忽略规则 |

**已验证可编译的组合（照抄即可）：**

```properties
minecraft_version=26.3
loader_version=0.20.1
loom_version=1.18.4
fabric_api_version=0.165.0+26.3
# Java 25；fabric-api 用 implementation（不是 modImplementation）
```

**要改的 4 处：** `rootProject.name`、`gradle.properties` 的 `archives_base_name` / `maven_group`、`build.gradle` 里 `loom { mods { "vmtools" { ... } } }` 的 mod id。

构建命令（容器内已装 JDK 21/25，用 `vm-jdk25` 切换）：

```bash
cd /root/VMFashion-mod && vm-jdk25 ./gradlew build
```

## 2. 几乎零改动复用（改包名 + 文案即可）

| 文件 | 行数 | 作用 | 需要改什么 |
|------|-----:|------|-----------|
| `util/ChatUtil.java` | 76 | 发命令 / 发聊天 / 本地系统消息 / 动作栏消息，统一走 `client.execute()` 保证主线程 | `[VMTools]` 前缀 |
| `util/FileUtil.java` | 105 | 配置目录、导出目录、时间戳文件名、备份、可读写检查 | `vmtools` → `vmfashion` 目录名 |
| `keybind/KeybindManager.java` | 64 | 快捷键注册 + `ClientTickEvents` 消费 + 打开界面 | 键位、翻译键、目标 Screen |
| `gui/component/ToastWidget.java` | 231 | 全局 Toast 通知（成功/错误/警告，淡入淡出、堆叠） | 基本不用改 |
| `gui/component/SearchField.java` | 79 | 封装 `EditBox` + hint + 变更回调 + 去抖 | 提示文案「搜索路径点」 |
| `gui/ConfirmScreen.java` | 140 | 通用确认对话框，自带 `createDeleteConfirm()` 工厂 | 配色常量 |
| `config/ModConfig.java` | 185 | Gson 配置持久化骨架（load/save + 字段级 setter 夹紧取值范围） | 字段换成时装相关配置 |
| `feature/waypoint/UIState.java` | 122 | 多窗口位置/展开/宽高/层级顺序持久化 | 类名 → `UiState`，去掉 waypoint 语义 |

> 这 8 个文件是本项目最成熟的资产：都没有业务耦合，搬过去直接能跑。

## 3. 需要改造后复用

| 文件 | 行数 | 改造思路 |
|------|-----:|---------|
| `gui/component/ColorPicker.java` | 121 | 目前硬绑 `WaypointColor` 枚举，把类型换成 `List<Integer>` / `int[]` 即可通用 —— 服装染色场景正好用得上 |
| `feature/waypoint/WaypointManager.java` | 373 | 「CRUD + 原子写入 + `.backup.json` 备份 + `dataVersion` 迁移 + 损坏自动回滚」的持久化范式，改造成 `OutfitManager` / `ClosetManager` |
| `feature/waypoint/WaypointIO.java` | 145 | JSON 导入导出 + **手写 `LocalDateTime` TypeAdapter**（Java 25 反射受限，这段必须抄） |
| `gui/WaypointScreen.java` | 1391 | 暗色圆角面板 + 自绘标题栏 + 滚动列表 + 右键菜单 + 拖拽排序 + 搜索过滤 + 窗口拖动/缩放。建议**先抽出 `BaseScreen`/`ListPanel`**，别整文件照搬 |
| `gui/EditWaypointScreen.java` | 626 | 表单编辑页 + 命令输入补全下拉（↑/↓/Tab/Enter/Esc），编辑「套装/外观条目」可套用 |
| `gui/SettingsScreen.java` | 139 | 设置页骨架（开关/滑块/分组） |
| `gui/ImportConfirmScreen.java` | 185 | 导入前预览确认 |
| `feature/waypoint/TeleportService.java` | 147 | 只保留「向服务端发命令 + 延迟确认」的时序控制，若新 mod 需要 `/hat`、`/skin` 之类服务端命令可参考 |

## 4. 不要复用

- `feature/escape/*`（AutoEscape / HealthMonitor，共约 650 行）—— 与逃生/血量相关
- `feature/freeze/FreezeManager.java` + `mixin/LocalPlayerMixin.java`（182 行）—— 冻结移动包绕过检测，时装模组用不上（但 mixin 注册与日志验证的写法可以看一眼）
- `feature/waypoint/Waypoint.java` / `WaypointGroup.java` / `WaypointColor.java`（335 行）—— 只借鉴数据模型形状
- `assets/vmtools/icon.png`、`lang/*.json` 内容 —— 键名与文案全部要重做（但 `key.` 前缀规则要保留）

## 5. 已知坑位（本项目踩过的，新项目直接避开）

1. **Mojang 名 vs Yarn 名**：仓库根目录 `CLAUDE.md` 里的 API 对照表写的是 1.21.11 / 26.1 时代，**已经过时**；当前 `mc-26.3` 分支全部用 Mojang 官方名（`Screen` / `Button` / `EditBox` / `GuiGraphicsExtractor` / `extractRenderState` / `KeyMapping`）。
2. **`renderBackground()` 只能调一次**，重复调用会 `Can only blur once per frame` 崩溃。
3. **Gson 序列化 `LocalDateTime` 必须自定义 TypeAdapter**（Java 21+ 模块系统限制反射）。
4. **26.x 的 fabric-api 用 `implementation`**，不是 `modImplementation`。
5. 纯客户端模组：`fabric.mod.json` 里 `"environment": "client"` + `splitEnvironmentSourceSets()`，业务代码全放 `src/client/java`。
6. 快捷键翻译键必须有 `key.` 前缀（`key.category.<modid>.main`）。
7. 配置文件要留备份/迁移机制，更新版本时不能丢用户数据。

## 6. 多版本分支 = 现成的移植模板

`/root/vmtools-mod` 的远端有 5 个分支，同一份业务代码在不同 MC 版本下的写法可直接对照：

| 分支 | MC | Java | Loom | Mappings |
|------|----|------|------|----------|
| `main` | 1.21.11 | 21 | 1.16.2 | Yarn |
| `mc-1.21.6` | 1.21.6 | 21 | 1.9-SNAPSHOT | Yarn |
| `mc-26.1` | 26.1 | 25 | 1.16.3 | Mojang |
| `mc-26.2` | 26.2 | 25 | 1.17.7 | Mojang |
| `mc-26.3` | 26.3 | 25 | 1.18.4 | Mojang |

如果 VMFashion 以后要多版本，直接 `git checkout` 到对应分支看 `build.gradle` + import 差异即可，不用重新摸 API 映射。

## 7. 建议起步顺序（功能未定时）

1. 复制第 1 节脚手架 → 跑通 `vm-jdk25 ./gradlew build`，先有一个能编译的空客户端 mod。
2. 搬第 2 节 8 个基础文件 → 改包名 `com.venus.vmfashion`、mod id `vmfashion`。
3. 把 `WaypointScreen` 里「面板 + 标题栏 + 列表 + 右键菜单 + 滚动」抽成 `BaseScreen`，这是后面所有界面的地基。
4. 功能方向定了再写业务（衣柜/换装/染色/外观展示都长在第 3 步之上）。
