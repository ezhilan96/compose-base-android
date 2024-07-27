package com.compose.base.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.compose.base.R
import com.compose.base.core.Constants
import com.compose.base.presentation.config.ComposeBaseTheme
import com.compose.base.presentation.screens.MainScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity class for the application. This activity serves as the entry point for the app
 * and handles various functionalities including:
 *  - Setting up the visual style (edge-to-edge, status/navigation bar styles).
 *  - Initializing AndroidX Splash Screen.
 *  - Handling in-app update checks and flow using AppUpdateManager.
 *  - Processing deep links from notifications for opening new bookings.
 *  - Displaying the MainScreen composable.
 *
 * This activity is annotated with `@AndroidEntryPoint` allowing for dependency injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //In-app update manager
    private var appUpdateManager: AppUpdateManager? = null

    //Callback for update availability hoisted to application scope
    private lateinit var onAvailableCallBack: () -> Unit

    //Activity Result Launcher for Update Flow
    private var requestUpdateLauncher: ActivityResultLauncher<IntentSenderRequest>? =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == RESULT_CANCELED) {
                //Show update app alert when update flow is cancelled
                onAvailableCallBack()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable edge to edge and Status/Nav Bar Styling
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                ContextCompat.getColor(this, R.color.background)
            ),
            navigationBarStyle = SystemBarStyle.dark(
                ContextCompat.getColor(this, R.color.background)
            ),
        )

        //Initialize AndroidX Splash Screen
        installSplashScreen()

        super.onCreate(savedInstanceState)

        //Hide action bar in emulators
        actionBar?.hide()

        //Initialize In-app update manager
        appUpdateManager ?: run { appUpdateManager = AppUpdateManagerFactory.create(this) }

        setContent {
            ComposeBaseTheme {
                MainScreen(checkForUpdate = this::checkForUpdate)
            }
        }
    }

    //Handle intents from Notifications
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // Create New booking broadcast intent
        val notificationType = intent.extras?.getString(Constants.KEY_NOTIFICATION_TYPE)
        val newBookingType = intent.extras?.getString(Constants.KEY_NOTIFICATION_BOOKING_TYPE)
        val newBookingId = intent.extras?.getString(Constants.KEY_NOTIFICATION_BOOKING_ID)
        val internalIntent = Intent().setAction(ACTION_VIEW)
            .putExtra(Constants.KEY_NOTIFICATION_TYPE, notificationType)
            .putExtra(Constants.KEY_NOTIFICATION_BOOKING_TYPE, newBookingType)
            .putExtra(Constants.KEY_NOTIFICATION_BOOKING_ID, newBookingId)

        // Send Local Broadcast/Deep Linking for New Booking
        LocalBroadcastManager.getInstance(this).sendBroadcast(internalIntent)
    }

    /**
     * Checks for available app updates and initiates the update flow if necessary.
     *
     * This function takes two lambda functions as parameters:
     *  - `onAvailable`: This lambda is called when an update is available and the update flow is triggered.
     *  - `onUnAvailable`: This lambda is called when no update is available or if an error occurs during the update check.
     *
     * The function performs the following steps:
     *  1. Hoists the `onAvailable` callback to application scope using `onAvailableCallBack`. This
     *     ensures the callback can be invoked later from the activity result when the update flow
     *     completes (even if the activity is recreated).
     *  2. Attempts to retrieve app update information using `appUpdateManager!!.appUpdateInfo`.
     *  3. Uses `addOnSuccessListener` and `addOnFailureListener` to handle the outcome:
     *      - On success:
     *          - Checks if an update is available (`UPDATE_AVAILABLE`) and allowed (`isUpdateTypeAllowed` for IMMEDIATE type).
    - If an update is available, starts the update flow using `startUpdateFlowForResult`. This
    might either update the app in the background or prompt the user to install (depending
    on the update type and user interaction). The `requestUpdateLauncher` is used to handle
    the activity result for the update flow.
    - If no update is available, calls the `onUnAvailable` callback.
    - On failure:
     *          - Logs the exception using Firebase Crashlytics (assuming it's integrated).
     *          - Calls the `onUnAvailable` callback.
     *
     * This function provides a mechanism to check for app updates and initiate the update process
     * seamlessly within the activity.
     *
     * @param onAvailable A lambda function to be called when an update is available.
     * @param onUnAvailable A lambda function to be called when no update is available or an error occurs.
     */
    private fun checkForUpdate(onAvailable: () -> Unit, onUnAvailable: () -> Unit) {
        // Hoisting callback to application scope. invoked when update is available in Activity result.
        onAvailableCallBack = onAvailable
        try {
            val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                val isUpdateAvailable =
                    appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                        AppUpdateType.IMMEDIATE
                    )
                if (isUpdateAvailable) {
                    // Start update flow. either updates the app or prompts the user to install (when cancelled on Activity result)
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
            Firebase.crashlytics.recordException(e)
            onUnAvailable()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requestUpdateLauncher = null
    }

    companion object {
        const val ACTION_VIEW = "OPEN_DETAILS"
    }
}