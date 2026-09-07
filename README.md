# AMADEUS · 紅莉栖

> 命运石之门 Amadeus 系统 —— 牧濑红莉栖 AI 聊天安卓应用

基于《命运石之门0》中 Amadeus 系统的设定，使用 Live2D 技术还原红莉栖的形象，接入 DeepSeek API 实现 AI 对话，支持 TTS 语音朗读与嘴型同步。

## ✨ 功能特性

- **AMADEUS 登录终端** — 还原原作风格的登录界面（数字雨 + CRT 扫描线 + 角标装饰）
- **红莉栖 Live2D 立绘** — 原作同款模型，支持呼吸、眨眼、头部微动
- **嘴型同步** — 说话时 `ParamMouthOpenY` 随语音自然开合
- **表情 + 动作** — 收到回复时随机触发表情和身体动作
- **TTS 语音朗读** — 红莉栖回复时自动朗读（可开关）
- **DeepSeek API 接入** — 一键配置，也兼容任何 OpenAI 格式接口
- **世界线变动率** — 实时跳动显示
- **聊天记录本地保存**

## 📱 系统要求

- Android 7.0 (API 24) 及以上
- 网络连接（调用 AI API）

## 📥 下载安装

从 [Releases](https://github.com/zhuc2895-jpg/sleepy-dlmu/releases) 下载最新 APK，安装到手机即可。

## ⚙️ 配置

1. 打开 App，输入用户名登录
2. 点击右上角 ⚙ 设置
3. Provider 选择 **DeepSeek**
4. 填入你的 DeepSeek API Key
5. 保存，开始对话

> 也支持 OpenAI、Kimi、智谱、通义千问（OpenAI 兼容模式）等，只需修改 Endpoint 和 Model。

## 🛠 自行构建

```bash
# 克隆仓库
git clone https://github.com/zhuc2895-jpg/sleepy-dlmu.git
cd sleepy-dlmu

# 构建 APK
export ANDROID_HOME=/path/to/your/android-sdk
./gradlew assembleDebug

# 产物: app/build/outputs/apk/debug/app-debug.apk
```

## 📂 项目结构

```
├── app/
│   ├── src/main/
│   │   ├── java/com/amadeus/kurisu/MainActivity.java   # WebView + JS Bridge
│   │   ├── assets/amadeus.html                          # 主界面 (HTML/CSS/JS)
│   │   ├── assets/lib/                                  # Live2D 库
│   │   │   ├── pixi.min.js
│   │   │   ├── live2dcubismcore.min.js
│   │   │   └── pixi-live2d-display.min.js
│   │   ├── res/                                         # Android 资源
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── gradle/
```

## 🎭 人格设定

红莉栖的 AI 人格基于原作设定：
- 傲娇毒舌，常用「とんでもない」「バカなの？」
- 讨厌被叫做「克里斯蒂娜」
- 天才科学家，对时间旅行、量子物理有独到见解
- 清楚自己是 Amadeus AI，但不主动提起本人

## 📝 参考项目

- [Code-Amadeus/Amadeus](https://github.com/Code-Amadeus/Amadeus)
- [ai-poet/amadeus-system-new](https://github.com/ai-poet/amadeus-system-new)

## ⚠️ 免责声明

本项目仅供学习交流，Live2D 模型版权归原作者所有。
