package com.aicore.input

import com.aicore.contracts.Capability
import com.aicore.contracts.ModelProfile

enum class InputType { TEXT, IMAGE, FILE, AUDIO, VIDEO }

class InputCapabilityGuard {
    fun isAllowed(inputType: InputType, model: ModelProfile): Boolean {
        val capability = when (inputType) {
            InputType.TEXT -> Capability.TEXT
            InputType.IMAGE -> Capability.IMAGE
            InputType.FILE -> Capability.FILE
            InputType.AUDIO -> Capability.AUDIO
            InputType.VIDEO -> Capability.VIDEO
        }
        return capability in model.capabilities
    }
}
