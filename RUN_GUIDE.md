# 资产管家 - 项目运行指南

## 一、环境准备

### 1.1 安装 JDK 17

下载并安装 [JDK 17](https://adoptium.net/temurin/releases/?version=17)

验证安装：
```powershell
java -version
# 应显示 openjdk version "17.x.x"
```

### 1.2 安装 Android Studio

1. 下载 [Android Studio Hedgehog](https://developer.android.com/studio) 或更新版本
2. 安装过程中勾选：
   - Android SDK
   - Android Virtual Device

### 1.3 配置 Android SDK

1. 打开 Android Studio
2. `File` → `Settings` → `Languages & Frameworks` → `Android SDK`
3. 安装以下组件：
   - Android SDK Platform 34
   - Android SDK Build-Tools 34.0.0
   - Android SDK Command-line Tools

4. 设置环境变量：
   ```powershell
   # PowerShell 永久设置
   [System.Environment]::SetEnvironmentVariable("ANDROID_HOME", "C:\Users\你的用户名\AppData\Local\Android\Sdk", "User")
   [System.Environment]::SetEnvironmentVariable("Path", "$env:Path;C:\Users\你的用户名\AppData\Local\Android\Sdk\platform-tools", "User")
   ```

---

## 二、项目配置

### 2.1 克隆项目

```bash
git clone <项目地址>
cd record-something
```

### 2.2 在 Android Studio 中打开

1. 打开 Android Studio
2. `File` → `Open`
3. 选择项目根目录 `record-something`
4. 点击 `OK`

### 2.3 同步 Gradle

首次打开时，Android Studio 会自动提示同步，点击 `Sync Now`

或手动同步：
1. 打开右侧 `Gradle` 面板
2. 点击刷新按钮 🔄

---

## 三、运行项目

### 3.1 方式一：Android Studio 运行（推荐）

1. 连接手机或启动模拟器
2. 在工具栏选择运行目标设备
3. 点击 ▶️ 运行按钮 或按 `Shift + F10`

### 3.2 方式二：命令行运行

```bash
# 清理并构建
./gradlew clean assembleDebug

# 安装到已连接设备
./gradlew installDebug

# 运行（如果设备支持）
./gradlew run
```

### 3.3 方式三：使用模拟器

1. `Tools` → `Device Manager`
2. 点击 `Create Device`
3. 选择设备型号（如 Pixel 6）
4. 选择系统镜像（API 34）
5. 创建并启动模拟器
6. 运行项目

---

## 四、常见问题

### Q1: Gradle 同步失败

**解决方法：**
1. 检查网络连接
2. 清除缓存：`File` → `Invalidate Caches` → `Invalidate and Restart`
3. 手动下载 Gradle Wrapper：
   ```bash
   # 在项目根目录执行
   .\gradlew.bat wrapper --gradle-version 8.2
   ```

### Q2: SDK 找不到

**解决方法：**
```powershell
# 设置环境变量（修改为你的实际路径）
$env:ANDROID_HOME = "C:\Users\用户名\AppData\Local\Android\Sdk"
$env:ANDROID_SDK_ROOT = "C:\Users\用户名\AppData\Local\Android\Sdk"
```

### Q3: 模拟器启动失败

**解决方法：**
1. 确保 CPU 虚拟化已启用（BIOS 中开启）
2. 检查 HAXM 是否安装
3. 尝试使用 Google API 镜像

### Q4: Kotlin 插件版本不匹配

**解决方法：**
更新 Android Studio 到最新版本，它会自动匹配正确的 Kotlin 插件版本。

---

## 五、构建 APK

### 5.1 Debug 版本

```bash
./gradlew assembleDebug
```

APK 位置：`app/build/outputs/apk/debug/app-debug.apk`

### 5.2 Release 版本

```bash
./gradlew assembleRelease
```

APK 位置：`app/build/outputs/apk/release/app-release.apk`

> Release 版本需要签名配置，首次需要创建签名文件。

---

## 六、项目结构概览

```
record-something/
├── app/                    # 应用模块
│   └── src/main/
│       ├── java/com/assetmanager/app/
│       │   ├── ui/        # UI层（Compose页面）
│       │   ├── viewmodel/ # ViewModel层
│       │   └── navigation/# 导航配置
│       └── res/           # 资源文件
│
├── core/                   # 核心库模块
│   └── src/main/
│       └── java/com/assetmanager/core/
│           ├── data/      # 数据层（Room数据库）
│           ├── domain/    # 领域层（业务逻辑）
│           └── di/        # 依赖注入
│
├── build.gradle.kts       # 根构建配置
└── settings.gradle.kts    # 项目设置
```

---

## 七、快捷键

| 功能 | Windows |
|------|---------|
| 运行 | `Shift + F10` |
| 调试运行 | `Shift + F9` |
| 同步 Gradle | `Ctrl + Shift + S` |
| 构建 Project | `Ctrl + F9` |
| 格式化代码 | `Ctrl + Alt + L` |
| 快速修复 | `Alt + Enter` |
| 搜索 | `Ctrl + N` |

---

## 八、技术支持

如有其他问题，请检查：
- [Android 开发者文档](https://developer.android.com/docs)
- [Jetpack Compose 文档](https://developer.android.com/compose)
- [项目 Issues](https://github.com/anomalyco/opencode/issues)
