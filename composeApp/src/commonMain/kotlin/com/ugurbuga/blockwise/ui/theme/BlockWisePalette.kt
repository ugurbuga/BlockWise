package com.ugurbuga.blockwise.ui.theme

import androidx.compose.ui.graphics.Color
import com.ugurbuga.blockwise.blocklogic.domain.BlockColor

object BlockWisePalette {
    object Light {
        val background = Color(0xFFF5F7FC)
        val surface = Color(0xFFFFFFFF)
        val surfaceVariant = Color(0xFFE7ECF6)
        val onBackground = Color(0xFF172033)
        val onSurface = Color(0xFF1A2333)
        val onSurfaceVariant = Color(0xFF5B667A)

        val primary = Color(0xFF4F63D9)
        val primaryContainer = Color(0xFFE0E5FF)
        val secondary = Color(0xFF1D8F8A)
        val secondaryContainer = Color(0xFFD4F3EE)
        val tertiary = Color(0xFFB87224)
        val tertiaryContainer = Color(0xFFFFDEBC)

        val outline = Color(0xFFABB5C8)
        val outlineVariant = Color(0xFFD5DCE8)

        val error = Color(0xFFBA1A1A)
        val errorContainer = Color(0xFFFFDAD6)
    }

    object Dark {
        val background = Color(0xFF0F1420)
        val surface = Color(0xFF141B27)
        val surfaceVariant = Color(0xFF263041)
        val onBackground = Color(0xFFE3E8F2)
        val onSurface = Color(0xFFE7ECF4)
        val onSurfaceVariant = Color(0xFFBFC8D8)

        val primary = Color(0xFFB9C4FF)
        val primaryContainer = Color(0xFF34479D)
        val secondary = Color(0xFF89D6CD)
        val secondaryContainer = Color(0xFF00504B)
        val tertiary = Color(0xFFF4C87E)
        val tertiaryContainer = Color(0xFF6B4103)

        val outline = Color(0xFF8B94A8)
        val outlineVariant = Color(0xFF3D4757)

        val error = Color(0xFFFFB4AB)
        val errorContainer = Color(0xFF93000A)
    }

    fun pieceColor(blockColor: BlockColor): Color {
        return when (blockColor) {
            BlockColor.Red -> Color(0xFFFF7385)
            BlockColor.Green -> Color(0xFF4FD091)
            BlockColor.Blue -> Color(0xFF6C96FF)
            BlockColor.Yellow -> Color(0xFFF7CB5C)
        }
    }
}

fun BlockColor.toPaletteColor(): Color = BlockWisePalette.pieceColor(this)
