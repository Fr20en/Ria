# Ria

Ria 是一个 LSPosed / Xposed hook 模块，基于 [SpoofMyDevice](https://github.com/BuSung-dev/SpoofMyDevice) 模板构建，配套管理界面使用 Jetpack Compose + Material 3。

## 功能（继承自模板骨架）

- 基于预设或完全自定义的设备档案（profiles），可按应用独立分配
- Build / 系统属性 / 电话 / 标识符 / 显示 / WebView 等常见 hook 点
- Per-field 开关与一键随机化
- Safe Mode：对指定应用保留真实系统版本信息
- 支持英文、韩文、日文、简体中文界面
- 运行时诊断界面

## 要求

- Android 8.0+
- LSPosed（root 环境），或实现 legacy Xposed API 的非 root 补丁管理器（LSPatch / NPatch）

## 使用

1. 安装 APK 并打开一次。
2. 在 LSPosed 中启用 Ria，勾选目标应用的作用域（不要勾选 Android System Framework）。
3. 在应用内创建档案并分配给目标应用，强制停止并重启目标应用生效。

## 自动构建（GitHub Actions）

工作流：`.github/workflows/build.yml`

- 推送到 `main` 或手动触发：构建 release APK，上传为 artifact（可用于测试）。
- 推送稳定版 tag（如 `v1.0.0`）：构建并自动创建 GitHub Release，附带签名 APK。

```bash
# 发布稳定版
git tag v1.0.0
git push origin v1.0.0
```

**只打包稳定版**：tag 含 `alpha` / `beta` / `rc` / `dev` / `preview` / `snapshot` 时只构建、不发布 Release。

## 签名

仓库内置固定签名 `ria.jks`（alias：`ria`，密码在 `app/build.gradle.kts` 中），保证每次 CI 构建产物签名一致，可直接覆盖安装更新。个人模块仓库专用，请勿用于分发敏感应用。

## License

继承上游模板许可（见 LICENSE）。
