package com.compose.base.data.model.remote.request

import com.compose.base.core.Constants

data class B2cStatusUpdateRequest(
    val bookingId: Int,
    val driverId: Int,
    val latitude: Double,
    val longitude: Double,
    val platform: String = Constants.JSON_ANDROID,
    val status: Int,
)

data class B2bStatusUpdateRequest(
    val bookingType: String = Constants.JSON_CORPORATE,
    val latitude: Double,
    val longitude: Double,
    val deviceId: String,
    val platform: String = Constants.JSON_ANDROID,
    val runningKm: Double?,
)

data class EtsStatusUpdateRequest(
    val bookingType: String = Constants.JSON_SHUTTLE,
    val latitude: Double,
    val longitude: Double,
    val platform: String = Constants.JSON_ANDROID,
)

data class EtsEmployeeStatusUpdateRequest(
    val id: Int,
    val bookingId: Int,
    val userId: Int,
    val employeeStatus: String,
    val employeeStatusChangedBy: String,
)

data class OndcStatusUpdateRequest(
    val bookingId: String,
    val status: Int,
    val coordinates: String,
    val actualDistance: Double?,
)