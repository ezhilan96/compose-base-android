package compose.base.app.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import compose.base.app.R

val defaultTypography = Typography()

val Typography = Typography(

    displayLarge = defaultTypography.displayLarge.copy(
        fontFamily = FontFamily(Font(R.font.montserrat_regular))
    ),
    displayMedium = defaultTypography.displayMedium.copy(
        fontFamily = FontFamily(Font(R.font.montserrat_medium)),
        fontSize = 11.sp,
    ),
    displaySmall = defaultTypography.displaySmall.copy(
        fontFamily = FontFamily(Font(R.font.montserrat_regular))
    ),

    headlineLarge = defaultTypography.headlineLarge.copy(
        fontFamily = FontFamily(Font(R.font.montserrat_regular))
    ),
    headlineMedium = defaultTypography.headlineMedium.copy(
        fontFamily = FontFamily(Font(R.font.montserrat_regular))
    ),
    headlineSmall = defaultTypography.headlineSmall.copy(
        fontFamily = FontFamily(Font(R.font.montserrat_regular))
    ),

    titleLarge = defaultTypography.titleLarge.copy(
        fontFamily = FontFamily(Font(R.font.montserrat_bold)),
        fontSize = 16.sp,
    ),
    titleMedium = defaultTypography.titleMedium.copy(
        fontFamily = FontFamily(Font(R.font.montserrat_medium)),
        fontSize = 14.sp,
    ),
    titleSmall = defaultTypography.titleSmall.copy(
        fontFamily = FontFamily(Font(R.font.montserrat_bold)),
        fontSize = 10.sp,
    ),

    bodyLarge = defaultTypography.bodyLarge.copy(
        fontFamily = FontFamily(Font(R.font.montserrat_regular))
    ),
    bodyMedium = defaultTypography.bodyMedium.copy(
        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
        fontSize = 12.sp,
    ),
    bodySmall = defaultTypography.bodySmall.copy(
        fontFamily = FontFamily(Font(R.font.montserrat_medium)),
        fontSize = 10.sp,
    ),

    labelLarge = defaultTypography.labelLarge.copy(
        fontFamily = FontFamily(Font(R.font.montserrat_regular))
    ),
    labelMedium = defaultTypography.labelMedium.copy(
        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
        fontSize = 14.sp,
    ),
    labelSmall = defaultTypography.labelSmall.copy(
        fontFamily = FontFamily(Font(R.font.montserrat_regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
    ),
)

data class TextStyles(
    val title: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.ubuntu_medium)),
        fontStyle = FontStyle.Italic,
        fontSize = 12.sp
    )
)

val LocalTextStyle = compositionLocalOf { TextStyles() }

val MaterialTheme.textStyle: TextStyles
    @Composable
    @ReadOnlyComposable
    get() = LocalTextStyle.current


