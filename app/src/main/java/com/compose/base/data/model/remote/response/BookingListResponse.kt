package com.compose.base.data.model.remote.response

data class BookingListResponse(
    val bookingId: String?,
    val bookingType: String?,
    val destination: String?,
    val distance: Float?,
    val driverId: Int?,
    val duration: Int?,
    val id: Int?,
    val source: String?,
    val status: Int?,
    val travelDateAndTime: String?,
    val returnDateAndTime: String?,
    val tripType: String?,
)