package com.compose.base.data.model.remote.request

data class LiveTrackingRequest(
    val latitude: Double,
    val longitude: Double,
    val bearingAngle: Float,
    val deviceId: String,
)

data class LocationSocketMessage(
    val latitude: Double,
    val longitude: Double,
    val direction: Float,
)