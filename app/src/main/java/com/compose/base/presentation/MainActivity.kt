package com.compose.base.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.screens.MainScreen
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

private var appUpdateManager: AppUpdateManager? = null

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val intentState: MutableStateFlow<Intent> by lazy { MutableStateFlow(intent) }

    private lateinit var onAvailableCallBack: () -> Unit
    private var requestUpdateLauncher: ActivityResultLauncher<IntentSenderRequest>? =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == RESULT_CANCELED) {
                onAvailableCallBack()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        appUpdateManager ?: run { appUpdateManager = AppUpdateManagerFactory.create(this) }
        setContent {
            ComposeBaseTheme {
                MainScreen(
                    intentState = intentState,
                    checkForUpdate = this::checkForUpdate,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentState.value = intent
    }

    private fun checkForUpdate(onAvailable: () -> Unit, onUnAvailable: () -> Unit) {
        onAvailableCallBack = onAvailable
        try {
            val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                val shouldStartUpdateFLow =
                    appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                        AppUpdateType.IMMEDIATE
                    )
                if (shouldStartUpdateFLow) {
                    appUpdateManager!!.startUpdateFlowForResult(
                        appUpdateInfo,
                        requestUpdateLauncher!!,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                    )
                } else {
                    onUnAvailable()
                }
            }.addOnFailureListener {
                onUnAvailable()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onUnAvailable()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requestUpdateLauncher = null
    }
}