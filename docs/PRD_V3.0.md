# AICore 跨平台智能基座
# 产品需求规格说明书 V3.0（最终执行版）

- 文档编号：AI-PRD-2026-V3.0
- 版本状态：已评审，待研发启动
- 密级：核心商业机密
- 产品负责人：[待签署]
- 日期：2026-04-15

---

## 1. 项目概述与核心战略

基于 **Kotlin Multiplatform (KMP)** 构建支持 **Android、iOS、Web (Wasm)、Windows (x64)、macOS (ARM/x64)** 的全平台 AI 助手。

核心设计哲学：
- 界面极简主义：主界面仅保留单页问答。
- 云端与本地共生：支持云端模型与离线模型。
- 绝对模块解耦：模块独立编译、独立验证、独立替换。

## 2. 技术架构与模块化插拔设计

技术栈：Kotlin 2.0+、Compose Multiplatform、Room 3、Navigation 3。

| 模块 ID | 职责描述 | 插拔边界与独立运行标准 |
| :--- | :--- | :--- |
| `:core:common-interfaces` | 契约层，定义 `InferenceEngine`、`FileProcessor` | 仅接口定义 |
| `:core:network` | 云端 API 网关（SSE、多供应商鉴权、连接池） | Mock 流式返回验证解析 |
| `:core:engine-adapter` | 离线推理适配器（LiteRT-LM/ONNX Runtime） | 真实模型文件单测比对 |
| `:core:session-manager` | 全局状态聚合器（恢复、降级、切换通知） | 启动失败场景降级验证 |
| `:feature:model-hub` | 模型生命周期管理（在线配置、离线下载/扫描/删除） | 脱 UI 验证扫描与断点续传 |
| `:feature:input-core` | 多模态预处理（ASR、文件提取、能力拦截） | 各平台权限与选择器验证 |
| `:feature:settings` | 设置中心 UI（供应商 Key、角色编辑、参数调节） | 独立运行验证表单持久化 |
| `:feature:chat` | 单页主界面（渲染、打字机、附件预览） | 严禁依赖具体引擎实现 |

## 3. 全局初始化与配置串联逻辑

`SessionManager` 作为唯一数据源。

冷启动流程：
1. 读取 `LastSessionConfig`（上次引擎/模型/角色）。
2. 模型可用性校验：离线文件丢失时强制降级在线。
3. 能力匹配校验：角色与模型能力冲突时阻断并提示。
4. 有效配置注入 `ChatViewModel` 并进入就绪态。

运行时重载协议：
- 切换引擎：弹窗确认后清空 `KVCache`，保留 UI 历史。
- 调整 Temperature：无需清空历史，下次请求即时生效。

## 4. 功能需求详细规格

### 4.1 模型管理中台（`:feature:model-hub`）
- 在线供应商：OpenAI、Claude、DeepSeek、通义千问、智谱 AI。
- 在线配置项：API Key、Base URL、Max Tokens。
- 模型列表拉取：有效 Key 后自动请求 `/v1/models`。
- 连通性测试：发送 `Hello` 并计时。
- 离线模型仓库：`gemma-4-e2b`（2GB，`.lite`/`.onnx`）、`gemma-4-e4b`（4GB，`.onnx`）。
- 下载管理：断点续传，下载前检查可用空间（>=1.5x 模型大小）。
- 格式过滤：引擎选择驱动模型后缀过滤。
- 在线/离线模式切换：对话页顶部状态栏左侧开关，立即生效。

### 4.2 多模态输入处理（`:feature:input-core`）
- 文本：多行自动撑高。
- 语音：按住说话转文字；长按发送按钮可发送原声 `.wav`。
- 附件：图片、文件（PDF/Word/Txt）、音频、视频。
- 拖拽：桌面端支持拖拽。
- 能力限制：离线纯文本模型下，仅保留可支持输入并对其他入口置灰提示。

### 4.3 角色设定与管理

```kotlin
data class Role(
    val id: String,
    val name: String,
    val systemPrompt: String,
    val temperature: Float,
    val enginePreference: EngineType?
)
```

- 设置页支持角色 CRUD。
- 对话页顶部头像打开抽屉，显示最近角色、全部角色、临时角色创建。

### 4.4 单页问答交互（`:feature:chat`）
- 顶部状态栏：模型名、角色名、首 Token 延迟。
- 消息列表：Markdown 代码高亮与表格渲染。
- 流式输出：`SharedFlow` 打字机效果，刷新率目标 60fps。
- 长按菜单：
  - 用户消息：复制、编辑重发
  - AI 消息：复制、重新生成

### 4.5 设置与配置中心（`:feature:settings`）
- 云端大脑：供应商管理、默认在线模型。
- 离线大脑：推理引擎、模型管理、线程数、上下文窗口。
- 角色工坊：角色与 Prompt 编辑。
- 对话设置：字号、自动播放语音。
- 存储管理：模型下载路径（仅桌面端）、清理对话（需二次确认）。

## 5. 非功能性需求与全平台适配

屏幕适配策略（WindowSizeClass）：
- `<360dp`：气泡宽 90%，输入框 48dp。
- `360-600dp`：气泡宽 75%。
- `600-840dp`：对话区最大宽 680dp，角色抽屉右侧常驻。
- `>840dp`：三栏布局（导航/对话/角色预览）。

性能指标：
- 首 Token 延迟：在线 <1.5s；离线 <2s（桌面 4B）。
- 内存增量：移动端加载 2B 模型 <1.2GB。

## 6. 跨平台差异化约束

- Web（Wasm）：禁用离线模型下载与加载，仅在线模式。
- Windows/macOS：支持 `Ctrl+K` 聚焦输入、文件拖拽、自定义模型路径。
- iOS/Android：下载大模型时强制 WiFi + 充电状态。

## 7. 模块插拔契约与接口强制定义

```kotlin
interface InferenceEngine {
    suspend fun generateStream(
        prompt: String,
        history: List<ChatMessage>,
        config: GenerationConfig
    ): Flow<GenerationChunk>

    suspend fun loadModel(path: String): Result<Unit>
    suspend fun unloadModel()
}

interface FileProcessor {
    suspend fun extractText(uri: String): Result<String>
    fun isSupportedMimeType(mime: String): Boolean
}
```

研发约束：`feature:chat` 禁止直接依赖具体推理实现（例如 `com.litert.*`）。

## 8. 项目实施路线图与里程碑

- P1（3周）：底座与云端，完成 SSE 流式在线对话。
- P2（4周）：离线引擎，桌面成功加载 `gemma-4-e4b.onnx` 并输出有效语义。
- P3（3周）：交互串联，完成离线缺失自动降级在线。
- P4（2周）：多模态与适配，折叠屏/桌面布局与 PDF 文本提取。
- P5（2周）：打磨发布，产出 Windows/Mac 安装包、iOS TestFlight、Android APK。
