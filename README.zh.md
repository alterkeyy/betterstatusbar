# betterstatusbar

[English](./README.md) | **简体中文**

一个 LSPosed 模块，原先从 [StatusBarBrightnessGesture](https://github.com/mbatthew/StatusBarBrightnessGesture) fork 而来。允许您通过在状态栏上水平滑动来控制屏幕亮度 — 无论通知栏开启还是关闭，甚至在锁屏界面下均可正常工作。

## v2.0.0 (LibXposed) 更新内容

- **迁移至 LibXposed (API 101)**：提升了性能，并能更好地兼容现代版本的 LSPosed。
- **自定义点击操作**：可以为电池图标、时钟或状态栏空白区域分配单击、双击或长按操作。
- **系统快捷切换**：直接从状态栏切换深色模式、省电模式或锁定屏幕。
- **触感反馈**：调节亮度或触发操作时提供细微的震动反馈。
- **相对亮度调节**：可选择基于当前亮度进行增量调节，而非绝对位置调节。
- **滑动灵敏度**：微调改变亮度所需的水平滑动距离。
- **手势日志**：内置日志查看器，用于调试手势检测和偏好设置更新。

## 运行要求

- **Android 13** 或更高版本 (SDK 33+)
- [Magisk](https://github.com/topjohnwu/Magisk) (已植入 root 权限)
- [LSPosed v1.9.3+](https://github.com/LSPosed/LSPosed) 或其他兼容 LibXposed 的框架

## 安装方法

1. 从 [Releases](https://github.com/parallelcc/MiCTS/releases) 页面安装 APK。
2. 打开 LSPosed → 模块 → 启用 **betterstatusbar**。
3. 确保作用域包含 **系统界面 (System UI)**。
4. 重启设备。
5. 打开应用并配置您的偏好设置。

### 一次性 ADB 设置 (确保重启后切换功能持久生效)

通过 ADB 连接您的设备并运行：
```bash
adb shell pm grant dev.module.betterstatusbar android.permission.WRITE_SECURE_SETTINGS
```
仅需在首次安装后运行一次。该权限在重启和应用更新后依然有效。

## 使用说明

- **向右/左滑动**：在状态栏上水平滑动以增加或降低亮度。
- **点击操作**：
  - **电池图标**：单击查看电池使用情况，或自定义为其他操作。
  - **时钟**：单击查看闹钟，或自定义为其他操作。
  - **空白区域**：双击进入休眠/锁定屏幕（需配置）等。
- **随时随地使用**：在锁屏、通知栏开启或使用应用时均有效。
- **指示器**：一个非侵入性的百分比悬浮窗会跟随您的滑动，颜色采用系统壁纸的强调色。

## 设置选项

打开应用可配置：
- **手势**：启用/禁用亮度滑动和点击操作。
- **亮度模式**：选择绝对调节（基于位置）或相对调节（基于增量）。
- **触感**：调整震动反馈强度（从无到强）。
- **灵敏度**：调整触发亮度变化所需的滑动距离。
- **自定义操作**：为以下区域映射单击、双击和长按：
  - 电池图标
  - 时间/时钟
  - 状态栏背景
- **操作类型**：
  - 启动 Intent (系统闹钟、电量使用等)
  - 切换深色模式
  - 切换省电模式
  - 锁定屏幕
  - 更多功能待添加...

## 兼容性

适用于大多数基于 AOSP 的 Android 13+ ROM，包括：
- Pixel 官方系统 (GrapheneOS, CalyxOS)
- LineageOS 及其衍生版 (crDroid, EvolutionX, DerpFest 等)

**注意**：可能不适用于高度定制的 ROM（如 MIUI/HyperOS, OriginOS 等），因为它们通常替换了标准的 SystemUI 状态栏类。

## 已测试环境

- Samsung Tab S8, OneUI 8 Android 16, 最新版 LSPosed v2.0.3

## CI/CD 设置 (进行中)

本项目使用 GitHub Actions 自动构建、签名并发布特定架构的 APK。

### 必要的 GitHub Secrets

要启用自动签名发布，请在 GitHub 仓库中配置以下 Secret (**Settings > Secrets and variables > Actions**)：

- `KEYSTORE_BASE64`：Base64 编码后的 Android 发布密钥库文件 (`base64 -w 0 your_keystore.jks`)。
- `KEYSTORE_PASSWORD`：密钥库密码。
- `KEY_ALIAS`：发布密钥别名。
- `KEY_PASSWORD`：发布密钥密码。

### 自动化工作流

- **Pull Requests**：构建项目以确保代码完整性。
- **Push to Main**：构建并签名特定架构的 APK (x86_64, arm64-v8a, armeabi-v7a)，并作为工作流附件上传。
- **Tag (v*)**：自动创建 GitHub Release 并附加签名后的 APK。

## 贡献

欢迎提交 Pull Request 或开 Issue 讨论新功能和错误修复！

## 致谢

本项目的创建离不开以下优秀的开源项目：
[mbatthew/StatusBarBrightnessGesture: LSPosed 模块 — 在 Android 12+ 上滑动状态栏控制亮度](https://github.com/mbatthew/StatusBarBrightnessGesture)

## 许可证

本项目采用 MIT 许可证 - 详情请参阅 [LICENSE](https://github.com/mbatthew/betterstatusbar/blob/cc585c53bd0278cc5114ed39ca640b52e12d057c/LICENSE) 文件。
