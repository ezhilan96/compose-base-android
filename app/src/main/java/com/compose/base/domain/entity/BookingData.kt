package com.compose.base.domain.entity

import com.compose.base.core.Constants
import com.compose.base.core.Constants.DEFAULT_STRING
import com.compose.base.presentation.util.UiText
import java.util.Calendar

enum class BusinessType { corporate, tmc, aggregator, vendor }

data class BookingData(
    val id: String,
    val displayId: String = DEFAULT_STRING,
    val tripType: UiText = UiText.Value(),
    val businessType: BusinessType = BusinessType.corporate,
    val status: UiText = UiText.Value(),
    val cash: String = DEFAULT_STRING,
    val fare: String = DEFAULT_STRING,

    val pickup: AddressInfo = AddressInfo(),
    val drop: AddressInfo = AddressInfo(),
    val viaPoints: List<AddressInfo> = listOf(),

    val dateTime: DateTime = DateTime(),
    val returnDateTime: DateTime = DateTime(),
    val distance: String = DEFAULT_STRING,
    val returnDistance: String = DEFAULT_STRING,
    val duration: UiText? = UiText.Value(),

    val vehicle: String = DEFAULT_STRING,
    val vehicleType: String = DEFAULT_STRING,
    val passengerCount: String = DEFAULT_STRING,
    val luggageCount: String = DEFAULT_STRING,
    val carrier: UiText = UiText.Value(),
    val flightNumber: String = DEFAULT_STRING,
    val notes: String = DEFAULT_STRING,

    val customerName: UiText = UiText.Value(),
    val customerPhone: UiText = UiText.Value(),
    val operatorName: UiText = UiText.Value(),
    val operatorPhone: UiText = UiText.Value(),
    val nightCharge: AdditionalCharge = AdditionalCharge(),
    val baseFare: AdditionalCharge = AdditionalCharge(),
    val verificationCode: String? = DEFAULT_STRING,
    val isReturnToOfficeEnabled: Boolean = false,
    val isEmployeeOnBoard: Boolean = false,
)

data class AdditionalCharge(
    val label: UiText = UiText.Value(),
    val amount: String = DEFAULT_STRING,
)

data class AddressInfo(
    val address: String = DEFAULT_STRING,
    val shortAddress: String = DEFAULT_STRING,
    val location: LatLng? = null,
    val duration: UiText? = UiText.Value(),
)

data class LatLng(
    val lat: Double? = Constants.DEFAULT_DOUBLE,
    val lng: Double? = Constants.DEFAULT_DOUBLE,
)

data class DateTime(
    var date: String = DEFAULT_STRING,
    var time: String = DEFAULT_STRING,
    var calendar: Calendar = Calendar.getInstance(),
)
