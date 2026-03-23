package com.ugurbuga.blockwise

import kotlin.test.Test
import kotlin.test.assertEquals

class DesktopWindowSizeStoreJvmTest {

    @Test
    fun `desktop window bounds sanitization falls back to defaults when values are missing`() {
        assertEquals(
            DesktopWindowBounds(widthDp = 1180, heightDp = 860),
            sanitizeDesktopWindowBounds(widthDp = null, heightDp = null),
        )
    }

    @Test
    fun `desktop window bounds sanitization preserves exact positive size without min clamp`() {
        assertEquals(
            DesktopWindowBounds(widthDp = 320, heightDp = 200),
            sanitizeDesktopWindowBounds(widthDp = 320, heightDp = 200),
        )
    }

    @Test
    fun `desktop window bounds sanitization preserves valid saved size and position`() {
        assertEquals(
            DesktopWindowBounds(widthDp = 1366, heightDp = 900, positionXDp = 240, positionYDp = 96),
            sanitizeDesktopWindowBounds(widthDp = 1366, heightDp = 900, positionXDp = 240, positionYDp = 96),
        )
    }

    @Test
    fun `desktop window bounds sanitization drops partial position values`() {
        assertEquals(
            DesktopWindowBounds(widthDp = 1180, heightDp = 860, positionXDp = null, positionYDp = null),
            sanitizeDesktopWindowBounds(widthDp = null, heightDp = null, positionXDp = 120, positionYDp = null),
        )
    }
}

