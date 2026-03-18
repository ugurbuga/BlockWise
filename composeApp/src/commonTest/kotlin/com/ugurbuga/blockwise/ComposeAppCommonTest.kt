package com.ugurbuga.blockwise

import com.ugurbuga.blockwise.blocklogic.domain.Shapes
import com.ugurbuga.blockwise.blocklogic.ui.GridAxisGeometry
import com.ugurbuga.blockwise.blocklogic.ui.normalizeDragStartOffsetInContent
import com.ugurbuga.blockwise.blocklogic.ui.resolveDraggedOriginAxis
import com.ugurbuga.blockwise.blocklogic.ui.resolveSnappedOriginAxis
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ComposeAppCommonTest {

    @Test
    fun example() {
        assertEquals(3, 1 + 2)
    }

    @Test
    fun `single piece reaches both horizontal edges`() {
        val axis = GridAxisGeometry(firstStart = 100f, step = 34f)

        assertEquals(
            0,
            resolveSnappedOriginAxis(
                targetContentStart = 100f,
                axis = axis,
                maxOrigin = 9,
            )
        )
        assertEquals(
            9,
            resolveSnappedOriginAxis(
                targetContentStart = 100f + 9 * 34f,
                axis = axis,
                maxOrigin = 9,
            )
        )
    }

    @Test
    fun `three cell bar reaches both horizontal edges`() {
        val axis = GridAxisGeometry(firstStart = 100f, step = 34f)

        assertEquals(
            0,
            resolveSnappedOriginAxis(
                targetContentStart = 100f,
                axis = axis,
                maxOrigin = 7,
            )
        )
        assertEquals(
            7,
            resolveSnappedOriginAxis(
                targetContentStart = 100f + 7 * 34f,
                axis = axis,
                maxOrigin = 7,
            )
        )
    }

    @Test
    fun `drag snapping reaches right edge even when grab point is near piece right side`() {
        val axisStarts = listOf(100f, 134f, 168f, 202f, 236f, 270f, 304f, 338f, 372f, 406f)

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                targetContentStart = axisStarts[7],
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                targetContentStart = axisStarts[7] + 20f,
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `drag snapping reaches left edge with same off center grab point`() {
        val axisStarts = listOf(100f, 134f, 168f, 202f, 236f, 270f, 304f, 338f, 372f, 406f)

        assertEquals(
            0,
            resolveDraggedOriginAxis(
                targetContentStart = axisStarts[0],
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `drag snapping clamps to the first origin when overlay start is far before the board`() {
        val axisStarts = listOf(100f, 134f, 168f, 202f, 236f, 270f, 304f, 338f, 372f, 406f)

        assertEquals(
            0,
            resolveDraggedOriginAxis(
                targetContentStart = -200f,
                axisStarts = axisStarts,
                pieceSpanCells = 1,
            )
        )
    }

    @Test
    fun `drag snapping clamps to the last origin when overlay start is beyond the board`() {
        val axisStarts = listOf(100f, 134f, 168f, 202f, 236f, 270f, 304f, 338f, 372f, 406f)

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                targetContentStart = 900f,
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `drag snapping reaches right edge with uneven measured starts`() {
        val axisStarts = listOf(100f, 134f, 168f, 202f, 236f, 270f, 304f, 338.5f, 373.5f, 409f)

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                targetContentStart = axisStarts[7],
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `drag snapping reaches bottom edge with uneven measured starts`() {
        val axisStarts = listOf(200f, 234f, 268f, 302f, 336f, 370f, 404f, 439f, 474f, 510f)

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                targetContentStart = axisStarts[7],
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `drag start offset is clamped to the piece content when drag begins in padding`() {
        assertEquals(
            96f,
            normalizeDragStartOffsetInContent(
                startOffsetInPiece = 104f,
                pieceContainerInsetPx = 8f,
                contentSizePx = 96f,
            )
        )
        assertEquals(
            0f,
            normalizeDragStartOffsetInContent(
                startOffsetInPiece = 3f,
                pieceContainerInsetPx = 8f,
                contentSizePx = 96f,
            )
        )
    }

    @Test
    fun `normalized drag start still resolves the final origin for a horizontal bar`() {
        val axisStarts = listOf(100f, 134f, 168f, 202f, 236f, 270f, 304f, 338f, 372f, 406f)
        val normalizedOffset = normalizeDragStartOffsetInContent(
            startOffsetInPiece = 112f,
            pieceContainerInsetPx = 8f,
            contentSizePx = 96f,
        )

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                targetContentStart = (axisStarts[7] + normalizedOffset) - normalizedOffset,
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `normalized drag start still resolves the final origin for a vertical bar`() {
        val axisStarts = listOf(200f, 234f, 268f, 302f, 336f, 370f, 404f, 438f, 472f, 506f)
        val normalizedOffset = normalizeDragStartOffsetInContent(
            startOffsetInPiece = 112f,
            pieceContainerInsetPx = 8f,
            contentSizePx = 96f,
        )

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                targetContentStart = (axisStarts[7] + normalizedOffset) - normalizedOffset,
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `all random shapes stay within 3x3 bounds`() {
        assertTrue(
            Shapes.All.all { shape ->
                val maxDx = shape.cells.maxOf { it.dx }
                val maxDy = shape.cells.maxOf { it.dy }
                maxDx < 3 && maxDy < 3
            }
        )
    }

    @Test
    fun `all random shapes are unique`() {
        val signatures = Shapes.All.map { shape ->
            shape.cells.joinToString(";") { "${it.dx},${it.dy}" }
        }
        assertEquals(signatures.size, signatures.toSet().size)
    }
}