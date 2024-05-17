package com.compose.base.presentation.config

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val sizeFactor: Float = 1f,
    val zero: Dp = 0.dp,
    val unit1: Dp = (sizeFactor * 1).dp,
    val unit2: Dp = (sizeFactor * 2).dp,
    val unit4: Dp = (sizeFactor * 4).dp,
    val unit6: Dp = (sizeFactor * 6).dp,
    val unit10: Dp = (sizeFactor * 10).dp,
    val unit20: Dp = (sizeFactor * 20).dp,
    val unit50: Dp = (sizeFactor * 50).dp,
    val unit100: Dp = (sizeFactor * 100).dp,

    val grid1: Dp = (sizeFactor * 8).dp,
    val grid2: Dp = (sizeFactor * 16).dp,
    val grid3: Dp = (sizeFactor * 24).dp,
    val grid4: Dp = (sizeFactor * 32).dp,
    val grid5: Dp = (sizeFactor * 40).dp,
)

val LocalSpacing = compositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable @ReadOnlyComposable get() = LocalSpacing.current