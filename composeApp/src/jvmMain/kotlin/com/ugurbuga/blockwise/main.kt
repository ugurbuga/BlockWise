package com.ugurbuga.blockwise

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val initialWindowBounds = remember { DesktopWindowSizeStore.loadWindowBounds() }
    val windowState = rememberWindowState(
        position = initialWindowBounds.positionXDp?.let { x ->
            initialWindowBounds.positionYDp?.let { y ->
                WindowPosition(x.dp, y.dp)
            }
        } ?: WindowPosition.PlatformDefault,
        width = initialWindowBounds.widthDp.dp,
        height = initialWindowBounds.heightDp.dp,
    )

    LaunchedEffect(windowState) {
        snapshotFlow {
            Triple(windowState.size, windowState.position, windowState.placement)
        }.collect { (size, position, placement) ->
            if (placement == WindowPlacement.Floating) {
                DesktopWindowSizeStore.saveWindowBounds(size = size, position = position)
            }
        }
    }

    Window(
        onCloseRequest = {
            if (windowState.placement == WindowPlacement.Floating) {
                DesktopWindowSizeStore.saveWindowBounds(
                    size = windowState.size,
                    position = windowState.position,
                )
            }
            exitApplication()
        },
        title = "BlockWise",
        state = windowState,
    ) {
        App()
    }
}