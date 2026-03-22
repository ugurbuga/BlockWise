package com.ugurbuga.blockwise.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ugurbuga.blockwise.AppColorPalette
import com.ugurbuga.blockwise.BlockColorPalette
import com.ugurbuga.blockwise.LocalBlockColorPalette
import com.ugurbuga.blockwise.LocalPaletteIsDarkTheme
import com.ugurbuga.blockwise.blocklogic.domain.BlockColor

internal data class BlockPieceColors(
    val red: Color,
    val green: Color,
    val blue: Color,
    val yellow: Color,
)

internal data class BlockWiseThemePalette(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val primary: Color,
    val primaryContainer: Color,
    val secondary: Color,
    val secondaryContainer: Color,
    val tertiary: Color,
    val tertiaryContainer: Color,
    val outline: Color,
    val outlineVariant: Color,
    val error: Color,
    val errorContainer: Color,
)

internal object BlockWisePalette {
    private data class PaletteFamily(
        val light: BlockWiseThemePalette,
        val dark: BlockWiseThemePalette,
    )

    private val classic = PaletteFamily(
        light = BlockWiseThemePalette(
            background = Color(0xFFF5F7FC),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE7ECF6),
            onBackground = Color(0xFF172033),
            onSurface = Color(0xFF1A2333),
            onSurfaceVariant = Color(0xFF5B667A),
            primary = Color(0xFF4F63D9),
            primaryContainer = Color(0xFFE0E5FF),
            secondary = Color(0xFF1D8F8A),
            secondaryContainer = Color(0xFFD4F3EE),
            tertiary = Color(0xFFB87224),
            tertiaryContainer = Color(0xFFFFDEBC),
            outline = Color(0xFFABB5C8),
            outlineVariant = Color(0xFFD5DCE8),
            error = Color(0xFFBA1A1A),
            errorContainer = Color(0xFFFFDAD6),
        ),
        dark = BlockWiseThemePalette(
            background = Color(0xFF0F1420),
            surface = Color(0xFF141B27),
            surfaceVariant = Color(0xFF263041),
            onBackground = Color(0xFFE3E8F2),
            onSurface = Color(0xFFE7ECF4),
            onSurfaceVariant = Color(0xFFBFC8D8),
            primary = Color(0xFFB9C4FF),
            primaryContainer = Color(0xFF34479D),
            secondary = Color(0xFF89D6CD),
            secondaryContainer = Color(0xFF00504B),
            tertiary = Color(0xFFF4C87E),
            tertiaryContainer = Color(0xFF6B4103),
            outline = Color(0xFF8B94A8),
            outlineVariant = Color(0xFF3D4757),
            error = Color(0xFFFFB4AB),
            errorContainer = Color(0xFF93000A),
        ),
    )

    private val aurora = PaletteFamily(
        light = BlockWiseThemePalette(
            background = Color(0xFFF2FAFF),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFDCF1F8),
            onBackground = Color(0xFF122530),
            onSurface = Color(0xFF18303A),
            onSurfaceVariant = Color(0xFF58727D),
            primary = Color(0xFF0E8FA4),
            primaryContainer = Color(0xFFD2F3F8),
            secondary = Color(0xFF6C63FF),
            secondaryContainer = Color(0xFFE7E4FF),
            tertiary = Color(0xFF2D9B7D),
            tertiaryContainer = Color(0xFFD3F4EA),
            outline = Color(0xFFA6C2CC),
            outlineVariant = Color(0xFFD0E2E8),
            error = Color(0xFFBA1A1A),
            errorContainer = Color(0xFFFFDAD6),
        ),
        dark = BlockWiseThemePalette(
            background = Color(0xFF071A23),
            surface = Color(0xFF0E202A),
            surfaceVariant = Color(0xFF1D3540),
            onBackground = Color(0xFFDDF1F6),
            onSurface = Color(0xFFE1F0F5),
            onSurfaceVariant = Color(0xFFB8CBD2),
            primary = Color(0xFF7DE6F6),
            primaryContainer = Color(0xFF006A7B),
            secondary = Color(0xFFC9C2FF),
            secondaryContainer = Color(0xFF4D45C7),
            tertiary = Color(0xFF8DE5C8),
            tertiaryContainer = Color(0xFF00513C),
            outline = Color(0xFF82989F),
            outlineVariant = Color(0xFF334A55),
            error = Color(0xFFFFB4AB),
            errorContainer = Color(0xFF93000A),
        ),
    )

    private val sunset = PaletteFamily(
        light = BlockWiseThemePalette(
            background = Color(0xFFFFF5F1),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF9E6DE),
            onBackground = Color(0xFF341A18),
            onSurface = Color(0xFF3A2320),
            onSurfaceVariant = Color(0xFF7A605A),
            primary = Color(0xFFD56545),
            primaryContainer = Color(0xFFFFDBD0),
            secondary = Color(0xFF8C5CF6),
            secondaryContainer = Color(0xFFE9DEFF),
            tertiary = Color(0xFFE09B2D),
            tertiaryContainer = Color(0xFFFFE3BA),
            outline = Color(0xFFCDB2AB),
            outlineVariant = Color(0xFFE8D5CF),
            error = Color(0xFFBA1A1A),
            errorContainer = Color(0xFFFFDAD6),
        ),
        dark = BlockWiseThemePalette(
            background = Color(0xFF231412),
            surface = Color(0xFF2D1B19),
            surfaceVariant = Color(0xFF49302D),
            onBackground = Color(0xFFF7DEDA),
            onSurface = Color(0xFFFCE8E3),
            onSurfaceVariant = Color(0xFFE3C4BC),
            primary = Color(0xFFFFB59F),
            primaryContainer = Color(0xFF9E4A2F),
            secondary = Color(0xFFD2BCFF),
            secondaryContainer = Color(0xFF5B40B1),
            tertiary = Color(0xFFFFD08A),
            tertiaryContainer = Color(0xFF7A5300),
            outline = Color(0xFFB69991),
            outlineVariant = Color(0xFF624643),
            error = Color(0xFFFFB4AB),
            errorContainer = Color(0xFF93000A),
        ),
    )


    private val blockPalettes: Map<BlockColorPalette, Pair<BlockPieceColors, BlockPieceColors>> = mapOf(
        BlockColorPalette.Classic to (
            BlockPieceColors(
                red = Color(0xFFFF6B7E),
                green = Color(0xFF40C98B),
                blue = Color(0xFF5B8FFF),
                yellow = Color(0xFFF5C84B),
            ) to BlockPieceColors(
                red = Color(0xFFFF8A99),
                green = Color(0xFF63D99F),
                blue = Color(0xFF82A7FF),
                yellow = Color(0xFFFFD86C),
            )
        ),
        BlockColorPalette.Candy to (
            BlockPieceColors(
                red = Color(0xFFFF7BC8),
                green = Color(0xFF6CE6C5),
                blue = Color(0xFF73D8FF),
                yellow = Color(0xFFFFB37A),
            ) to BlockPieceColors(
                red = Color(0xFFFF9DD8),
                green = Color(0xFF8EF0D4),
                blue = Color(0xFF95E2FF),
                yellow = Color(0xFFFFC596),
            )
        ),
        BlockColorPalette.Neon to (
            BlockPieceColors(
                red = Color(0xFFFF2FA3),
                green = Color(0xFF39FF72),
                blue = Color(0xFF24C4FF),
                yellow = Color(0xFFFFFF33),
            ) to BlockPieceColors(
                red = Color(0xFFFF5BBA),
                green = Color(0xFF68FF90),
                blue = Color(0xFF58D6FF),
                yellow = Color(0xFFFFFF66),
            )
        ),
        BlockColorPalette.Earth to (
            BlockPieceColors(
                red = Color(0xFFD96C45),
                green = Color(0xFF6F9B4D),
                blue = Color(0xFF3F8C8C),
                yellow = Color(0xFFD9A441),
            ) to BlockPieceColors(
                red = Color(0xFFE48A67),
                green = Color(0xFF8AB967),
                blue = Color(0xFF5EABAB),
                yellow = Color(0xFFE2BB67),
            )
        ),
    )

    fun themePalette(palette: AppColorPalette, darkTheme: Boolean): BlockWiseThemePalette {
        val family = when (palette) {
            AppColorPalette.Classic -> classic
            AppColorPalette.Aurora -> aurora
            AppColorPalette.Sunset -> sunset
        }
        return if (darkTheme) family.dark else family.light
    }

    fun pieceColor(
        blockColor: BlockColor,
        palette: BlockColorPalette,
        darkTheme: Boolean,
    ): Color {
        val colors = blockColors(palette, darkTheme)
        return when (blockColor) {
            BlockColor.Red -> colors.red
            BlockColor.Green -> colors.green
            BlockColor.Blue -> colors.blue
            BlockColor.Yellow -> colors.yellow
        }
    }

    fun blockColors(
        palette: BlockColorPalette,
        darkTheme: Boolean,
    ): BlockPieceColors {
        return blockPalettes.getValue(palette).let { if (darkTheme) it.second else it.first }
    }
}

@Composable
fun BlockColor.toPaletteColor(): Color {
    return BlockWisePalette.pieceColor(
        blockColor = this,
        palette = LocalBlockColorPalette.current,
        darkTheme = LocalPaletteIsDarkTheme.current,
    )
}
