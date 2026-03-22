package com.ugurbuga.blockwise

import androidx.compose.ui.unit.DpSize
import java.util.prefs.Preferences
import kotlin.math.roundToInt

private const val DEFAULT_DESKTOP_WINDOW_WIDTH_DP = 1180
private const val DEFAULT_DESKTOP_WINDOW_HEIGHT_DP = 860
private const val MIN_DESKTOP_WINDOW_WIDTH_DP = 900
private const val MIN_DESKTOP_WINDOW_HEIGHT_DP = 680

internal data class DesktopWindowSize(
    val widthDp: Int,
    val heightDp: Int,
)

internal fun sanitizeDesktopWindowSize(widthDp: Int?, heightDp: Int?): DesktopWindowSize {
    return DesktopWindowSize(
        widthDp = widthDp?.coerceAtLeast(MIN_DESKTOP_WINDOW_WIDTH_DP)
            ?: DEFAULT_DESKTOP_WINDOW_WIDTH_DP,
        heightDp = heightDp?.coerceAtLeast(MIN_DESKTOP_WINDOW_HEIGHT_DP)
            ?: DEFAULT_DESKTOP_WINDOW_HEIGHT_DP,
    )
}

internal object DesktopWindowSizeStore {
    private const val WINDOW_WIDTH_DP_KEY = "desktop_window_width_dp"
    private const val WINDOW_HEIGHT_DP_KEY = "desktop_window_height_dp"

    private val prefs: Preferences = Preferences.userRoot().node("com.ugurbuga.blockwise.window")

    fun loadWindowSize(): DesktopWindowSize {
        val widthDp = if (prefs.get(WINDOW_WIDTH_DP_KEY, null) != null) {
            prefs.getInt(WINDOW_WIDTH_DP_KEY, DEFAULT_DESKTOP_WINDOW_WIDTH_DP)
        } else {
            null
        }
        val heightDp = if (prefs.get(WINDOW_HEIGHT_DP_KEY, null) != null) {
            prefs.getInt(WINDOW_HEIGHT_DP_KEY, DEFAULT_DESKTOP_WINDOW_HEIGHT_DP)
        } else {
            null
        }
        return sanitizeDesktopWindowSize(widthDp = widthDp, heightDp = heightDp)
    }

    fun saveWindowSize(widthDp: Int, heightDp: Int) {
        val sanitized = sanitizeDesktopWindowSize(widthDp = widthDp, heightDp = heightDp)
        prefs.putInt(WINDOW_WIDTH_DP_KEY, sanitized.widthDp)
        prefs.putInt(WINDOW_HEIGHT_DP_KEY, sanitized.heightDp)
        prefs.flush()
    }

    fun saveWindowSize(size: DpSize) {
        val widthDp = size.width.value
        val heightDp = size.height.value
        if (!widthDp.isFinite() || !heightDp.isFinite()) return
        if (widthDp <= 0f || heightDp <= 0f) return
        saveWindowSize(widthDp = widthDp.roundToInt(), heightDp = heightDp.roundToInt())
    }
}

