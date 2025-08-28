
# Sms2Bark

监听安卓短信并通过 Bark 推送到 iOS。包含 GitHub Actions 云端构建流程，构建产物可直接在 Actions 的 Artifacts 下载。

## 本地构建
- 用 Android Studio 打开项目，Build → Build APK(s)

## 云端构建（GitHub Actions）
1. 推送到 GitHub 仓库。
2. 在 **Actions** 里运行 `Build Android APK` 工作流。
3. 构建完成后，从 Artifacts 下载 APK。
