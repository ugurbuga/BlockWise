package com.ugurbuga.blockwise

import com.ugurbuga.blockwise.blocklogic.domain.Shapes
import com.ugurbuga.blockwise.blocklogic.ui.buildGridGeometry
import com.ugurbuga.blockwise.blocklogic.ui.cellExtentPx
import com.ugurbuga.blockwise.blocklogic.ui.GridAxisGeometry
import com.ugurbuga.blockwise.blocklogic.ui.normalizeDragStartOffsetInContent
import com.ugurbuga.blockwise.blocklogic.ui.resolveDragAnchor
import com.ugurbuga.blockwise.blocklogic.ui.resolveDraggedOriginAxis
import com.ugurbuga.blockwise.blocklogic.ui.resolveNearestCellAxis
import com.ugurbuga.blockwise.blocklogic.ui.resolvePointerCellAxis
import com.ugurbuga.blockwise.blocklogic.ui.resolveSnappedOriginAxis
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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

    @Test
    fun `cell extent is derived from board size and gap`() {
        assertEquals(
            48.2f,
            cellExtentPx(
                boardExtentPx = 500f,
                gridCount = 10,
                gapPx = 2f,
            )
        )
    }

    @Test
    fun `grid geometry uses board origin as the first cell start`() {
        val geometry = assertNotNull(
            buildGridGeometry(
                gridTopLeftInRoot = Offset(24f, 96f),
                gridSizePx = IntSize(500, 500),
                gridCount = 10,
                gapPx = 2f,
            )
        )

        assertEquals(24f, geometry.x.firstStart)
        assertEquals(96f, geometry.y.firstStart)
        assertEquals(50.2f, geometry.x.step)
        assertEquals(50.2f, geometry.y.step)
    }

    @Test
    fun `grid geometry snapping reaches the right edge on 8 10 and 12 boards`() {
        listOf(8, 10, 12).forEach { gridSize ->
            val geometry = assertNotNull(
                buildGridGeometry(
                    gridTopLeftInRoot = Offset(40f, 120f),
                    gridSizePx = IntSize(480, 480),
                    gridCount = gridSize,
                    gapPx = 2f,
                )
            )
            val maxOrigin = gridSize - 3

            assertEquals(
                maxOrigin,
                resolveSnappedOriginAxis(
                    targetContentStart = geometry.x.firstStart + geometry.x.step * 50f,
                    axis = geometry.x,
                    maxOrigin = maxOrigin,
                )
            )
        }
    }

    @Test
    fun `grid geometry snapping reaches the bottom edge without producing row 10 on a 10 board`() {
        val geometry = assertNotNull(
            buildGridGeometry(
                gridTopLeftInRoot = Offset(32f, 80f),
                gridSizePx = IntSize(500, 500),
                gridCount = 10,
                gapPx = 2f,
            )
        )

        val originY = assertNotNull(
            resolveSnappedOriginAxis(
                targetContentStart = geometry.y.firstStart + geometry.y.step * 100f,
                axis = geometry.y,
                maxOrigin = 7,
            )
        )

        assertEquals(7, originY)
        assertTrue(listOf(originY, originY + 1, originY + 2).all { it in 0..9 })
    }

    @Test
    fun `drag anchor selects the nearest occupied cell for a horizontal bar`() {
        val anchor = resolveDragAnchor(
            piece = com.ugurbuga.blockwise.blocklogic.domain.Piece(
                shape = Shapes.Line3H,
                color = com.ugurbuga.blockwise.blocklogic.domain.BlockColor.Blue,
            ),
            offsetInContent = Offset(118f, 20f),
            cellWidthPx = 48f,
            cellHeightPx = 48f,
            gapPx = 2f,
        )

        assertEquals(2, anchor.dx)
        assertEquals(0, anchor.dy)
    }

    @Test
    fun `drag anchor selects the nearest occupied cell for a vertical bar`() {
        val anchor = resolveDragAnchor(
            piece = com.ugurbuga.blockwise.blocklogic.domain.Piece(
                shape = Shapes.Line3V,
                color = com.ugurbuga.blockwise.blocklogic.domain.BlockColor.Red,
            ),
            offsetInContent = Offset(20f, 118f),
            cellWidthPx = 48f,
            cellHeightPx = 48f,
            gapPx = 2f,
        )

        assertEquals(0, anchor.dx)
        assertEquals(2, anchor.dy)
    }

    @Test
    fun `nearest cell snapping does not advance before the next cell center threshold`() {
        val axis = GridAxisGeometry(firstStart = 32f, step = 50.2f)

        assertEquals(
            0,
            resolveNearestCellAxis(
                pointerPosition = 32f + 47f,
                axis = axis,
                cellExtent = 48.2f,
                maxCellIndex = 9,
            )
        )
        assertEquals(
            1,
            resolveNearestCellAxis(
                pointerPosition = 32f + 51f,
                axis = axis,
                cellExtent = 48.2f,
                maxCellIndex = 9,
            )
        )
    }

    @Test
    fun `nearest cell snapping reaches the final board cell and clamps beyond it`() {
        val geometry = assertNotNull(
            buildGridGeometry(
                gridTopLeftInRoot = Offset(32f, 80f),
                gridSizePx = IntSize(500, 500),
                gridCount = 10,
                gapPx = 2f,
            )
        )

        assertEquals(
            9,
            resolveNearestCellAxis(
                pointerPosition = geometry.x.firstStart + geometry.x.step * 9 + geometry.cellWidth / 2f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
        assertEquals(
            9,
            resolveNearestCellAxis(
                pointerPosition = geometry.x.firstStart + geometry.x.step * 20,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
    }

    @Test
    fun `pointer cell snapping stays on the same cell while pointer is inside that cell area`() {
        val geometry = assertNotNull(
            buildGridGeometry(
                gridTopLeftInRoot = Offset(32f, 80f),
                gridSizePx = IntSize(500, 500),
                gridCount = 10,
                gapPx = 2f,
            )
        )

        assertEquals(
            0,
            resolvePointerCellAxis(
                pointerPosition = geometry.x.firstStart + geometry.cellWidth - 1f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
        assertEquals(
            1,
            resolvePointerCellAxis(
                pointerPosition = geometry.x.firstStart + geometry.x.step + 1f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
    }

    @Test
    fun `pointer cell snapping resolves gap positions to the nearest neighboring cell`() {
        val geometry = assertNotNull(
            buildGridGeometry(
                gridTopLeftInRoot = Offset(32f, 80f),
                gridSizePx = IntSize(500, 500),
                gridCount = 10,
                gapPx = 2f,
            )
        )
        val gapStart = geometry.x.firstStart + geometry.cellWidth

        assertEquals(
            0,
            resolvePointerCellAxis(
                pointerPosition = gapStart + 0.25f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
        assertEquals(
            1,
            resolvePointerCellAxis(
                pointerPosition = gapStart + 1.75f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
    }

    @Test
    fun `pointer cell snapping reaches and clamps to the final board cell`() {
        val geometry = assertNotNull(
            buildGridGeometry(
                gridTopLeftInRoot = Offset(32f, 80f),
                gridSizePx = IntSize(500, 500),
                gridCount = 10,
                gapPx = 2f,
            )
        )

        assertEquals(
            9,
            resolvePointerCellAxis(
                pointerPosition = geometry.x.firstStart + geometry.x.step * 9 + geometry.cellWidth - 1f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
        assertEquals(
            9,
            resolvePointerCellAxis(
                pointerPosition = geometry.x.firstStart + geometry.x.step * 20f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
    }
}