package com.aicore.session

import com.aicore.contracts.Capability
import com.aicore.contracts.EngineType
import com.aicore.contracts.LastSessionConfig
import com.aicore.contracts.ModelProfile
import com.aicore.contracts.ModelRepository
import com.aicore.contracts.Role
import com.aicore.contracts.RoleRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class SessionManagerTest {
    @Test
    fun `downgrades to cloud when offline model missing`() = runTest {
        val offline = ModelProfile("gemma-4-e4b", EngineType.ONNX, setOf(Capability.TEXT))
        val cloud = ModelProfile("gpt-5", EngineType.CLOUD, setOf(Capability.TEXT, Capability.CONTEXT_128K))
        val role = Role("r1", "analyst", "", 0.6f, null, setOf(Capability.TEXT))

        val manager = SessionManager(
            modelRepository = FakeModelRepo(
                models = mapOf(offline.id to offline, cloud.id to cloud),
                localModels = emptySet(),
                fallback = cloud,
            ),
            roleRepository = FakeRoleRepo(mapOf(role.id to role)),
        )

        val result = manager.restore(LastSessionConfig(EngineType.ONNX, offline.id, role.id)).getOrThrow()
        assertEquals(EngineType.CLOUD, result.engineType)
        assertTrue(result.downgradedFromOffline)
    }

    private class FakeModelRepo(
        private val models: Map<String, ModelProfile>,
        private val localModels: Set<String>,
        private val fallback: ModelProfile?,
    ) : ModelRepository {
        override suspend fun getModel(modelId: String): ModelProfile? = models[modelId]
        override suspend fun hasLocalModel(modelId: String): Boolean = modelId in localModels
        override suspend fun firstAvailableCloudModel(): ModelProfile? = fallback
    }

    private class FakeRoleRepo(
        private val roles: Map<String, Role>,
    ) : RoleRepository {
        override suspend fun getRole(roleId: String): Role? = roles[roleId]
    }
}
