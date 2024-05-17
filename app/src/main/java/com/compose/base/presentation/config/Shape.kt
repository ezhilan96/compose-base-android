package com.compose.base.presentation.config

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp

fun getDefaultShapes(sizeFactor: Float) = Shapes(
    extraSmall = ShapeDefaults.ExtraSmall.copy(CornerSize((sizeFactor * 4).dp)),
    small = ShapeDefaults.Small.copy(CornerSize((sizeFactor * 8).dp)),
    medium = ShapeDefaults.Medium.copy(CornerSize((sizeFactor * 12).dp)),
    large = ShapeDefaults.Large.copy(CornerSize((sizeFactor * 16).dp)),
    extraLarge = ShapeDefaults.ExtraLarge.copy(CornerSize((sizeFactor * 28).dp)),
)

data class CustomShapes(
    val sizeFactor: Float = 1f,
    val defaultCard: RoundedCornerShape = RoundedCornerShape((sizeFactor * 10).dp),
    val defaultCardTop: RoundedCornerShape = RoundedCornerShape(
        topStart = (sizeFactor * 10).dp,
        topEnd = (sizeFactor * 10).dp,
    ),
    val defaultCardBottom: RoundedCornerShape = RoundedCornerShape(
        bottomStart = (sizeFactor * 10).dp,
        bottomEnd = (sizeFactor * 10).dp,
    ),
    val defaultButton: RoundedCornerShape = RoundedCornerShape((sizeFactor * 4).dp),
    val max: RoundedCornerShape = RoundedCornerShape((sizeFactor * 100).dp),
    val defaultBottomSheet: RoundedCornerShape = RoundedCornerShape(
        topStart = (sizeFactor * 20).dp,
        topEnd = (sizeFactor * 20).dp,
    ),
)

val LocalCustomShapes = compositionLocalOf { CustomShapes() }

val MaterialTheme.customShapes: CustomShapes
    @Composable @ReadOnlyComposable get() = LocalCustomShapes.current

