# AICore 跨平台智能基座

AICore 是一个基于 **Kotlin Multiplatform (KMP)** 的跨平台 AI 助手工程骨架，当前仓库已完成：

1. 产品基线文档（PRD / 路线图 / ADR / 运行清单）。
2. 多模块项目初始化（`core` + `feature`）。
3. 关键链路最小可运行实现（会话恢复降级、聊天流式输出、模型与输入能力约束）。

## 模块结构

- `core/common-interfaces`：统一契约与核心数据模型（`InferenceEngine`、`FileProcessor` 等）。
- `core/network`：云端推理引擎样例实现（流式返回模拟）。
- `core/engine-adapter`：离线推理引擎样例实现（模型加载与格式校验）。
- `core/session-manager`：冷启动恢复与在线/离线降级逻辑。
- `feature/model-hub`：模型注册、格式过滤、本地可用性查询。
- `feature/input-core`：输入能力检测（文本/图像/文件/音视频）。
- `feature/settings`：设置状态与配置更新。
- `feature/chat`：与引擎解耦的聊天 ViewModel。

## 快速开始

```bash
./gradlew test
```

## 文档索引

- `docs/PRD_V3.0.md`：产品需求规格说明书 V3.0（执行基线）。
- `docs/DELIVERY_PLAN.md`：阶段交付路线图与风险台账。
- `docs/ARCHITECTURE_DECISIONS.md`：关键逻辑 ADR 归档。
- `docs/PRODUCT_OPS_CHECKLIST.md`：产品负责人推进与发布保障清单。
- `docs/PLATFORM_RUNBOOK.md`：全平台运行保障清单与落地步骤。
