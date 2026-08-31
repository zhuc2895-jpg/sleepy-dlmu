# DLMU 定制改动日志 · CHANGELOG

> 上游仓库：[lingion/sleepy](https://github.com/lingion/sleepy) · GPL-3.0
> 二次开发：[HK416](https://github.com/zhuc2895-jpg) · 本仓库：[zhuc2895-jpg/sleepy-dlmu](https://github.com/zhuc2895-jpg/sleepy-dlmu)

---

## v1.0.41-dlmu · 2026-08-31

**基线版本：** lingion/sleepy `v1.0.41`（versionCode 42）
**定制目标：** 大连海事大学 Dalian Maritime University（DLMU）— URP 综合教务系统（清元优软 URP_NEW 协议）

---

### 一、新增学校配置 · DLMU 教务系统接入

| 文件 | 改动摘要 |
|------|----------|
| `app/src/main/assets/schools.json` | 新增 `大连海事大学` 条目（排序在 D 字母分组，位于 schools.json:156-168） |

条目详情：
```jsonc
{
  "sortKey": "D",
  "name": "大连海事大学",
  "url": "https://www.dlmu.edu.cn/",
  "type": "urp_new",               // 匹配 JwNewUrpParser（URP 新版 JSON 课表解析）
  "aliases": ["dlmu", "海事大学", "海大"],
  "sortKeyFull": "dalianhaishidaxue",
  "enableFetch": false             // 暂不启用 WebView 内 fetch，保持默认 HTML 捕获路径
}
```

协议链路验证：
- `JwProtocol.TYPE_URP_NEW = "urp_new"`（`JwProtocol.kt:24`）
- `JwParserRegistry.parserFor("urp_new")` → `::JwNewUrpParser`（`JwParserRegistry.kt:53`）
- `JwProtocol.displayName("urp_new")` → `"URP 教务"`（UI 展示文案，`JwProtocol.kt:75`）

---

### 二、补充 enableFetch 字段解析

| 文件 | 改动行 | 说明 |
|------|--------|------|
| `app/src/main/java/com/lingion/sleepy/data/jw/JwImportViewModel.kt` | `parseSchoolsJson` 函数（L156-174） | 从 JSON 中读取 `enableFetch` 属性（缺省 `false`）注入 `JwSchoolInfo` |

```kotlin
// 新增行：L173
enableFetch = obj.optBoolean("enableFetch", false)
```

消费端：`JwFetchProtocol.kt:24-26` 会根据 `school.enableFetch` 与 URL 指纹决定是否使用 WebView 内 fetch 模式。
对大连海事大学设置为 `false`，避免在未验证的 URP 教务页面强行 fetch 导致 0 课结果。

---

### 三、新增海事蓝（Maritime）主题

| 文件 | 说明 |
|------|------|
| `app/src/main/java/com/lingion/sleepy/ui/theme/ThemePresets.kt` | 新增主题常量、Light/Dark 配色对、ThemePreset 实例并纳入全局列表 |
| `app/src/main/res/values/strings.xml` | 新增 `theme_name_maritime = "海事蓝"`（L553） |

**常量与配色：**

- `ThemePresets.KEY_MARITIME = "maritime"`（L27）
- **Maritime Light**：primary `#004A7C` / secondary `#4C5F7A` / tertiary `#006875` / background `#F7F9FC`
- **Maritime Dark**：primary `#96CBFF` / secondary `#B3C4DE` / tertiary `#82D1DE` / background `#101418`
- `ThemePresets.all` 末尾追加 `Maritime`（`ThemePresets.kt:448`）
- `ThemePresets.byKey("maritime")` → 返回 `Maritime` preset（L450-453）

主题切换入口：App 内 **我的 → 外观设置 → 主题**，选择"海事蓝"即可一键应用。

---

### 四、字符串资源（i18n zh-CN 主集）

| 键 | 值 | 用途 |
|----|----|------|
| `theme_name_maritime` | 海事蓝 | 主题选择器显示名 |
| `school_recommended` | 热门推荐 | 推荐区标题 |
| `school_recommended_tag` | 推荐 | 推荐学校卡片徽章 |
| `dlmu_intranet_hint` | 大连海事大学教务系统需在校内网络或 VPN 环境下访问 | 大连海事专属提示 |

位置：`app/src/main/res/values/strings.xml` L553-556。

---

### 五、学校选择页：热门推荐置顶区

| 文件 | 改动 |
|------|------|
| `app/src/main/java/com/lingion/sleepy/ui/screen/imports/SchoolSelectScreen.kt` | 新增 RECOMMENDED_SCHOOL_NAMES、RecommendedSection、RecommendedSchoolCard 并接入主布局 |

**细节清单：**

1. **L75-76**：定义 `RECOMMENDED_SCHOOL_NAMES = listOf("大连海事大学")`，可随时扩充推荐名单。
2. **L270-282**：仅在无搜索关键字（`query.isBlank()`）时，在字母分组 LazyColumn 上方独立渲染推荐区，不影响右侧字母索引栏计算。
3. **L597-639**：`RecommendedSection` — ⭐ Star 图标 + 主题色「热门推荐」标题 + 分割线，一张或多张推荐卡片。
4. **L641-734**：`RecommendedSchoolCard` — 独立卡片容器：
   - 主色（海事蓝）填充 Star 图标方形 Logo
   - 主色胶囊「推荐」徽章（school_recommended_tag）
   - 复用原有 `SchoolStatusBadge`（支持 supported / pending / grad / legacy 四种状态）
   - 副行显示协议：`URP 教务 · www.dlmu.edu.cn`
   - **DLMU 专属**：当 `school.name == "大连海事大学"` 时，渲染 Info 图标 + 内网/VPN 提示条（ℹ️ `dlmu_intranet_hint`）
5. **新增 import**：`Icons.Outlined.Info` / `Icons.Outlined.Star`。

---

### 六、构建适配（沙盒环境定制，非功能性业务改动）

| 文件 | 改动 |
|------|------|
| `app/build.gradle.kts` | `compileSdk / targetSdk = 36` · `versionName = "1.0.41-dlmu"`（L10 / L15 / L17） |
| `gradle.properties` | 代理配置（systemProp.http.proxyHost/Port） · `-Xmx1536m + UseSerialGC` 内存受限构建 · `workers.max=1` · `useFullClasspathForDexingTransform=true` · `forceDexingSingleThread=true` |

> 说明：AGP 9.1 在沙盒 ≈6GB RAM cgroup 约束下，compileSdk=37 会因 SDK 平台 manifest 小数版本（android-37.0 → ApiLevel 37.0）触发 `Failed to find target with hash string 'android-37'`，因此回落至整数基线 36；targetSdk=36 已满足 Google Play 2025 年要求。

---

### 七、APK 构建产物

构建命令：`./gradlew :app:assembleDebug`（JDK 17 / Gradle 9.3.1 / Android SDK Platform 36）

| ABI 文件名 | 架构 | 大小 | 内置 so |
|------------|------|------|---------|
| `app-arm64-v8a-debug.apk` | arm64-v8a | ≈19 MB | `libandroidx.graphics.path.so` · `libdatastore_shared_counter.so` |
| `app-armeabi-v7a-debug.apk` | armeabi-v7a | ≈19 MB | 同上（32 位版本） |
| `app-x86_64-debug.apk` | x86_64 | ≈19 MB | 同上（x86_64 版本） |

包元数据（aapt dump badging）：
```
package: name='com.lingion.sleepy' versionCode='42' versionName='1.0.41-dlmu-debug'
compileSdkVersion='36' application-label:'Sleepy 课程表'
```

---

## 致谢 & 署名

- **上游作者**：lingion — [Sleepy · 轻课表](https://github.com/lingion/sleepy)（GPL-3.0）
- **定制开发**：HK416（[@zhuc2895-jpg](https://github.com/zhuc2895-jpg)）
- **母校**：大连海事大学 Dalian Maritime University
