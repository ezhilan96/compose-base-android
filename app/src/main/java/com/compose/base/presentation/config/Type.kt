package com.compose.base.presentation.config

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.compose.base.R

val defaultTypography = Typography()

fun getTypography(sizeFactor: Float) = Typography(

    displayLarge = defaultTypography.displayLarge.copy(
        fontFamily = FontFamily(Font(R.font.roboto_regular))
    ),
    displayMedium = defaultTypography.displayMedium.copy(
        fontFamily = FontFamily(Font(R.font.roboto_regular))
    ),
    displaySmall = defaultTypography.displaySmall.copy(
        fontFamily = FontFamily(Font(R.font.roboto_regular))
    ),

    headlineLarge = defaultTypography.headlineLarge.copy(
        fontFamily = FontFamily(Font(R.font.roboto_regular))
    ),
    headlineMedium = defaultTypography.headlineMedium.copy(
        fontFamily = FontFamily(Font(R.font.roboto_regular))
    ),
    headlineSmall = defaultTypography.headlineSmall.copy(
        fontFamily = FontFamily(Font(R.font.roboto_regular))
    ),

    titleLarge = defaultTypography.titleLarge.copy(
        fontFamily = FontFamily(Font(R.font.roboto_medium)),
    ),
    titleMedium = defaultTypography.titleMedium.copy(
        fontFamily = FontFamily(Font(R.font.roboto_medium)),
    ),
    titleSmall = defaultTypography.titleSmall.copy(
        fontFamily = FontFamily(Font(R.font.roboto_medium)),
    ),

    bodyLarge = defaultTypography.bodyLarge.copy(
        fontFamily = FontFamily(Font(R.font.roboto_regular))
    ),
    bodyMedium = defaultTypography.bodyMedium.copy(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
    ),
    bodySmall = defaultTypography.bodySmall.copy(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
    ),

    labelLarge = defaultTypography.labelLarge.copy(
        fontFamily = FontFamily(Font(R.font.roboto_medium)),
    ),
    labelMedium = defaultTypography.labelMedium.copy(
        fontFamily = FontFamily(Font(R.font.roboto_medium)),
    ),
    labelSmall = defaultTypography.labelSmall.copy(
        fontFamily = FontFamily(Font(R.font.roboto_medium)),
    ),
)


data class TextStyles(

    val sizeFactor: Float = 1f,

    val phoneLink: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_bold)),
        textDecoration = TextDecoration.Underline,
        fontSize = (sizeFactor * 12).sp
    ),

    val moreLink: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_bold)), fontSize = (sizeFactor * 10).sp
    ),

    val amountLabel: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontWeight = FontWeight.Medium,
        fontSize = (sizeFactor * 12).sp,
    ),

    val amount: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontWeight = FontWeight.Medium,
        fontSize = (sizeFactor * 14).sp,
    ),

    val amountLabelHeading: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontWeight = FontWeight.SemiBold,
        fontSize = (sizeFactor * 12).sp,
    ),

    val amountHeading: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontWeight = FontWeight.SemiBold,
        fontSize = (sizeFactor * 14).sp,
    ),

    val amountLabelHeading2: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_bold)),
        fontWeight = FontWeight.Bold,
        fontSize = (sizeFactor * 13).sp,
    ),

    val amountHeading2: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_bold)),
        fontWeight = FontWeight.Bold,
        fontSize = (sizeFactor * 15).sp,
    ),

    val navigateLabel: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_medium)),
        fontSize = (sizeFactor * 12).sp,
    ),

    val tabTitle: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_bold)),
        fontSize = (sizeFactor * 12).sp,
    ),

    val labelTiny: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_bold)),
        fontSize = (sizeFactor * 8).sp,
    ),

    val noteTiny: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 8).sp,
    ),

    val subText: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontWeight = FontWeight.Light,
        fontSize = (sizeFactor * 8).sp,
    ),

    val filterTitleMedium: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 15).sp,
    ),

    val filterBodyMedium: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 14).sp,
    ),

    val buttonLabelMedium: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_medium)),
        fontSize = (sizeFactor * 14).sp,
    ),

    val emptyListTitle: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_medium)),
        fontSize = (sizeFactor * 16).sp,
    ),

    val onDarkBody: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)), fontSize = (sizeFactor * 13).sp
    ),

    val logoTitle: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontStyle = FontStyle.Italic,
        fontSize = (sizeFactor * 12).sp
    ),

    val contactName: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 16).sp,
    ),

    val ubuntuLabel: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 14).sp,
    ),

    val ubuntuBody: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 14).sp,
    ),

    val otpLabel: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 14).sp,
        letterSpacing = (sizeFactor * 16).sp,
        textAlign = TextAlign.Center
    ),

    val bookingOtpLabel: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_medium)),
        fontSize = (sizeFactor * 18).sp,
        letterSpacing = (sizeFactor * 32).sp,
        textAlign = TextAlign.Center
    ),

    val areaLabel: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_medium)), fontSize = (sizeFactor * 12).sp
    ),

    val viaLabel: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontWeight = FontWeight.Medium,
        fontSize = (sizeFactor * 8).sp,
    ),

    val viaBody: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontWeight = FontWeight.Medium,
        fontSize = (sizeFactor * 11).sp,
    ),

    val welcomeNotesHeadline: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontWeight = FontWeight.Bold,
        fontSize = (sizeFactor * 38).sp,
        lineHeight = (sizeFactor * 40).sp,
    ), val welcomeNotesBody: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 16).sp,
        lineHeight = (sizeFactor * 40).sp,
    ), val loginHeadline: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_medium)),
        fontWeight = FontWeight.Medium,
        fontSize = (sizeFactor * 18).sp,
    ), val loginSmall: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 12).sp,
        lineHeight = (sizeFactor * 20).sp,
    ), val loginLabel: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 12).sp,
    ), val loginLabelLarge: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 16).sp,
    ), val loginBody: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 16).sp,
    ), val loginDisplay: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_bold)),
        fontSize = (sizeFactor * 16).sp,
    ), val otpInputBody: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 20).sp,
        fontWeight = FontWeight.Light,
        textAlign = TextAlign.Center,
    ), val otpDisplay: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 14).sp,
    ),

    val fareDetailHeadline: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_bold)),
        fontSize = (sizeFactor * 50).sp,
    ),

    val fareDetailBody: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 32).sp,
    ),

    val fareDetailLabel: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_light)),
        fontSize = (sizeFactor * 14).sp,
    ),

    val fareDetailLabelSmall: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 12).sp,
    ),

    val fareDetailLabelExtraSmall: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_light)),
        fontSize = (sizeFactor * 10).sp,
    ),

    val fareDetailDisplay: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_medium)),
        fontSize = (sizeFactor * 14).sp,
    ), val termsNote: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
    ), val userImageLabel: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.roboto_regular)),
        fontSize = (sizeFactor * 13).sp,
    )
)

val LocalTextStyle = compositionLocalOf { TextStyles() }

val MaterialTheme.textStyle: TextStyles
    @Composable @ReadOnlyComposable get() = LocalTextStyle.current

