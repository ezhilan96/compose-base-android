package com.compose.base.presentation.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.compose.base.R
import com.compose.base.domain.entity.LatLng

/**
 * Requests the user's current location with high accuracy.
 *
 * This function retrieves the user's current location using the device's location services.
 * It requires the ACCESS_FINE_LOCATION permission, and the `@SuppressLint("MissingPermission")` annotation
 * suppresses a lint warning because explicit permission checking is not included in this example.
 * In a real application, you should always handle permission requests before accessing location data.
 *
 * @receiver The application context required for accessing location services.
 * @param onResult A callback function that receives the retrieved location (if successful) or null (if failed).
 */
@SuppressLint("MissingPermission")
fun Context.requestCurrentLocation(onResult: (Location?) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    val currentLocationTask =
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
    currentLocationTask.addOnSuccessListener(onResult).addOnFailureListener { onResult(null) }
        .addOnFailureListener { onResult(null) }
}

/**
 * Retrieves an Intent to initiate the Phone Number Hint API flow for user phone number selection.
 *
 * This function utilizes the Phone Number Hint API to simplify acquiring user phone numbers
 * within your app. It attempts to retrieve a PendingIntent that triggers a system UI where the user
 * can select a phone number from their SIM cards. This eliminates the need for manual entry.
 *
 * If the Phone Number Hint API is unavailable, it falls back to a deprecated approach
 * using the GetPhoneNumberHintIntent API. This fallback might not be available on all devices
 * and should be considered a temporary solution.
 *
 * This function also handles potential exceptions and logs them using Firebase Crashlytics.
 *
 * @receiver The application context required for accessing system services.
 * @param onResult A callback function that receives the retrieved PendingIntent (if successful)
 *                  or null (if failed or unavailable).
 */
fun Context.getPhoneNumberHintIntent(
    onResult: (PendingIntent?) -> Unit
) {
    Identity.getSignInClient(this as Activity)
        .getPhoneNumberHintIntent(GetPhoneNumberHintIntentRequest.builder().build())
        .addOnSuccessListener {
            onResult(it)
        }.addOnFailureListener {
            Firebase.crashlytics.recordException(it)
            try {
                val hintRequest: HintRequest =
                    HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build()
                val intent: PendingIntent =
                    Credentials.getClient(this).getHintPickerIntent(hintRequest)
                onResult(intent)
            } catch (e: Exception) {
                Firebase.crashlytics.recordException(e)
                onResult(null)
            }
        }.addOnCanceledListener {
            onResult(null)
        }
}

/**
 * Initiates a call intent to dial a phone number.
 *
 * This function creates an Intent with the `ACTION_DIAL` action and sets the phone number
 * as the data URI. This intent will typically launch the default dialer app on the user's device
 * allowing them to confirm the call before initiating it.
 *
 * @receiver The application context required for launching the intent.
 * @param phone The phone number string to be dialed.
 */
fun Context.dialPhoneNumber(phone: String) {
    val uri = "tel:$phone"
    val callIntent = Intent(Intent.ACTION_DIAL)
    callIntent.data = Uri.parse(uri)
    ContextCompat.startActivity(this, callIntent, null)
}

/**
 * Opens Google Maps navigation for a specific location.
 *
 * This function opens Google Maps with navigation functionality for a provided LatLng object.
 * It attempts to launch the official Google Maps app directly with navigation intent.
 * If the Google Maps app is not installed, it falls back to opening a web link in the user's preferred browser.
 *
 * This function utilizes String resources for flexibility in customizing labels within the Intent.
 *
 * @receiver The application context required for launching intents.
 * @param latLng The LatLng object representing the location to navigate to.
 */
fun Context.openGoogleMapNavigation(latLng: LatLng) {
    val gmmIntentUri = UiText.Resource(
        R.string.google_map_query,
        latLng.lat,
        latLng.lng,
    )
    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(gmmIntentUri.asString(this)))
    mapIntent.setPackage(UiText.Resource(R.string.google_map_package).asString(this))
    try {
        ContextCompat.startActivity(this, mapIntent, null)
    } catch (e: Exception) {
        Firebase.crashlytics.recordException(e)
        val googleMapUrl = UiText.Resource(
            R.string.url_google_map,
            latLng.lat,
            latLng.lng,
        )
        ContextCompat.startActivity(
            this, Intent.createChooser(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(googleMapUrl.asString(this)),
                ),
                UiText.Resource(
                    R.string.title_map,
                    latLng.lat,
                    latLng.lng,
                ).asString(this),
            ), null
        )
    }
}

/**
 * Requests user's permission to enable location services for the app.
 *
 * This function checks the user's location settings using the LocationServices API.
 * It utilizes a LocationSettingsRequest to verify if location is enabled and meets
 * the specified criteria (set in the LocationRequest). If location is disabled or doesn't
 * meet the criteria, a system dialog will be presented to the user prompting them
 * to enable it.
 *
 * This function uses the `@SuppressLint("MissingPermission")` annotation to suppress a lint warning
 * because explicit permission checking is not included in this example. In a real application,
 * you should always handle location permissions before requesting location updates.
 *
 * @receiver The application context required for accessing location services.
 * @param onSuccess A callback function that receives a LocationSettingsResponse object
 *                  indicating successful location settings check.
 * @param onCancel A callback function that is triggered when the user cancels the location
 *                  settings dialog (if presented).
 * @param onFailure A callback function that receives an Exception if an error occurs.
 */
@SuppressLint("MissingPermission")
fun Context.requestLocationService(
    onSuccess: (LocationSettingsResponse) -> Unit,
    onCancel: () -> Unit,
    onFailure: (Exception) -> Unit,
) {
    val mLocationRequest = LocationRequest.Builder(0).build()
    val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
    val client: SettingsClient = LocationServices.getSettingsClient(this)
    val checkLocationSettingsTask = client.checkLocationSettings(builder.build())
    checkLocationSettingsTask.addOnSuccessListener(onSuccess).addOnCanceledListener(onCancel)
        .addOnFailureListener(onFailure)
}

/**
 * Retrieves the application URL from string resources.
 *
 * By using string resources, you can easily manage and update the app URL without modifying code.
 *
 * @receiver The application context required for accessing string resources.
 * @return The constructed app URL as a String.
 */
fun Context.getAppUrl(): String = getString(R.string.app_url, getString(R.string.base_url))

/**
 * Retrieves the application URL from string resources within a composable function.
 *
 * By using string resources, you can easily manage and update the app URL without modifying code.
 *
 * @return The constructed app URL as a String.
 */
@Composable
fun getAppUrl(): String = stringResource(R.string.app_url, stringResource(R.string.base_url))

/**
 * Retrieves the customer site URL from string resources.
 *
 * By using string resources, you can easily manage and update the customer URL without modifying code.
 *
 * @receiver The application context required for accessing string resources.
 * @return The constructed customer URL as a String.
 */
fun Context.getCustomerUrl(): String =
    getString(R.string.customer_url, getString(R.string.base_url))

/**
 * Retrieves the customer URL from string resources within a composable function.
 *
 * By using string resources, you can easily manage and update the customer URL without modifying code.
 *
 * @return The constructed customer URL as a String.
 */
@Composable
fun getCustomerUrl(): String =
    stringResource(R.string.customer_url, stringResource(R.string.base_url))

/**
 * Retrieves the socket URL from string resources.
 *
 * By using string resources, you can easily manage and update the socket URL without modifying code.
 *
 * @receiver The application context required for accessing string resources.
 * @return The constructed socket URL as a String.
 */
fun Context.getSocketUrl(): String = getString(R.string.socket_url, getString(R.string.base_url))