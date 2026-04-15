package com.aicore.settings

import com.aicore.contracts.EngineType

data class SettingsState(
    val cloudBaseUrl: String = "",
    val cloudApiKey: String = "",
    val defaultModelId: String = "",
    val offlineEngine: EngineType = EngineType.ONNX,
    val inferenceThreads: Int = 2,
    val contextWindow: Int = 4096,
)

class SettingsStore {
    private var state: SettingsState = SettingsState()

    fun read(): SettingsState = state

    fun update(transform: (SettingsState) -> SettingsState): SettingsState {
        state = transform(state)
        return state
    }
}
