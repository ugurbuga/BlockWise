package com.ugurbuga.blockwise

import androidx.compose.runtime.staticCompositionLocalOf

internal enum class NeonPulseSpeed(
    val storageValue: String,
    val durationMillis: Int,
) {
    Slow(storageValue = "slow", durationMillis = 3000),
    Normal(storageValue = "normal", durationMillis = 1650),
    Fast(storageValue = "fast", durationMillis = 650),
    ;

    companion object {
        fun fromStorageValue(value: String?): NeonPulseSpeed? {
            return entries.firstOrNull { it.storageValue == value }
        }
    }
}

internal val SelectableNeonPulseSpeeds = NeonPulseSpeed.entries

internal object NeonPulseSpeedStore {
    private const val NEON_PULSE_SPEED_KEY = "neon_pulse_speed"

    fun loadSelectedNeonPulseSpeed(): NeonPulseSpeed? {
        return NeonPulseSpeed.fromStorageValue(PlatformAppSettings.getString(NEON_PULSE_SPEED_KEY))
    }

    fun saveSelectedNeonPulseSpeed(speed: NeonPulseSpeed) {
        PlatformAppSettings.putString(NEON_PULSE_SPEED_KEY, speed.storageValue)
    }
}

internal fun initializeNeonPulseSpeed(): NeonPulseSpeed {
    return NeonPulseSpeedStore.loadSelectedNeonPulseSpeed()
        ?: NeonPulseSpeed.Normal.also(NeonPulseSpeedStore::saveSelectedNeonPulseSpeed)
}

internal val LocalNeonPulseSpeed = staticCompositionLocalOf { NeonPulseSpeed.Normal }

