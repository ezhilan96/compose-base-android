package com.compose.base.presentation.screens.shared.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.compose.base.presentation.config.customColors
import com.compose.base.presentation.config.textStyle

/**
 * Composable function for displaying labeled text with optional subtext.
 *
 * This composable provides a reusable way to display text with a label above it and an optional
 * subtext below it. It uses Material Design styles for formatting.
 *
 * - `modifier`: (Optional) A modifier to be applied to the entire LabeledText composable.
 * - `label`: The text to be displayed as the label.
 * - `text`: The main text content.
 * - `subText`: (Optional) The subtext to be displayed below the main text.
 * - `subTextColor`: (Optional) The color of the subtext. Defaults to the current LocalContentColor.
 *
 * This composable utilizes a `Column` to stack the label, text, and subtext (if provided).
 * It applies Material Theme styles for typography and colors.
 */
@Composable
fun LabeledText(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    subText: String? = null,
    subTextColor: Color = LocalContentColor.current,
) {
    val localModifier = Modifier
    Column(modifier = modifier) {
        Text(
            modifier = localModifier.wrapContentWidth(Alignment.Start),
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.customColors.textDark,
        )
        Text(
            modifier = localModifier.wrapContentWidth(Alignment.Start),
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (!subText.isNullOrEmpty()) {
            Text(
                modifier = localModifier.wrapContentWidth(Alignment.Start),
                text = subText,
                style = MaterialTheme.textStyle.subText,
                color = subTextColor,
            )
        }
    }
}