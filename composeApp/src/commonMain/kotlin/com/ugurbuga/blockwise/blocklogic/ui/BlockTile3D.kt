package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ugurbuga.blockwise.BlockVisualStyle
import com.ugurbuga.blockwise.LocalBlockVisualStyle
import com.ugurbuga.blockwise.LocalNeonPulseSpeed

internal fun Color.lighten(amount: Float): Color = lerpTo(Color.White, amount)

internal fun Color.darken(amount: Float): Color = lerpTo(Color.Black, amount)

private fun Color.lerpTo(target: Color, amount: Float): Color {
    val fraction = amount.coerceIn(0f, 1f)
    return Color(
        red = red + (target.red - red) * fraction,
        green = green + (target.green - green) * fraction,
        blue = blue + (target.blue - blue) * fraction,
        alpha = alpha + (target.alpha - alpha) * fraction,
    )
}

internal enum class BlockTileInteraction {
    Normal,
    Pressed,
}

private data class BlockTileStyleSpec(
    val topLighten: Float,
    val bottomDarken: Float,
    val fillAlpha: Float,
    val useSolidBase: Boolean,
    val topHighlightAlpha: Float,
    val leftHighlightAlpha: Float,
    val bottomShadeAlpha: Float,
    val rightShadeAlpha: Float,
    val diagonalGlowAlpha: Float,
    val glossAlpha: Float,
    val borderDarken: Float,
    val borderLighten: Float,
    val useLightBorder: Boolean,
    val shadowAlpha: Float,
    val elevationMultiplier: Float,
    val neonGlowAlpha: Float,
)

private const val NEON_PULSE_MIN_SCALE = 0.98f
private const val NEON_PULSE_MAX_SCALE = 1.16f
private const val NEON_PULSE_MIN_INTENSITY = 0.78f
private const val NEON_PULSE_MAX_INTENSITY = 1.2f

private fun blockTileStyleSpec(
    style: BlockVisualStyle,
    recessed: Boolean,
    interaction: BlockTileInteraction,
): BlockTileStyleSpec {
    val pressed = interaction == BlockTileInteraction.Pressed
    if (recessed) {
        return when (style) {
            BlockVisualStyle.Flat -> BlockTileStyleSpec(
                topLighten = 0f,
                bottomDarken = 0f,
                fillAlpha = 1f,
                useSolidBase = true,
                topHighlightAlpha = 0f,
                leftHighlightAlpha = 0f,
                bottomShadeAlpha = 0f,
                rightShadeAlpha = 0f,
                diagonalGlowAlpha = 0f,
                glossAlpha = 0f,
                borderDarken = if (pressed) 0.12f else 0.18f,
                borderLighten = 0f,
                useLightBorder = false,
                shadowAlpha = 0f,
                elevationMultiplier = 0f,
                neonGlowAlpha = 0f,
            )

            BlockVisualStyle.Raised3D -> BlockTileStyleSpec(
                topLighten = 0.02f,
                bottomDarken = 0.18f,
                fillAlpha = 1f,
                useSolidBase = false,
                topHighlightAlpha = if (pressed) 0.04f else 0.07f,
                leftHighlightAlpha = if (pressed) 0.03f else 0.05f,
                bottomShadeAlpha = if (pressed) 0.16f else 0.12f,
                rightShadeAlpha = if (pressed) 0.14f else 0.1f,
                diagonalGlowAlpha = 0.08f,
                glossAlpha = 0f,
                borderDarken = 0.26f,
                borderLighten = 0f,
                useLightBorder = false,
                shadowAlpha = 0f,
                elevationMultiplier = 0f,
                neonGlowAlpha = 0f,
            )

            BlockVisualStyle.LiquidGlass -> BlockTileStyleSpec(
                topLighten = 0.12f,
                bottomDarken = 0.05f,
                fillAlpha = if (pressed) 0.92f else 0.88f,
                useSolidBase = false,
                topHighlightAlpha = if (pressed) 0.1f else 0.16f,
                leftHighlightAlpha = if (pressed) 0.06f else 0.1f,
                bottomShadeAlpha = if (pressed) 0.08f else 0.06f,
                rightShadeAlpha = if (pressed) 0.06f else 0.05f,
                diagonalGlowAlpha = if (pressed) 0.08f else 0.12f,
                glossAlpha = if (pressed) 0.08f else 0.14f,
                borderDarken = 0f,
                borderLighten = if (pressed) 0.08f else 0.14f,
                useLightBorder = true,
                shadowAlpha = 0f,
                elevationMultiplier = 0f,
                neonGlowAlpha = 0f,
            )

            BlockVisualStyle.Neon -> BlockTileStyleSpec(
                topLighten = 0.02f,
                bottomDarken = 0.12f,
                fillAlpha = if (pressed) 0.92f else 0.88f,
                useSolidBase = false,
                topHighlightAlpha = if (pressed) 0.04f else 0.07f,
                leftHighlightAlpha = if (pressed) 0.03f else 0.05f,
                bottomShadeAlpha = if (pressed) 0.12f else 0.1f,
                rightShadeAlpha = if (pressed) 0.08f else 0.06f,
                diagonalGlowAlpha = if (pressed) 0.06f else 0.1f,
                glossAlpha = 0f,
                borderDarken = 0f,
                borderLighten = if (pressed) 0.08f else 0.14f,
                useLightBorder = true,
                shadowAlpha = 0f,
                elevationMultiplier = 0f,
                neonGlowAlpha = if (pressed) 0.08f else 0.14f,
            )
        }
    }

    return when (style) {
        BlockVisualStyle.Flat -> BlockTileStyleSpec(
            topLighten = 0f,
            bottomDarken = 0f,
            fillAlpha = 1f,
            useSolidBase = true,
            topHighlightAlpha = 0f,
            leftHighlightAlpha = 0f,
            bottomShadeAlpha = 0f,
            rightShadeAlpha = 0f,
            diagonalGlowAlpha = 0f,
            glossAlpha = 0f,
            borderDarken = if (pressed) 0.2f else 0.3f,
            borderLighten = 0f,
            useLightBorder = false,
            shadowAlpha = 0f,
            elevationMultiplier = 0f,
            neonGlowAlpha = 0f,
        )

        BlockVisualStyle.Raised3D -> BlockTileStyleSpec(
            topLighten = if (pressed) 0.1f else 0.2f,
            bottomDarken = if (pressed) 0.12f else 0.2f,
            fillAlpha = 1f,
            useSolidBase = false,
            topHighlightAlpha = if (pressed) 0.12f else 0.22f,
            leftHighlightAlpha = if (pressed) 0.07f else 0.14f,
            bottomShadeAlpha = if (pressed) 0.18f else 0.26f,
            rightShadeAlpha = if (pressed) 0.14f else 0.2f,
            diagonalGlowAlpha = if (pressed) 0.08f else 0.12f,
            glossAlpha = if (pressed) 0.06f else 0.1f,
            borderDarken = if (pressed) 0.28f else 0.4f,
            borderLighten = 0f,
            useLightBorder = false,
            shadowAlpha = if (pressed) 0.1f else 0.22f,
            elevationMultiplier = if (pressed) 0.4f else 0.9f,
            neonGlowAlpha = 0f,
        )

        BlockVisualStyle.LiquidGlass -> BlockTileStyleSpec(
            topLighten = 0.4f,
            bottomDarken = 0.02f,
            fillAlpha = if (pressed) 0.8f else 0.72f,
            useSolidBase = false,
            topHighlightAlpha = if (pressed) 0.28f else 0.42f,
            leftHighlightAlpha = if (pressed) 0.18f else 0.28f,
            bottomShadeAlpha = if (pressed) 0.06f else 0.1f,
            rightShadeAlpha = if (pressed) 0.04f else 0.08f,
            diagonalGlowAlpha = if (pressed) 0.16f else 0.24f,
            glossAlpha = if (pressed) 0.34f else 0.62f,
            borderDarken = 0f,
            borderLighten = if (pressed) 0.18f else 0.32f,
            useLightBorder = true,
            shadowAlpha = if (pressed) 0.06f else 0.12f,
            elevationMultiplier = if (pressed) 0.24f else 0.56f,
            neonGlowAlpha = 0f,
        )

        BlockVisualStyle.Neon -> BlockTileStyleSpec(
            topLighten = 0.08f,
            bottomDarken = 0.22f,
            fillAlpha = 0.94f,
            useSolidBase = false,
            topHighlightAlpha = if (pressed) 0.08f else 0.14f,
            leftHighlightAlpha = if (pressed) 0.05f else 0.08f,
            bottomShadeAlpha = if (pressed) 0.14f else 0.18f,
            rightShadeAlpha = if (pressed) 0.1f else 0.13f,
            diagonalGlowAlpha = if (pressed) 0.08f else 0.1f,
            glossAlpha = if (pressed) 0.05f else 0.09f,
            borderDarken = 0f,
            borderLighten = if (pressed) 0.14f else 0.2f,
            useLightBorder = true,
            shadowAlpha = if (pressed) 0.18f else 0.28f,
            elevationMultiplier = if (pressed) 0.24f else 0.44f,
            neonGlowAlpha = if (pressed) 0.18f else 0.26f,
        )
    }
}

@Composable
internal fun BlockTile3D(
    fillColor: Color,
    modifier: Modifier = Modifier,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 1.dp,
    cornerRadius: Dp = 8.dp,
    recessed: Boolean = false,
    elevation: Dp = if (recessed) 0.dp else 4.dp,
    interaction: BlockTileInteraction = BlockTileInteraction.Normal,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val style = LocalBlockVisualStyle.current
    val neonPulseSpeed = LocalNeonPulseSpeed.current
    val adjustedCornerRadius = when (style) {
        BlockVisualStyle.Flat -> cornerRadius * 0.9f
        BlockVisualStyle.Raised3D -> cornerRadius
        BlockVisualStyle.LiquidGlass -> cornerRadius * 1.45f
        BlockVisualStyle.Neon -> cornerRadius * 0.65f
    }
    val shape = RoundedCornerShape(adjustedCornerRadius)
    val spec = blockTileStyleSpec(
        style = style,
        recessed = recessed,
        interaction = interaction,
    )
    val resolvedBorderColor = if (borderColor != Color.Transparent) {
        borderColor
    } else if (spec.useLightBorder) {
        fillColor.lighten(spec.borderLighten)
    } else {
        fillColor.darken(spec.borderDarken)
    }
    val baseBrush = Brush.linearGradient(
        colors = if (spec.useSolidBase) {
            listOf(fillColor.copy(alpha = spec.fillAlpha), fillColor.copy(alpha = spec.fillAlpha))
        } else {
            listOf(
                fillColor.lighten(spec.topLighten).copy(alpha = spec.fillAlpha),
                fillColor.copy(alpha = spec.fillAlpha),
                fillColor.darken(spec.bottomDarken).copy(alpha = spec.fillAlpha),
            )
        }
    )
    val neonTransition = if (style == BlockVisualStyle.Neon) {
        rememberInfiniteTransition(label = "neon-glow")
    } else {
        null
    }
    val neonPulseScale = neonTransition?.animateFloat(
        initialValue = NEON_PULSE_MIN_SCALE,
        targetValue = NEON_PULSE_MAX_SCALE,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = neonPulseSpeed.durationMillis),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "neon-pulse-scale",
    )?.value ?: 1f
    val neonPulseIntensity = neonTransition?.animateFloat(
        initialValue = NEON_PULSE_MIN_INTENSITY,
        targetValue = NEON_PULSE_MAX_INTENSITY,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = neonPulseSpeed.durationMillis),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "neon-pulse-intensity",
    )?.value ?: 1f
    val neonPulseProgress = if (style == BlockVisualStyle.Neon) {
        ((neonPulseIntensity - NEON_PULSE_MIN_INTENSITY) /
            (NEON_PULSE_MAX_INTENSITY - NEON_PULSE_MIN_INTENSITY)).coerceIn(0f, 1f)
    } else {
        0f
    }
    val shadowColor = if (recessed) {
        Color.Transparent
    } else if (style == BlockVisualStyle.Neon) {
        fillColor.lighten(0.34f).copy(
            alpha = (spec.shadowAlpha * (0.75f + neonPulseProgress * 1.65f)).coerceIn(0f, 0.96f),
        )
    } else {
        fillColor.darken(0.62f).copy(alpha = spec.shadowAlpha)
    }
    val topOverlayBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = spec.topHighlightAlpha),
            Color.Transparent,
        )
    )
    val leftOverlayBrush = Brush.horizontalGradient(
        colors = listOf(
            Color.White.copy(alpha = spec.leftHighlightAlpha),
            Color.Transparent,
        )
    )
    val bottomOverlayBrush = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Black.copy(alpha = spec.bottomShadeAlpha),
        )
    )
    val rightOverlayBrush = Brush.horizontalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Black.copy(alpha = spec.rightShadeAlpha),
        )
    )
    val diagonalGlowBrush = Brush.linearGradient(
        colors = listOf(
            fillColor.lighten((spec.topLighten + spec.borderLighten).coerceAtLeast(0.08f)).copy(
                alpha = spec.diagonalGlowAlpha,
            ),
            Color.Transparent,
            Color.Black.copy(alpha = spec.diagonalGlowAlpha),
        )
    )
    val glossBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = spec.glossAlpha),
            Color.White.copy(alpha = spec.glossAlpha * 0.32f),
            Color.Transparent,
        )
    )
    val neonGlowBrush = Brush.radialGradient(
        colors = listOf(
            fillColor.lighten(0.34f).copy(
                alpha = (spec.neonGlowAlpha * (0.72f + neonPulseProgress * 0.55f)).coerceIn(0f, 0.64f),
            ),
            fillColor.lighten(0.14f).copy(
                alpha = (spec.neonGlowAlpha * 0.26f * neonPulseIntensity).coerceIn(0f, 0.22f),
            ),
            Color.Transparent,
        )
    )
    val neonCoreBrush = Brush.radialGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.06f + neonPulseProgress * 0.08f),
            fillColor.lighten(0.22f).copy(alpha = 0.08f + neonPulseProgress * 0.08f),
            Color.Transparent,
        )
    )
    val neonSheenBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.1f + neonPulseProgress * 0.08f),
            Color.White.copy(alpha = 0.03f + neonPulseProgress * 0.03f),
            Color.Transparent,
        )
    )
    val pressedScaleX = if (interaction == BlockTileInteraction.Pressed && style != BlockVisualStyle.Flat) 1.01f else 1f
    val pressedScaleY = if (interaction == BlockTileInteraction.Pressed && style != BlockVisualStyle.Flat) 0.95f else 1f

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = pressedScaleX
                scaleY = pressedScaleY
            }
            .shadow(
                elevation = elevation * when {
                    recessed -> 0f
                    style == BlockVisualStyle.Neon -> spec.elevationMultiplier * (1f + neonPulseProgress * 2f)
                    else -> spec.elevationMultiplier
                },
                shape = shape,
                ambientColor = shadowColor,
                spotColor = shadowColor,
            )
            .clip(shape)
            .background(baseBrush)
            .border(borderWidth, resolvedBorderColor, shape)
    ) {
        if (style == BlockVisualStyle.Neon && spec.neonGlowAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .graphicsLayer {
                        scaleX = neonPulseScale
                        scaleY = neonPulseScale
                        alpha = 0.82f + neonPulseProgress * 0.12f
                    }
                    .clip(shape)
                    .background(neonGlowBrush)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .fillMaxHeight(0.82f)
                    .align(Alignment.Center)
                    .graphicsLayer { alpha = 0.48f + neonPulseProgress * 0.2f }
                    .clip(shape)
                    .background(neonCoreBrush)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .fillMaxHeight(0.22f)
                    .align(Alignment.TopCenter)
                    .graphicsLayer { alpha = 0.78f + neonPulseProgress * 0.12f }
                    .clip(RoundedCornerShape(adjustedCornerRadius * 0.82f))
                    .background(neonSheenBrush)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .align(Alignment.TopCenter)
                .clip(shape)
                .background(topOverlayBrush)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.22f)
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .clip(shape)
                .background(leftOverlayBrush)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(shape)
                .background(diagonalGlowBrush)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.34f)
                .align(Alignment.TopCenter)
                .clip(shape)
                .background(glossBrush)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.26f)
                .align(Alignment.BottomCenter)
                .clip(shape)
                .background(bottomOverlayBrush)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.18f)
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .clip(shape)
                .background(rightOverlayBrush)
        )
        if (style == BlockVisualStyle.LiquidGlass) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.18f)
                    .align(Alignment.TopCenter)
                    .clip(shape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.38f),
                                Color.White.copy(alpha = 0.16f),
                                Color.Transparent,
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(shape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.16f),
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent,
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .fillMaxHeight(0.52f)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(adjustedCornerRadius * 0.9f))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.28f),
                                Color.White.copy(alpha = 0.12f),
                                Color.Transparent,
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .fillMaxHeight(0.7f)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(adjustedCornerRadius))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.03f),
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .border(
                        width = borderWidth,
                        color = Color.White.copy(alpha = 0.36f),
                        shape = shape,
                    )
            )
        }
        if (style == BlockVisualStyle.Neon) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .border(
                        width = borderWidth * (1f + neonPulseProgress * 0.28f),
                        color = resolvedBorderColor.lighten(0.12f).copy(
                            alpha = (0.62f + neonPulseProgress * 0.18f).coerceIn(0f, 0.86f),
                        ),
                        shape = shape,
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .border(
                        width = borderWidth * 0.7f,
                        color = Color.White.copy(alpha = 0.08f + neonPulseProgress * 0.06f),
                        shape = shape,
                    )
            )
        }
        content()
    }
}

