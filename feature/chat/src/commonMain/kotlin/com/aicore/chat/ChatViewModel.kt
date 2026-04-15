package com.aicore.chat

import com.aicore.contracts.ChatMessage
import com.aicore.contracts.GenerationConfig
import com.aicore.contracts.InferenceEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val inferenceEngine: InferenceEngine,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    fun send(prompt: String, temperature: Float = 0.7f) {
        val user = ChatMessage(role = "user", content = prompt)
        _messages.value = _messages.value + user + ChatMessage(role = "assistant", content = "")

        scope.launch {
            inferenceEngine.generateStream(
                prompt = prompt,
                history = _messages.value,
                config = GenerationConfig(temperature = temperature, maxTokens = 1024),
            ).collect { chunk ->
                if (!chunk.isFinal) {
                    appendAssistant(chunk.text)
                }
            }
        }
    }

    private fun appendAssistant(piece: String) {
        val list = _messages.value.toMutableList()
        val idx = list.indexOfLast { it.role == "assistant" }
        if (idx >= 0) {
            val current = list[idx]
            list[idx] = current.copy(content = current.content + piece)
            _messages.value = list
        }
    }
}
