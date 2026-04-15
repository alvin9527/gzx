package com.aicore.engine

import com.aicore.contracts.ChatMessage
import com.aicore.contracts.GenerationChunk
import com.aicore.contracts.GenerationConfig
import com.aicore.contracts.InferenceEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OfflineInferenceEngine : InferenceEngine {
    private var loaded = false

    override suspend fun generateStream(
        prompt: String,
        history: List<ChatMessage>,
        config: GenerationConfig,
    ): Flow<GenerationChunk> = flow {
        check(loaded) { "Offline model is not loaded" }
        emit(GenerationChunk("Local response: ", isFinal = false))
        emit(GenerationChunk(prompt.reversed(), isFinal = false))
        emit(GenerationChunk("", isFinal = true))
    }

    override suspend fun loadModel(path: String): Result<Unit> {
        loaded = path.endsWith(".onnx") || path.endsWith(".lite")
        return if (loaded) Result.success(Unit) else Result.failure(IllegalArgumentException("Unsupported model format"))
    }

    override suspend fun unloadModel() {
        loaded = false
    }
}
