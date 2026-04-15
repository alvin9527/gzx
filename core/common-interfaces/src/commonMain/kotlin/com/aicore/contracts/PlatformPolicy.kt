package com.aicore.contracts

enum class PlatformType {
    ANDROID,
    IOS,
    WEB_WASM,
    WINDOWS,
    MACOS,
}

data class PlatformRuntimeContext(
    val platform: PlatformType,
    val isWifiConnected: Boolean = true,
    val isCharging: Boolean = true,
    val availableStorageBytes: Long = Long.MAX_VALUE,
)

data class DownloadRequest(
    val modelId: String,
    val modelSizeBytes: Long,
)

enum class PolicyViolationCode {
    OFFLINE_NOT_SUPPORTED_ON_WEB,
    MOBILE_REQUIRES_WIFI,
    MOBILE_REQUIRES_CHARGING,
    INSUFFICIENT_STORAGE,
}

data class PolicyViolation(
    val code: PolicyViolationCode,
    val message: String,
)
