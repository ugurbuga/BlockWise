package com.ugurbuga.blockwise.ui.theme

import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import com.ugurbuga.blockwise.AndroidAppContextHolder

@Composable
internal actual fun PlatformSystemBarsEffect(
    statusBarColor: Color,
    navigationBarColor: Color,
    darkTheme: Boolean,
) {
    val view = LocalView.current
    if (view.isInEditMode) return

    SideEffect {
        val activity = AndroidAppContextHolder.currentActivity ?: return@SideEffect
        val statusBarArgb = statusBarColor.toArgb()
        val navigationBarArgb = navigationBarColor.toArgb()

        activity.enableEdgeToEdge(
            statusBarStyle = systemBarStyle(
                darkTheme = darkTheme,
                color = statusBarArgb,
            ),
            navigationBarStyle = systemBarStyle(
                darkTheme = darkTheme,
                color = navigationBarArgb,
            ),
        )
    }
}

private fun systemBarStyle(darkTheme: Boolean, color: Int): SystemBarStyle {
    return if (darkTheme) {
        SystemBarStyle.dark(color)
    } else {
        SystemBarStyle.light(
            scrim = color,
            darkScrim = color,
        )
    }
}

