package com.ugurbuga.blockwise.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal expect fun PlatformSystemBarsEffect(
    statusBarColor: Color,
    navigationBarColor: Color,
    darkTheme: Boolean,
)

