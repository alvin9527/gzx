package com.aicore.modelhub

import com.aicore.contracts.DownloadRequest
import com.aicore.contracts.PlatformRuntimeContext
import com.aicore.contracts.PlatformType
import com.aicore.contracts.PolicyViolationCode
import kotlin.test.Test
import kotlin.test.assertTrue

class DownloadPolicyValidatorTest {
    private val validator = DownloadPolicyValidator()

    @Test
    fun `mobile download requires wifi charging and 1_5x storage`() {
        val violations = validator.validate(
            request = DownloadRequest(modelId = "gemma-4-e4b", modelSizeBytes = 4_000L),
            runtime = PlatformRuntimeContext(
                platform = PlatformType.ANDROID,
                isWifiConnected = false,
                isCharging = false,
                availableStorageBytes = 5_000L,
            ),
        )

        val codes = violations.map { it.code }.toSet()
        assertTrue(PolicyViolationCode.MOBILE_REQUIRES_WIFI in codes)
        assertTrue(PolicyViolationCode.MOBILE_REQUIRES_CHARGING in codes)
        assertTrue(PolicyViolationCode.INSUFFICIENT_STORAGE in codes)
    }
}
