package compose.base.app.presentation.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import compose.base.app.R

@ExperimentalMaterial3Api
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Scaffold(containerColor = MaterialTheme.colorScheme.primaryContainer) {
        Box(modifier = modifier
            .padding(it)
            .fillMaxSize()) {
            Icon(
                modifier = modifier.align(Alignment.Center),
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "",
                tint = Color.Unspecified
            )
        }
    }
}