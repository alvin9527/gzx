package com.aicore.session

import com.aicore.contracts.EngineType
import com.aicore.contracts.LastSessionConfig
import com.aicore.contracts.ModelProfile
import com.aicore.contracts.PlatformRuntimeContext
import com.aicore.contracts.ModelRepository
import com.aicore.contracts.Role
import com.aicore.contracts.RoleRepository

data class SessionState(
    val engineType: EngineType,
    val model: ModelProfile,
    val role: Role,
    val downgradedFromOffline: Boolean,
)

class SessionManager(
    private val modelRepository: ModelRepository,
    private val roleRepository: RoleRepository,
    private val platformPolicyValidator: PlatformPolicyValidator = PlatformPolicyValidator(),
) {
    suspend fun restore(
        config: LastSessionConfig,
        runtime: PlatformRuntimeContext? = null,
    ): Result<SessionState> {
        val role = roleRepository.getRole(config.roleId)
            ?: return Result.failure(IllegalStateException("Role not found: ${config.roleId}"))

        val model = modelRepository.getModel(config.modelId)
            ?: return Result.failure(IllegalStateException("Model not found: ${config.modelId}"))

        if (runtime != null) {
            val violations = platformPolicyValidator.validateEngineSelection(config.engineType, runtime)
            if (violations.isNotEmpty()) {
                return Result.failure(IllegalStateException(violations.joinToString { it.message }))
            }
        }

        val resolved = when (config.engineType) {
            EngineType.CLOUD -> validateCompatibility(role, model, downgraded = false)
            EngineType.LITERT, EngineType.ONNX -> resolveOffline(role, model)
        }
        return resolved
    }

    private suspend fun resolveOffline(role: Role, model: ModelProfile): Result<SessionState> {
        val exists = modelRepository.hasLocalModel(model.id)
        if (exists) {
            return validateCompatibility(role, model, downgraded = false)
        }

        val fallback = modelRepository.firstAvailableCloudModel()
            ?: return Result.failure(IllegalStateException("Offline model missing and no cloud fallback configured"))
        return validateCompatibility(role, fallback, downgraded = true)
    }

    private fun validateCompatibility(
        role: Role,
        model: ModelProfile,
        downgraded: Boolean,
    ): Result<SessionState> {
        val missing = role.requiredCapabilities - model.capabilities
        if (missing.isNotEmpty()) {
            return Result.failure(IllegalStateException("Capability mismatch: missing=$missing"))
        }
        return Result.success(
            SessionState(
                engineType = model.engineType,
                model = model,
                role = role,
                downgradedFromOffline = downgraded,
            )
        )
    }
}
