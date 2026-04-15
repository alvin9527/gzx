package com.aicore.network

import com.aicore.contracts.ChatMessage
import com.aicore.contracts.GenerationChunk
import com.aicore.contracts.GenerationConfig
import com.aicore.contracts.InferenceEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CloudInferenceEngine : InferenceEngine {
    override suspend fun generateStream(
        prompt: String,
        history: List<ChatMessage>,
        config: GenerationConfig,
    ): Flow<GenerationChunk> = flow {
        val response = "Cloud response: $prompt"
        response.split(" ").forEachIndexed { index, token ->
            delay(15)
            emit(GenerationChunk(text = if (index == 0) token else " $token", isFinal = false))
        }
        emit(GenerationChunk(text = "", isFinal = true))
    }

    override suspend fun loadModel(path: String): Result<Unit> = Result.success(Unit)
    override suspend fun unloadModel() = Unit
}
