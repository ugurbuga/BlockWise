package com.ugurbuga.blockwise

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val initialWindowSize = remember { DesktopWindowSizeStore.loadWindowSize() }
    val windowState = rememberWindowState(
        width = initialWindowSize.widthDp.dp,
        height = initialWindowSize.heightDp.dp,
    )

    LaunchedEffect(windowState) {
        snapshotFlow { windowState.size }
            .collect(DesktopWindowSizeStore::saveWindowSize)
    }

    Window(
        onCloseRequest = {
            DesktopWindowSizeStore.saveWindowSize(windowState.size)
            exitApplication()
        },
        title = "BlockWise",
        state = windowState,
    ) {
        App()
    }
}