@file:OptIn(ExperimentalMaterial3Api::class)

package com.compose.base.presentation.screens.core.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.compose.base.R
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.config.customShapes
import com.compose.base.presentation.config.spacing

@Composable
fun NoInternetScreen(
    modifier: Modifier = Modifier,
    onRetryConnection: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                modifier = modifier.padding(all = MaterialTheme.spacing.grid4),
                painter = painterResource(id = R.drawable.ic_no_internet),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Text(modifier = modifier.padding(horizontal = MaterialTheme.spacing.unit50),
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily(Font(R.font.roboto_bold)),
                            fontSize = 22.sp,
                        )
                    ) {
                        append(
                            "Oops!\n"
                        )
                    }
                    withStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily(Font(R.font.roboto_medium)),
                            fontSize = 22.sp,

                            )
                    ) {
                        append("Unable to reach you!\n")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily(Font(R.font.roboto_regular)),
                            fontSize = 16.sp
                        )
                    ) {
                        append("\nCheck your internet connection to get back in network.")
                    }
                })
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.grid4),
                horizontalArrangement = Arrangement.Center
            ) {

                Button(
                    onClick = onRetryConnection,
                    shape = MaterialTheme.customShapes.defaultButton,
                ) {
                    Text(
                        text = stringResource(R.string.button_try_again),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun NoInternetPreview() {
    ComposeBaseTheme {
        NoInternetScreen {

        }
    }
}