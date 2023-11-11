package compose.base.app.presentation.pages.auth.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@ExperimentalMaterial3Api
@Composable
fun LoginRoute(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    navController: NavController,
) {
    val uiState by viewModel.apply {
        this.navController = navController
    }.loginUiState.collectAsStateWithLifecycle()

    LoginScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel::handleEvent,
    )
}

@ExperimentalMaterial3Api
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    uiEvent: (LoginUiEvent) -> Unit,
) {
    Scaffold(topBar = { TopAppBar(title = { Text(text = "Base app login") }) }) {
        Box(
            modifier = modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Text(modifier = modifier.align(Alignment.Center), text = "Login")
        }
    }
}