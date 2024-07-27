@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.compose.base.presentation.screens.home.profile

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.compose.base.BuildConfig
import com.compose.base.R
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.config.customColors
import com.compose.base.presentation.config.spacing
import com.compose.base.presentation.config.textStyle
import com.compose.base.presentation.screens.shared.dialog.DefaultAlert
import com.compose.base.presentation.util.AppSignatureHelper
import com.compose.base.presentation.util.getCustomerUrl

@Composable
fun ProfileDestination(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ProfileScreen(
        modifier = modifier,
        uiState = uiState,
        onLogout = viewModel::onLogout,
        onDismissAlert = viewModel::onDismissAlert,
        onBackPress = navigateUp,
    )

    LaunchedEffect(Unit) {
        (context as ComponentActivity).enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                ContextCompat.getColor(context, R.color.black)
            ),
            navigationBarStyle = SystemBarStyle.light(
                ContextCompat.getColor(context, R.color.white),
                ContextCompat.getColor(context, R.color.light)
            ),
        )
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    uiState: ProfileScreenUiState,
    onLogout: () -> Unit = {},
    onDismissAlert: () -> Unit = {},
    onBackPress: () -> Unit = {},
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val customerBaseUrl = getCustomerUrl()
    val uriHandler = LocalUriHandler.current
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    titleContentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    navigationIconContentColor = MaterialTheme.customColors.lightVariant,
                ),
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(
                            modifier = modifier.padding(MaterialTheme.spacing.grid1),
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                title = {},
            )
        },
    ) {
        Column(
            modifier = modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Surface(
                color = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.4f),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        modifier = modifier.fillMaxSize(.25f),
                        imageVector = Icons.Filled.Person,
                        contentDescription = null
                    )
                    Spacer(modifier = modifier.height(MaterialTheme.spacing.grid1))
                    Text(
                        text = uiState.name, style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = modifier.height(MaterialTheme.spacing.grid1))
                    Text(
                        text = uiState.phoneNumber,
                        style = MaterialTheme.textStyle.onDarkBody,
                        color = MaterialTheme.customColors.textLight,
                    )
                    Spacer(modifier = modifier.height(MaterialTheme.spacing.grid1))
                    Text(
                        text = uiState.email,
                        style = MaterialTheme.textStyle.onDarkBody,
                        color = MaterialTheme.customColors.textLight,
                    )
                }
            }

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    HorizontalDivider(modifier = modifier.padding(horizontal = MaterialTheme.spacing.unit10))
                    TextButton(
                        modifier = modifier.fillMaxWidth(),
                        onClick = { uriHandler.openUri("${customerBaseUrl}terms") },
                        shape = RoundedCornerShape(MaterialTheme.spacing.zero),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(start = MaterialTheme.spacing.grid1),
                            text = stringResource(R.string.button_terms),
                            textAlign = TextAlign.Start
                        )
                    }
                    HorizontalDivider(modifier = modifier.padding(horizontal = MaterialTheme.spacing.unit10))
                    TextButton(
                        modifier = modifier.fillMaxWidth(),
                        onClick = { uriHandler.openUri("${customerBaseUrl}policy") },
                        shape = RoundedCornerShape(MaterialTheme.spacing.zero),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(start = MaterialTheme.spacing.grid1),
                            text = stringResource(R.string.button_privacy),
                            textAlign = TextAlign.Start
                        )
                    }
                    HorizontalDivider(modifier = modifier.padding(horizontal = MaterialTheme.spacing.unit10))
                    TextButton(
                        modifier = modifier.fillMaxWidth(),
                        onClick = onLogout,
                        shape = RoundedCornerShape(MaterialTheme.spacing.zero),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(start = MaterialTheme.spacing.grid1),
                            text = stringResource(R.string.button_logout),
                            textAlign = TextAlign.Start
                        )
                    }
                }

                Text(
                    modifier = modifier
                        .padding(MaterialTheme.spacing.grid1)
                        .combinedClickable(onLongClick = {
                            clipboardManager.setText(AnnotatedString(AppSignatureHelper(context).appSignatures.first()))
                        }) {},
                    text = stringResource(R.string.label_version, BuildConfig.VERSION_NAME),
                    style = MaterialTheme.textStyle.logoTitle,
                    color = MaterialTheme.customColors.lightVariant,
                )

            }
        }

        if (uiState.enableAlert) {
            DefaultAlert(
                message = uiState.alertMessage.asString(),
                onDismiss = onDismissAlert,
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview
@Composable
fun ProfileScreenPreview() {
    ComposeBaseTheme {
        ProfileScreen(uiState = ProfileScreenUiState())
    }
}