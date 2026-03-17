package com.ugurbuga.blockwise

import com.ugurbuga.blockwise.blocklogic.ui.GridAxisGeometry
import com.ugurbuga.blockwise.blocklogic.ui.resolveDraggedOriginAxis
import com.ugurbuga.blockwise.blocklogic.ui.resolveSnappedOriginAxis
import kotlin.test.Test
import kotlin.test.assertEquals

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
        val cellCenters = List(10) { index -> 100f + index * 34f + 16f }
        val fingerToAnchorCellCenter = -14f

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                fingerInRoot = cellCenters[9] - fingerToAnchorCellCenter,
                fingerToAnchorCellCenter = fingerToAnchorCellCenter,
                cellCenters = cellCenters,
                anchorCellOffset = 2,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `drag snapping reaches left edge with same off center grab point`() {
        val cellCenters = List(10) { index -> 100f + index * 34f + 16f }
        val fingerToAnchorCellCenter = -14f

        assertEquals(
            0,
            resolveDraggedOriginAxis(
                fingerInRoot = cellCenters[0] - fingerToAnchorCellCenter,
                fingerToAnchorCellCenter = fingerToAnchorCellCenter,
                cellCenters = cellCenters,
                anchorCellOffset = 0,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `drag snapping returns null when anchor cell center is far outside the board`() {
        val cellCenters = List(10) { index -> 100f + index * 34f + 16f }

        assertEquals(
            null,
            resolveDraggedOriginAxis(
                fingerInRoot = -200f,
                fingerToAnchorCellCenter = 0f,
                cellCenters = cellCenters,
                anchorCellOffset = 0,
                pieceSpanCells = 1,
            )
        )
    }

    @Test
    fun `drag snapping reaches right edge with uneven measured centers`() {
        val cellCenters = listOf(116f, 150f, 184f, 218f, 252f, 286f, 320f, 354.5f, 389.5f, 425f)
        val fingerToAnchorCellCenter = 0f

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                fingerInRoot = cellCenters[9],
                fingerToAnchorCellCenter = fingerToAnchorCellCenter,
                cellCenters = cellCenters,
                anchorCellOffset = 2,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `drag snapping reaches bottom edge with uneven measured centers`() {
        val cellCenters = listOf(216f, 250f, 284f, 318f, 352f, 386f, 420f, 455f, 490f, 526f)

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                fingerInRoot = cellCenters[9],
                fingerToAnchorCellCenter = 0f,
                cellCenters = cellCenters,
                anchorCellOffset = 2,
                pieceSpanCells = 3,
            )
        )
    }
}