package com.compose.base.presentation.screens.core.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.compose.base.R
import com.compose.base.presentation.config.ComposeBaseTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Scaffold {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Image(
                modifier = modifier
                    .align(Alignment.Center)
                    .size(280.dp),
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
            )
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    ComposeBaseTheme {
        SplashScreen()
    }
}