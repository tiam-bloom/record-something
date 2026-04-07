# 项目运行问题记录

## 运行时间
2026-04-07

## 遇到的问题及解决方案

### 1. Gradle Wrapper JAR 缺失
**问题**: `gradle-wrapper.jar` 文件不存在，导致 `./gradlew` 无法执行
```
Could not find org.gradle.wrapper.GradleWrapperMain
ClassNotFoundException: org.gradle.wrapper.GradleWrapperMain
```

**解决**: 从 GitHub 下载 gradle-wrapper.jar
```bash
curl -L -o gradle/wrapper/gradle-wrapper.jar \
  https://github.com/gradle/gradle/raw/v8.9.0/gradle/wrapper/gradle-wrapper.jar
```

### 2. Android SDK 配置不完整
**问题**: SDK 缺少必要的 Platform 35 和 Build-Tools，导致编译失败
```
java.io.IOException: 文件名、目录名或卷标语法不正确
```

**原因**:
- `platforms/android-35` 未安装
- `build-tools` 目录结构不正确（文件直接放在 `build-tools/` 而非 `build-tools/34.0.0/`）

**解决**:
1. 接受 SDK 许可协议并手动创建 license 文件:
```bash
mkdir -p F:/android-sdk/licenses
echo -e "\n24333f8a63b6825ea9c5514f83c2829b004d1fee" > F:/android-sdk/licenses/android-sdk-license
echo -e "\n84831b9409646a918e30573bab4c9c91346d8abd" >> F:/android-sdk/licenses/android-sdk-license
echo -e "\nd975f751698a77b662f1254ddbeed3901e976f5a" >> F:/android-sdk/licenses/android-sdk-license
```

2. 安装缺失组件:
```bash
F:/android-sdk/cmdline-tools/bin/sdkmanager.bat --install \
  "platforms;android-35" "build-tools;35.0.1" --sdk_root="F:/android-sdk"
```

3. 修复 `local.properties` 中的路径格式（改为正斜杠）:
```
sdk.dir=F:/android-sdk
```

### 3. Compose BOM 版本过旧
**问题**: 代码中使用了新版 Material Icons 的 `AutoMirrored` API，但 BOM 版本为 `2023.10.01`，不支持

**编译错误**:
```
Unresolved reference 'automirrored'
Unresolved reference 'AutoMirrored'
Unresolved reference 'HorizontalDivider'
```

**解决**: 更新 `app/build.gradle.kts` 中的 Compose BOM 版本:
```kotlin
// 旧版本
implementation(platform("androidx.compose:compose-bom:2023.10.01"))
// 新版本
implementation(platform("androidx.compose:compose-bom:2024.02.00"))
```

## 最终成功配置

| 组件 | 版本 |
|------|------|
| JDK | OpenJDK 17.0.2 |
| Gradle | 8.9 (wrapper) |
| Android SDK Platform | 35 |
| Android SDK Build-Tools | 35.0.1 |
| Compose BOM | 2024.02.00 |
| Android Gradle Plugin | 8.5.2 |
| Kotlin | 2.0.21 |

## 构建命令

```bash
# Debug 构建
./gradlew assembleDebug

# 安装到设备
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 启动应用
adb shell am start -n com.assetmanager.app/.MainActivity
```

## APK 信息
- 路径: `app/build/outputs/apk/debug/app-debug.apk`
- 大小: ~16MB

## 警告信息（不影响运行）
- AGP 8.5.2 建议使用 compileSdk 34，当前为 35（可通过 `android.suppressUnsupportedCompileSdk=35` 抑制）
- 部分 Icons API 已废弃，建议使用 AutoMirrored 版本
- statusBarColor API 已废弃

## 后续建议
1. 将 `compileSdk` 降级至 34 以匹配 AGP 8.5.2 的官方支持
2. 更新废弃的 Icons API 使用 AutoMirrored 版本
3. 考虑升级 AGP 至更新版本以支持 compileSdk 35

---

# 应用闪退问题修复 (2026-04-07)

## 问题描述
应用安装成功后打开时立即闪退，adb logcat 显示:
```
java.lang.RuntimeException: Cannot create an instance of class com.assetmanager.app.viewmodel.HomeViewModel
Caused by: java.lang.NoSuchMethodException: com.assetmanager.app.viewmodel.HomeViewModel.<init> []
```

## 根因分析
**Hilt 依赖注入版本不一致**

| 模块 | Hilt 版本 |
|------|-----------|
| app | 2.48.1 |
| core | 2.51.1 |
| 根插件 | 2.51.1 |

app 模块与 core 模块使用不同版本的 Hilt，导致 Dagger/Hilt 的注解处理器生成的代码不兼容，ViewModel 无法正确注入依赖。

## 修复方案
统一 app 模块的 Hilt 版本为 2.51.1:

**app/build.gradle.kts 修改:**
```kotlin
// 修改前
implementation("com.google.dagger:hilt-android:2.48.1")
ksp("com.google.dagger:hilt-android-compiler:2.48.1")

// 修改后
implementation("com.google.dagger:hilt-android:2.51.1")
ksp("com.google.dagger:hilt-android-compiler:2.51.1")
```

同时统一了 test 和 androidTest 的 Compose BOM 版本。

## 验证结果
- 应用成功启动，PID 5050
- 无闪退或异常日志
- HomeScreen 正常加载

## 相关文件
- `app/build.gradle.kts` - Hilt 版本统一
