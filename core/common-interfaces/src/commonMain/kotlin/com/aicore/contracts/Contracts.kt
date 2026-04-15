package com.aicore.contracts

import kotlinx.coroutines.flow.Flow

enum class EngineType { CLOUD, LITERT, ONNX }

enum class Capability { TEXT, IMAGE, FILE, AUDIO, VIDEO, CONTEXT_128K }

data class ChatMessage(
    val role: String,
    val content: String,
)

data class GenerationConfig(
    val temperature: Float,
    val maxTokens: Int,
)

data class GenerationChunk(
    val text: String,
    val isFinal: Boolean,
)

data class Role(
    val id: String,
    val name: String,
    val systemPrompt: String,
    val temperature: Float,
    val enginePreference: EngineType?,
    val requiredCapabilities: Set<Capability> = emptySet(),
)

data class ModelProfile(
    val id: String,
    val engineType: EngineType,
    val capabilities: Set<Capability>,
    val localPath: String? = null,
)

data class LastSessionConfig(
    val engineType: EngineType,
    val modelId: String,
    val roleId: String,
)

interface InferenceEngine {
    suspend fun generateStream(
        prompt: String,
        history: List<ChatMessage>,
        config: GenerationConfig,
    ): Flow<GenerationChunk>

    suspend fun loadModel(path: String): Result<Unit>
    suspend fun unloadModel()
}

interface FileProcessor {
    suspend fun extractText(uri: String): Result<String>
    fun isSupportedMimeType(mime: String): Boolean
}

interface ModelRepository {
    suspend fun getModel(modelId: String): ModelProfile?
    suspend fun hasLocalModel(modelId: String): Boolean
    suspend fun firstAvailableCloudModel(): ModelProfile?
}

interface RoleRepository {
    suspend fun getRole(roleId: String): Role?
}
