package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.back
import blockwise.composeapp.generated.resources.shapes_preview_section_2x2
import blockwise.composeapp.generated.resources.shapes_preview_section_3x3
import blockwise.composeapp.generated.resources.shapes_preview_section_4x4
import blockwise.composeapp.generated.resources.shapes_preview_section_5x5
import blockwise.composeapp.generated.resources.shapes_preview_title
import com.ugurbuga.blockwise.blocklogic.domain.BlockColor
import com.ugurbuga.blockwise.blocklogic.domain.Piece
import com.ugurbuga.blockwise.blocklogic.domain.Shape
import com.ugurbuga.blockwise.blocklogic.domain.Shapes
import com.ugurbuga.blockwise.AppColorPalette
import com.ugurbuga.blockwise.BlockVisualStyle
import com.ugurbuga.blockwise.LocalAppColorPalette
import com.ugurbuga.blockwise.LocalBlockColorPalette
import com.ugurbuga.blockwise.LocalBlockVisualStyle
import com.ugurbuga.blockwise.LocalBoardBlockStyleMode
import com.ugurbuga.blockwise.LocalPaletteIsDarkTheme
import com.ugurbuga.blockwise.resolveBoardBlockShapeStyle
import com.ugurbuga.blockwise.resolveBoardEmptyBlockRenderStyle
import com.ugurbuga.blockwise.resolveBoardFilledBlockRenderStyle
import com.ugurbuga.blockwise.LocalBlockGapSpacing
import com.ugurbuga.blockwise.ui.theme.BlockWisePalette
import com.ugurbuga.blockwise.ui.theme.BlockWiseTheme
import com.ugurbuga.blockwise.localizedStringResource as stringResource

private const val SHAPES_PER_ROW = 4
private val PREVIEW_BOARD_SIZE_DP = 92.dp
private val PREVIEW_BOARD_CORNER_RADIUS_DP = 14.dp
private val PREVIEW_CARD_PADDING_DP = 12.dp
private val PREVIEW_BOARD_INSET_DP = 10.dp
private val PREVIEW_GRID_CELL_BORDER_WIDTH_DP = 1.dp
private val PREVIEW_PIECE_BORDER_WIDTH_DP = 1.2.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShapesPreviewScreen(
    onBack: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopAppBar(
                title = { 
                    Text(stringResource(Res.string.shapes_preview_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                item {
                    ShapesLevelSection(
                        title = stringResource(Res.string.shapes_preview_section_2x2),
                        shapes = Shapes.forMaxDimension(2),
                        gridCount = 2,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                item {
                    ShapesLevelSection(
                        title = stringResource(Res.string.shapes_preview_section_3x3),
                        shapes = Shapes.forMaxDimension(3),
                        gridCount = 3,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                item {
                    ShapesLevelSection(
                        title = stringResource(Res.string.shapes_preview_section_4x4),
                        shapes = Shapes.forMaxDimension(4),
                        gridCount = 4,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                item {
                    ShapesLevelSection(
                        title = stringResource(Res.string.shapes_preview_section_5x5),
                        shapes = Shapes.forMaxDimension(5),
                        gridCount = 5,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShapesPreviewScreenPreview() {
    BlockWiseTheme {
        ShapesPreviewScreen(onBack = {})
    }
}

@Composable
private fun ShapesLevelSection(
    title: String,
    shapes: List<Shape>,
    gridCount: Int,
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val chunkedShapes = shapes
            .sortedWith(
                compareBy<Shape> { it.cells.size }
                    .thenBy { shape -> shape.cells.maxOf { it.dy } }
                    .thenBy { shape -> shape.cells.maxOf { it.dx } }
            )
            .chunked(SHAPES_PER_ROW)
        
        chunkedShapes.forEach { rowShapes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top
            ) {
                rowShapes.forEach { shape ->
                    ShapePreviewItem(
                        shape = shape,
                        gridCount = gridCount,
                        modifier = Modifier.weight(1f)
                    )
                    if (rowShapes.indexOf(shape) < rowShapes.size - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ShapePreviewItem(
    shape: Shape,
    gridCount: Int,
    modifier: Modifier = Modifier
) {
    val gap = LocalBlockGapSpacing.current.gapDp

    val innerBoardSize = PREVIEW_BOARD_SIZE_DP - PREVIEW_BOARD_INSET_DP * 2
    val cellSize = (innerBoardSize - gap * (gridCount - 1)) / gridCount

    val pieceColor = rememberPreviewColor(shape)
    val piece = Piece(shape = shape, color = pieceColor)

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PREVIEW_CARD_PADDING_DP),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(PREVIEW_BOARD_SIZE_DP)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(PREVIEW_BOARD_INSET_DP),
            ) {
                MiniBoard(
                    gridCount = gridCount,
                    cellSize = cellSize,
                    gap = gap,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                )

                val maxDx = piece.shape.cells.maxOf { it.dx }
                val maxDy = piece.shape.cells.maxOf { it.dy }
                val widthCells = maxDx + 1
                val heightCells = maxDy + 1
                val originX = ((gridCount - widthCells) / 2).coerceAtLeast(0)
                val originY = ((gridCount - heightCells) / 2).coerceAtLeast(0)
                val step = cellSize + gap

                Box(
                    modifier = Modifier
                        .absoluteOffset(
                            x = step * originX,
                            y = step * originY,
                        ),
                ) {
                    PiecePreview(
                        piece = piece,
                        cellSize = cellSize,
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        borderWidth = PREVIEW_PIECE_BORDER_WIDTH_DP,
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberPreviewColor(shape: Shape): BlockColor {
    return when ((shape.cells.size + shape.cells.maxOf { it.dx } + shape.cells.maxOf { it.dy }) % 4) {
        0 -> BlockColor.Red
        1 -> BlockColor.Green
        2 -> BlockColor.Blue
        else -> BlockColor.Yellow
    }
}

@Composable
private fun MiniBoard(
    gridCount: Int,
    cellSize: Dp,
    gap: Dp,
    modifier: Modifier = Modifier,
) {
    val selectedBlockStyle = LocalBlockVisualStyle.current
    val boardBlockStyleMode = LocalBoardBlockStyleMode.current
    val boardShapeStyle = resolveBoardBlockShapeStyle(boardBlockStyleMode, selectedBlockStyle)
    val emptyCellRenderStyle = resolveBoardEmptyBlockRenderStyle(boardBlockStyleMode, selectedBlockStyle)
    val filledCellRenderStyle = resolveBoardFilledBlockRenderStyle(boardBlockStyleMode, selectedBlockStyle)
    val appColorPalette = LocalAppColorPalette.current
    val useDarkTheme = LocalPaletteIsDarkTheme.current
    val paletteColors = BlockWisePalette.blockColors(
        palette = LocalBlockColorPalette.current,
        darkTheme = useDarkTheme,
    )
    val themePalette = BlockWisePalette.themePalette(appColorPalette, useDarkTheme)

    val paletteAccent = themedBoardPaletteAccent(
        appColorPalette = appColorPalette,
        style = BlockVisualStyle.Flat,
        colors = listOf(
            paletteColors.red,
            paletteColors.green,
            paletteColors.blue,
            paletteColors.yellow,
        ),
    )
    val boardBaseSurfaceColor = themedBoardBaseSurfaceColor(
        appColorPalette = appColorPalette,
        style = BlockVisualStyle.Flat,
        themePalette = themePalette,
    )
    val resolvedBoardBaseSurfaceColor = if (useDarkTheme) {
        boardBaseSurfaceColor
    } else {
        blendColors(boardBaseSurfaceColor, androidx.compose.ui.graphics.Color.Black, 0.035f)
    }
    val boardBaseBorderColor = themedBoardBaseBorderColor(
        appColorPalette = appColorPalette,
        style = BlockVisualStyle.Flat,
        themePalette = themePalette,
    )
    val emptyCellColor = themedEmptyBoardCellColor(
        baseColor = resolvedBoardBaseSurfaceColor,
        paletteAccent = paletteAccent,
        style = BlockVisualStyle.Flat,
    )
    val emptyCellBorderColor = themedEmptyBoardCellBorderColor(
        baseColor = boardBaseBorderColor,
        paletteAccent = paletteAccent,
        style = BlockVisualStyle.Flat,
    )

    val boardCornerRadius = (cellSize * 0.22f).coerceAtLeast(6.dp)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(gap),
    ) {
        for (y in 0 until gridCount) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) {
                for (x in 0 until gridCount) {
                    val cellFillColor = emptyCellColor
                    val cellBorderColor = emptyCellBorderColor
                    BlockTile3D(
                        fillColor = cellFillColor,
                        borderColor = cellBorderColor,
                        borderWidth = 1.dp,
                        cornerRadius = boardCornerRadius,
                        recessed = true,
                        elevation = 0.dp,
                        renderStyleOverride = emptyCellRenderStyle,
                        shapeStyleOverride = boardShapeStyle,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                    )
                }
            }
        }
    }
}
