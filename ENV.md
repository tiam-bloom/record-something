# 环境配置指南

## 必要环境

### JDK
- **版本**: JDK 17 (OpenJDK 或 Oracle JDK 均可)
- **验证**: `java -version` → 应显示 `openjdk version "17.x.x"`

### Android SDK
- **版本**: Android SDK Platform 35, Build-Tools 35.x
- **最低 SDK**: 26 (Android 8.0)
- **目标 SDK**: 35 (Android 15)

### Android Studio (可选)
- **版本**: Hedgehog (2023.11.1) 或更新版本
- 包含 Android SDK、模拟器等工具

### 其他工具
- **Git**: 用于版本控制
- **Gradle**: 项目已包含 wrapper，无需单独安装

---

## 环境验证命令

```bash
# 验证 JDK
java -version

# 验证 Android SDK
echo $ANDROID_HOME
ls $ANDROID_HOME/platforms/
ls $ANDROID_HOME/build-tools/
```

---

## 项目运行步骤

### 方式一：命令行构建

```bash
# 1. 克隆项目
git clone <项目地址>
cd record-something

# 2. 配置本地 SDK 路径 (如尚未配置)
echo "sdk.dir=你的SDK路径" > local.properties
# 示例: echo "sdk.dir=F:/android-sdk" > local.properties

# 3. 构建 Debug APK
./gradlew assembleDebug

# 4. 安装到设备
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 5. 启动应用
adb shell am start -n com.assetmanager.app/.MainActivity
```

### 方式二：Android Studio

```bash
# 1. 打开 Android Studio
# 2. File → Open → 选择项目根目录
# 3. 等待 Gradle Sync 完成
# 4. 连接设备或启动模拟器
# 5. 点击 Run (Shift + F10)
```

### 方式三：使用模拟器

```bash
# 1. 在 Android Studio 中创建模拟器 (Tools → Device Manager)
# 2. 选择设备型号和 API 34/35 系统镜像
# 3. 启动模拟器
# 4. 运行 ./gradlew installDebug
```

---

## 常见环境问题

### SDK 组件缺失

如遇到 `platforms;android-35` 缺失错误，执行:

```bash
# 接受许可协议
mkdir -p $ANDROID_HOME/licenses
echo -e "\n24333f8a63b6825ea9c5514f83c2829b004d1fee" > $ANDROID_HOME/licenses/android-sdk-license
echo -e "\n84831b9409646a918e30573bab4c9c91346d8abd" >> $ANDROID_HOME/licenses/android-sdk-license
echo -e "\nd975f751698a77b662f1254ddbeed3901e976f5a" >> $ANDROID_HOME/licenses/android-sdk-license

# 安装缺失组件
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager.bat --install \
  "platforms;android-35" "build-tools;35.0.1" --sdk_root=$ANDROID_HOME
```

### Gradle Wrapper 问题

如遇到 `GradleWrapperMain` 找不到:

```bash
curl -L -o gradle/wrapper/gradle-wrapper.jar \
  https://github.com/gradle/gradle/raw/v8.9.0/gradle/wrapper/gradle-wrapper.jar
```

### local.properties 路径问题

Windows 下路径使用反斜杠可能导致问题，确保使用正斜杠:

```
sdk.dir=F:/android-sdk
```

---

## 版本信息速查

| 组件 | 版本 | 备注 |
|------|------|------|
| JDK | 17 | 必须 |
| Gradle | 8.9 | wrapper 提供 |
| Android SDK Platform | 35 | 必须 |
| Android SDK Build-Tools | 35.0.1+ | 必须 |
| Kotlin | 2.0.21 | Gradle 插件控制 |
| Compose BOM | 2024.02.00 | UI 框架 |
| Hilt | 2.51.1 | DI 框架 |
| Min SDK | 26 | Android 8.0 |
| Target SDK | 35 | Android 15 |
