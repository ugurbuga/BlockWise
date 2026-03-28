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
import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.app_title
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.unit.DpSize
import java.awt.Dimension
import java.awt.Toolkit

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
        title = stringResource(Res.string.app_title),
        state = windowState,
        onPreviewKeyEvent = { false },
        onKeyEvent = { false },
    ) {
        // Set up aspect ratio constraints using AWT window
        LaunchedEffect(Unit) {
            val window = (this@Window.window)
            window.addComponentListener(object : java.awt.event.ComponentAdapter() {
                override fun componentResized(e: java.awt.event.ComponentEvent?) {
                    val currentSize = window.size
                    val aspectRatio = 9f / 16f
                    
                    // Calculate new size maintaining aspect ratio
                    val newWidth: Int
                    val newHeight: Int
                    
                    if (currentSize.width / aspectRatio <= currentSize.height) {
                        // Width is limiting factor
                        newWidth = currentSize.width
                        newHeight = (currentSize.width / aspectRatio).roundToInt()
                    } else {
                        // Height is limiting factor
                        newHeight = currentSize.height
                        newWidth = (currentSize.height * aspectRatio).roundToInt()
                    }
                    
                    // Apply new size if different
                    if (currentSize.width != newWidth || currentSize.height != newHeight) {
                        window.size = Dimension(newWidth, newHeight)
                    }
                }
            })
        }
        
        App()
    }
}