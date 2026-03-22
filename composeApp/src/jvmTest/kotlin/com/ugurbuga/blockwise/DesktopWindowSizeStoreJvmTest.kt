package com.ugurbuga.blockwise

import kotlin.test.Test
import kotlin.test.assertEquals

class DesktopWindowSizeStoreJvmTest {

    @Test
    fun `desktop window size sanitization falls back to defaults when values are missing`() {
        assertEquals(
            DesktopWindowSize(widthDp = 1180, heightDp = 860),
            sanitizeDesktopWindowSize(widthDp = null, heightDp = null),
        )
    }

    @Test
    fun `desktop window size sanitization clamps overly small values`() {
        assertEquals(
            DesktopWindowSize(widthDp = 900, heightDp = 680),
            sanitizeDesktopWindowSize(widthDp = 320, heightDp = 200),
        )
    }

    @Test
    fun `desktop window size sanitization preserves valid saved size`() {
        assertEquals(
            DesktopWindowSize(widthDp = 1366, heightDp = 900),
            sanitizeDesktopWindowSize(widthDp = 1366, heightDp = 900),
        )
    }
}

