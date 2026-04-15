package com.aicore.modelhub

import com.aicore.contracts.DownloadRequest
import com.aicore.contracts.PlatformRuntimeContext
import com.aicore.contracts.PlatformType
import com.aicore.contracts.PolicyViolation
import com.aicore.contracts.PolicyViolationCode

class DownloadPolicyValidator {
    fun validate(request: DownloadRequest, runtime: PlatformRuntimeContext): List<PolicyViolation> {
        val violations = mutableListOf<PolicyViolation>()

        if (runtime.platform == PlatformType.ANDROID || runtime.platform == PlatformType.IOS) {
            if (!runtime.isWifiConnected) {
                violations += PolicyViolation(
                    PolicyViolationCode.MOBILE_REQUIRES_WIFI,
                    "Mobile requires WiFi to download large offline models.",
                )
            }
            if (!runtime.isCharging) {
                violations += PolicyViolation(
                    PolicyViolationCode.MOBILE_REQUIRES_CHARGING,
                    "Mobile requires charging state for large model download.",
                )
            }
        }

        val minRequiredStorage = (request.modelSizeBytes * 1.5).toLong()
        if (runtime.availableStorageBytes < minRequiredStorage) {
            violations += PolicyViolation(
                PolicyViolationCode.INSUFFICIENT_STORAGE,
                "Insufficient storage: required >= $minRequiredStorage bytes.",
            )
        }

        return violations
    }
}
