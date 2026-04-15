package com.aicore.modelhub

import com.aicore.contracts.EngineType
import com.aicore.contracts.ModelProfile
import com.aicore.contracts.ModelRepository

class InMemoryModelHub(
    private val models: MutableMap<String, ModelProfile> = linkedMapOf(),
    private val localModelIds: MutableSet<String> = linkedSetOf(),
) : ModelRepository {
    fun register(model: ModelProfile, isLocal: Boolean = false) {
        models[model.id] = model
        if (isLocal) localModelIds += model.id
    }

    fun supportedModelsFor(engineType: EngineType): List<ModelProfile> {
        return models.values.filter { model ->
            when (engineType) {
                EngineType.LITERT -> model.localPath?.endsWith(".lite") == true
                EngineType.ONNX -> model.localPath?.endsWith(".onnx") == true
                EngineType.CLOUD -> model.engineType == EngineType.CLOUD
            }
        }
    }

    override suspend fun getModel(modelId: String): ModelProfile? = models[modelId]

    override suspend fun hasLocalModel(modelId: String): Boolean = modelId in localModelIds

    override suspend fun firstAvailableCloudModel(): ModelProfile? =
        models.values.firstOrNull { it.engineType == EngineType.CLOUD }
}
