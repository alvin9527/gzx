package com.aicore.session

import com.aicore.contracts.EngineType
import com.aicore.contracts.PlatformRuntimeContext
import com.aicore.contracts.PlatformType
import com.aicore.contracts.PolicyViolation
import com.aicore.contracts.PolicyViolationCode

class PlatformPolicyValidator {
    fun validateEngineSelection(
        selectedEngine: EngineType,
        runtime: PlatformRuntimeContext,
    ): List<PolicyViolation> {
        if (runtime.platform == PlatformType.WEB_WASM && selectedEngine != EngineType.CLOUD) {
            return listOf(
                PolicyViolation(
                    code = PolicyViolationCode.OFFLINE_NOT_SUPPORTED_ON_WEB,
                    message = "Web(Wasm) only supports cloud models.",
                )
            )
        }
        return emptyList()
    }
}
