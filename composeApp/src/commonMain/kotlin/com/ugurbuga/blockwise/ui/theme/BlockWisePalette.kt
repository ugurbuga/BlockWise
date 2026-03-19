package com.ugurbuga.blockwise.ui.theme

import androidx.compose.ui.graphics.Color
import com.ugurbuga.blockwise.blocklogic.domain.BlockColor

object BlockWisePalette {
    val background = Color(0xFFF4F7FB)
    val surface = Color(0xFFFFFFFF)
    val surfaceVariant = Color(0xFFE7EDF8)
    val onBackground = Color(0xFF182033)
    val onSurface = Color(0xFF1B2335)
    val onSurfaceVariant = Color(0xFF5C687F)

    val primary = Color(0xFF596BFF)
    val primaryContainer = Color(0xFFDCE3FF)
    val secondary = Color(0xFF1C9FA4)
    val secondaryContainer = Color(0xFFD4F3F2)
    val tertiary = Color(0xFFF1A84B)
    val tertiaryContainer = Color(0xFFFFE5BF)

    val outline = Color(0xFFB2BDD0)
    val outlineVariant = Color(0xFFD7DFEC)

    val error = Color(0xFFBA1A1A)
    val errorContainer = Color(0xFFFFDAD6)

    val pieceBorder = Color(0xFF8C97AD)
    val pieceSelectedBorder = primary

    fun pieceColor(blockColor: BlockColor): Color {
        return when (blockColor) {
            BlockColor.Red -> Color(0xFFF46D7D)
            BlockColor.Green -> Color(0xFF45C18A)
            BlockColor.Blue -> Color(0xFF5D8CFF)
            BlockColor.Yellow -> Color(0xFFF5C451)
        }
    }
}

fun BlockColor.toPaletteColor(): Color = BlockWisePalette.pieceColor(this)
