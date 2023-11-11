package compose.base.app.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val md_theme_light_primary = Color(0xFFEFA603)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFFFC647)
val md_theme_light_onPrimaryContainer = Color(0xFFFFFFFF)
val md_theme_light_secondary = Color(0xFFF0F3FF)
val md_theme_light_onSecondary = Color(0xFF222222)
val md_theme_light_secondaryContainer = Color(0xFF222222)
val md_theme_light_onSecondaryContainer = Color(0xFFFFFFFF)
val md_theme_light_tertiary = Color(0xFF7D5260)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFFFD8E4)
val md_theme_light_onTertiaryContainer = Color(0xFF31111D)
val md_theme_light_error = Color(0xFFB3261E)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFF9DEDC)
val md_theme_light_onErrorContainer = Color(0xFF410E0B)
val md_theme_light_outline = Color(0xFF79747E)
val md_theme_light_background = Color(0xFFFFFFFF)
val md_theme_light_onBackground = Color(0xFF222222)
val md_theme_light_surface = Color(0xFFFFFFFF)
val md_theme_light_onSurface = Color(0xFF222222)
val md_theme_light_surfaceVariant = Color(0xFFE7E0EC)
val md_theme_light_onSurfaceVariant = Color(0xFF9C9C9C)
val md_theme_light_inverseSurface = Color(0xFF141414)
val md_theme_light_inverseOnSurface = Color(0xFFFFFFFF)
val md_theme_light_inversePrimary = Color(0xFFD0BCFF)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFFFFFFFF)
val md_theme_light_outlineVariant = Color(0xFFF3F3F3)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFFEFA603)
val md_theme_dark_onPrimary = Color(0xFFFFFFFF)
val md_theme_dark_primaryContainer = Color(0xFFFFC647)
val md_theme_dark_onPrimaryContainer = Color(0xFFFFFFFF)
val md_theme_dark_secondary = Color(0xFFF0F3FF)
val md_theme_dark_onSecondary = Color(0xFF222222)
val md_theme_dark_secondaryContainer = Color(0xFF222222)
val md_theme_dark_onSecondaryContainer = Color(0xFFFFFFFF)
val md_theme_dark_tertiary = Color(0xFF7D5260)
val md_theme_dark_onTertiary = Color(0xFFFFFFFF)
val md_theme_dark_tertiaryContainer = Color(0xFFFFD8E4)
val md_theme_dark_onTertiaryContainer = Color(0xFF31111D)
val md_theme_dark_error = Color(0xFFB3261E)
val md_theme_dark_onError = Color(0xFFFFFFFF)
val md_theme_dark_errorContainer = Color(0xFFF9DEDC)
val md_theme_dark_onErrorContainer = Color(0xFF410E0B)
val md_theme_dark_outline = Color(0xFF79747E)
val md_theme_dark_background = Color(0xFFFFFFFF)
val md_theme_dark_onBackground = Color(0xFF222222)
val md_theme_dark_surface = Color(0xFFFFFFFF)
val md_theme_dark_onSurface = Color(0xFF222222)
val md_theme_dark_surfaceVariant = Color(0xFFE7E0EC)
val md_theme_dark_onSurfaceVariant = Color(0xFF9C9C9C)
val md_theme_dark_inverseSurface = Color(0xFF141414)
val md_theme_dark_inverseOnSurface = Color(0xFFFFFFFF)
val md_theme_dark_inversePrimary = Color(0xFFD0BCFF)
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = Color(0xFFFFFFFF)
val md_theme_dark_outlineVariant = Color(0xFFF3F3F3)
val md_theme_dark_scrim = Color(0xFF000000)

data class Colors(
    val textLight: Color = Color(0xFFFFFFFF)
)

val LocalColors = compositionLocalOf { Colors() }

val MaterialTheme.colors: Colors
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current