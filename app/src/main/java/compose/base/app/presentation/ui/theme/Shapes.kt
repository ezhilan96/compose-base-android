package compose.base.app.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp

val defaultShapes = Shapes(
    extraSmall = ShapeDefaults.ExtraSmall.copy(),
)

data class CustomShapes(
    val smallStart: RoundedCornerShape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp),
    val mediumStart: RoundedCornerShape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
    val largeStart: RoundedCornerShape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
    val smallTop: RoundedCornerShape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
    val mediumTop: RoundedCornerShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
    val largeTop: RoundedCornerShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    val smallEnd: RoundedCornerShape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp),
    val mediumEnd: RoundedCornerShape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
    val largeEnd: RoundedCornerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
    val smallBottom: RoundedCornerShape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp),
    val mediumBottom: RoundedCornerShape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
    val largeBottom: RoundedCornerShape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
)

val LocalCustomShapes = compositionLocalOf { CustomShapes() }

val MaterialTheme.customShapes: CustomShapes
    @Composable @ReadOnlyComposable get() = LocalCustomShapes.current
