package com.compose.base.presentation.screens.shared.screen.digitalSignature

import android.content.res.Configuration
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.compose.base.R
import com.compose.base.core.Constants.DEFAULT_FILE_EXTENSION
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.config.customShapes
import com.compose.base.presentation.config.spacing
import com.compose.base.presentation.config.textStyle
import com.compose.base.presentation.screens.core.ScreenNavItem
import com.compose.base.presentation.screens.shared.component.TopProgressIndicatorLight
import com.compose.base.presentation.screens.shared.dialog.DefaultAlert
import com.compose.base.presentation.util.enableGesture
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.rememberDrawController
import java.io.File
import java.io.FileOutputStream

@Composable
fun DigitalSignatureDestination(
    modifier: Modifier = Modifier,
    onResult: (String) -> Unit,
    navigateUp: () -> Unit,
    viewModel: DigitalSignatureViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DigitalSignatureScreen(
        modifier = modifier,
        isLoading = uiState.isLoading,
        uiEvent = viewModel::onUiEvent,
        navigateUp = navigateUp,
    )
    LaunchedEffect(uiState.screenStack) {
        uiState.screenStack.forEach { navigationItem ->
            when (navigationItem) {
                DigitalSignatureNavItem.Done -> onResult(uiState.screenState.sign!!)
            }
        }
    }
    if (uiState.screenStack.contains(ScreenNavItem.ALERT_DIALOG)) {
        DefaultAlert(
            message = uiState.alertMessage.asString(),
            onDismiss = { viewModel.onUiEvent(DigitalSignatureUiEvent.OnDismiss) },
        )
    }
}

@Composable
fun DigitalSignatureScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    uiEvent: (DigitalSignatureUiEvent) -> Unit,
    navigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val drawController = rememberDrawController()
    drawController.changeColor(Color.Black)
    drawController.changeStrokeWidth(5f)
    Scaffold(modifier = modifier.enableGesture(!isLoading)) {
        Column(
            modifier = modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    modifier = modifier.padding(
                        horizontal = MaterialTheme.spacing.grid05,
                        vertical = MaterialTheme.spacing.grid1
                    ),
                    onClick = navigateUp,
                ) {
                    Icon(
                        modifier = modifier.padding(MaterialTheme.spacing.grid1),
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                    )
                }
                Text(
                    modifier = modifier.padding(start = MaterialTheme.spacing.grid1),
                    text = stringResource(R.string.title_customer_signature),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            TopProgressIndicatorLight(
                modifier = modifier.fillMaxWidth(),
                isLoading = isLoading,
            )

            DrawBox(
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1f),
                drawController = drawController,
                bitmapCallback = { image, _ ->
                    val documentFile = File(
                        context.cacheDir,
                        System.currentTimeMillis().toString() + DEFAULT_FILE_EXTENSION
                    )
                    try {
                        val fileOutputStream = FileOutputStream(documentFile)
                        image?.asAndroidBitmap()?.compress(
                            Bitmap.CompressFormat.JPEG, 30, fileOutputStream
                        )
                        fileOutputStream.flush()
                        fileOutputStream.close()
                    } catch (e: Exception) {
                        Firebase.crashlytics.recordException(e)
                    }
                    uiEvent(DigitalSignatureUiEvent.OnSubmit(documentFile.toUri()))
                },
            )
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.customShapes.defaultBottomSheet,
                    )
            ) {
                Row(
                    modifier = modifier
                        .padding(MaterialTheme.spacing.grid1)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TextButton(
                        onClick = {
                            drawController.reset()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.inverseOnSurface
                        ),
                        shape = MaterialTheme.customShapes.defaultButton,
                    ) {
                        Icon(
                            modifier = modifier.padding(end = MaterialTheme.spacing.grid1),
                            imageVector = Icons.Outlined.Replay,
                            contentDescription = null,
                        )
                        Text(
                            text = stringResource(R.string.button_reset),
                            style = MaterialTheme.textStyle.ubuntuLabel,
                        )
                    }
                    TextButton(
                        onClick = {
                            if (drawController.exportPath().path.isNotEmpty()) {
                                drawController.saveBitmap()
                            } else {
                                Toast.makeText(
                                    context, ContextCompat.getString(
                                        context, R.string.error_empty_signature
                                    ), Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.inverseOnSurface
                        ),
                        shape = MaterialTheme.customShapes.defaultButton,
                    ) {
                        Icon(
                            modifier = modifier.padding(end = MaterialTheme.spacing.grid1),
                            imageVector = Icons.Outlined.Done,
                            contentDescription = null,
                        )
                        Text(
                            text = stringResource(id = R.string.button_submit),
                            style = MaterialTheme.textStyle.ubuntuLabel,
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview
@Composable
fun SignaturePreview() {
    ComposeBaseTheme {
        DigitalSignatureScreen(
            Modifier,
            false,
            {},
            {},
        )
    }
}