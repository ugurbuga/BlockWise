package com.ugurbuga.blockwise

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.WindowPosition
import java.util.prefs.Preferences
import kotlin.math.roundToInt

private const val DEFAULT_DESKTOP_WINDOW_WIDTH_DP = 720
private const val DEFAULT_DESKTOP_WINDOW_HEIGHT_DP = 1280

internal data class DesktopWindowBounds(
    val widthDp: Int,
    val heightDp: Int,
    val positionXDp: Int? = null,
    val positionYDp: Int? = null,
)

internal fun sanitizeDesktopWindowBounds(
    widthDp: Int?,
    heightDp: Int?,
    positionXDp: Int? = null,
    positionYDp: Int? = null,
): DesktopWindowBounds {
    return DesktopWindowBounds(
        widthDp = widthDp?.takeIf { it > 0 } ?: DEFAULT_DESKTOP_WINDOW_WIDTH_DP,
        heightDp = heightDp?.takeIf { it > 0 } ?: DEFAULT_DESKTOP_WINDOW_HEIGHT_DP,
        positionXDp = if (positionXDp != null && positionYDp != null) positionXDp else null,
        positionYDp = if (positionXDp != null && positionYDp != null) positionYDp else null,
    )
}

internal object DesktopWindowSizeStore {
    private const val WINDOW_WIDTH_DP_KEY = "desktop_window_width_dp"
    private const val WINDOW_HEIGHT_DP_KEY = "desktop_window_height_dp"
    private const val WINDOW_POSITION_X_DP_KEY = "desktop_window_position_x_dp"
    private const val WINDOW_POSITION_Y_DP_KEY = "desktop_window_position_y_dp"

    private val prefs: Preferences = Preferences.userRoot().node("com.ugurbuga.blockwise.window")

    fun loadWindowBounds(): DesktopWindowBounds {
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
        val positionXDp = if (prefs.get(WINDOW_POSITION_X_DP_KEY, null) != null) {
            prefs.getInt(WINDOW_POSITION_X_DP_KEY, 0)
        } else {
            null
        }
        val positionYDp = if (prefs.get(WINDOW_POSITION_Y_DP_KEY, null) != null) {
            prefs.getInt(WINDOW_POSITION_Y_DP_KEY, 0)
        } else {
            null
        }
        return sanitizeDesktopWindowBounds(
            widthDp = widthDp,
            heightDp = heightDp,
            positionXDp = positionXDp,
            positionYDp = positionYDp,
        )
    }

    fun saveWindowBounds(
        widthDp: Int,
        heightDp: Int,
        positionXDp: Int? = null,
        positionYDp: Int? = null,
    ) {
        val sanitized = sanitizeDesktopWindowBounds(
            widthDp = widthDp,
            heightDp = heightDp,
            positionXDp = positionXDp,
            positionYDp = positionYDp,
        )
        prefs.putInt(WINDOW_WIDTH_DP_KEY, sanitized.widthDp)
        prefs.putInt(WINDOW_HEIGHT_DP_KEY, sanitized.heightDp)
        if (sanitized.positionXDp != null && sanitized.positionYDp != null) {
            prefs.putInt(WINDOW_POSITION_X_DP_KEY, sanitized.positionXDp)
            prefs.putInt(WINDOW_POSITION_Y_DP_KEY, sanitized.positionYDp)
        } else {
            prefs.remove(WINDOW_POSITION_X_DP_KEY)
            prefs.remove(WINDOW_POSITION_Y_DP_KEY)
        }
        prefs.flush()
    }

    fun saveWindowBounds(size: DpSize, position: WindowPosition? = null) {
        val widthDp = size.width.value
        val heightDp = size.height.value
        if (!widthDp.isFinite() || !heightDp.isFinite()) return
        if (widthDp <= 0f || heightDp <= 0f) return
        val positionXDp = position
            ?.takeIf { it.isSpecified }
            ?.x
            ?.value
            ?.takeIf { it.isFinite() }
            ?.roundToInt()
        val positionYDp = position
            ?.takeIf { it.isSpecified }
            ?.y
            ?.value
            ?.takeIf { it.isFinite() }
            ?.roundToInt()
        saveWindowBounds(
            widthDp = widthDp.roundToInt(),
            heightDp = heightDp.roundToInt(),
            positionXDp = positionXDp,
            positionYDp = positionYDp,
        )
    }
}

