package com.aicore.session

import com.aicore.contracts.EngineType
import com.aicore.contracts.PlatformRuntimeContext
import com.aicore.contracts.PlatformType
import com.aicore.contracts.PolicyViolationCode
import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformPolicyValidatorTest {
    private val validator = PlatformPolicyValidator()

    @Test
    fun `web wasm blocks offline engine`() {
        val violations = validator.validateEngineSelection(
            selectedEngine = EngineType.ONNX,
            runtime = PlatformRuntimeContext(platform = PlatformType.WEB_WASM),
        )

        assertEquals(1, violations.size)
        assertEquals(PolicyViolationCode.OFFLINE_NOT_SUPPORTED_ON_WEB, violations.first().code)
    }

    @Test
    fun `desktop allows offline engine`() {
        val violations = validator.validateEngineSelection(
            selectedEngine = EngineType.ONNX,
            runtime = PlatformRuntimeContext(platform = PlatformType.WINDOWS),
        )

        assertEquals(0, violations.size)
    }
}
