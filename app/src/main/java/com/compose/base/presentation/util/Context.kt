package com.compose.base.presentation.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.core.content.ContextCompat
import com.compose.base.R
import com.compose.base.domain.entity.LatLng
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@SuppressLint("MissingPermission")
fun Context.requestCurrentLocation(onResult: (Location?) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    val currentLocationTask =
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
    currentLocationTask.addOnSuccessListener(onResult).addOnFailureListener { onResult(null) }
        .addOnFailureListener { onResult(null) }
}

fun Context.getPhoneNumberHintIntent(
    onResult: (PendingIntent?) -> Unit
) {
    try {
        val hintRequest: HintRequest =
            HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build()
        val intent: PendingIntent = Credentials.getClient(this).getHintPickerIntent(hintRequest)
        onResult(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        Identity.getSignInClient(this as Activity)
            .getPhoneNumberHintIntent(GetPhoneNumberHintIntentRequest.builder().build())
            .addOnSuccessListener {
                onResult(it)
            }.addOnFailureListener {
                it.printStackTrace()
                onResult(null)
            }.addOnCanceledListener {
                onResult(null)
            }
    }
}

fun Context.dialPhoneNumber(phone: String) {
    val uri = "tel:$phone"
    val callIntent = Intent(Intent.ACTION_DIAL)
    callIntent.data = Uri.parse(uri)
    ContextCompat.startActivity(this, callIntent, null)
}

fun Context.openGoogleMapNavigation(latLng: LatLng) {
    val gmmIntentUri = UiText.Resource(
        R.string.google_map_query,
        latLng.lat,
        latLng.lng,
    )
    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(gmmIntentUri.asString(this)))
    mapIntent.setPackage(UiText.Resource(R.string.google_map_package).asString(this))
    mapIntent.resolveActivity(packageManager)?.let {
        ContextCompat.startActivity(this, mapIntent, null)
    } ?: run {
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