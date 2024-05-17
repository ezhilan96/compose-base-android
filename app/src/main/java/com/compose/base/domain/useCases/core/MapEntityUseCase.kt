package com.compose.base.domain.useCases.core

import com.compose.base.data.model.remote.response.BookingListResponse
import com.compose.base.domain.entity.DateTime
import com.compose.base.domain.entity.ListData
import com.compose.base.presentation.util.AppTextActions

class MapEntityUseCase {

    private val appTextActions = AppTextActions()

//    operator fun invoke(bookingResponse: B2cBookingDetailResponse): BookingData? {
//        return try {
//            val tripType = appTextActions.convertTripType(
//                tripType = bookingResponse.bookingType,
//                isReturn = !bookingResponse.returnTravelDateTime.isNullOrEmpty(),
//            )!!
//            val bookingStatus = bookingResponse.status?.let { status ->
//                b2cStatusDataList.first { it.second == status }.first
//            }!!
//            val dateTime =
//                bookingResponse.travelDateTime?.let { appTextActions.convertUTC(it) } ?: DateTime()
//            val returnDateTime =
//                bookingResponse.returnTravelDateTime?.let { appTextActions.convertUTC(it) }
//                    ?: DateTime()
//
//            val hours = bookingResponse.packageDuration?.let { it / 60 }
//            BookingData(
//                id = bookingResponse.id!!.toString(),
//                displayId = bookingResponse.bookingId ?: Constants.DEFAULT_STRING,
//                tripType = tripType,
//                vehicleType = bookingResponse.vehicleTypeName ?: Constants.DEFAULT_STRING,
//                vehicle = "${bookingResponse.vehicleMake} ${bookingResponse.vehicleModel}",
//                cash = bookingResponse.paymentInfo?.cash?.roundToInt()?.toString()
//                    ?: Constants.DEFAULT_STRING,
//                pickup = AddressInfo(
//                    address = bookingResponse.pickup ?: Constants.DEFAULT_STRING,
//                    location = LatLng(
//                        bookingResponse.pickupInfo?.lat ?: 0.0,
//                        bookingResponse.pickupInfo?.lng ?: 0.0
//                    ),
//                    shortAddress = bookingResponse.pickupInfo?.postcode ?: Constants.DEFAULT_STRING,
//                ),
//                drop = AddressInfo(
//                    address = bookingResponse.dropoff ?: Constants.DEFAULT_STRING,
//                    location = LatLng(
//                        bookingResponse.dropoffInfo?.lat ?: 0.0,
//                        bookingResponse.dropoffInfo?.lng ?: 0.0
//                    ),
//                    shortAddress = bookingResponse.dropoffInfo?.postcode
//                        ?: Constants.DEFAULT_STRING,
//                ),
//                viaPoints = bookingResponse.viaDetails?.map {
//                    val hour = Constants.DOUBLE_ZERO_PADDING.format((it.duration ?: 0) / 60)
//                    val minute = Constants.DOUBLE_ZERO_PADDING.format((it.duration ?: 0) % 60)
//                    val eta = UiText.Resource(R.string.body_time, hour, minute)
//                    AddressInfo(
//                        address = it.address ?: "",
//                        duration = eta,
//                    )
//                } ?: listOf(),
//                dateTime = dateTime,
//                distance = appTextActions.limitDecimalPoint(bookingResponse.distance),
//                duration = appTextActions.getDurationFromHours(hours),
//                returnDateTime = returnDateTime,
//                returnDistance = appTextActions.limitDecimalPoint(bookingResponse.returnDistance),
//                passengerCount = bookingResponse.passengerCount?.toString()
//                    ?: Constants.DEFAULT_STRING,
//                carrier = if (bookingResponse.isCarrier == true) UiText.Resource(R.string.yes)
//                else UiText.Resource(R.string.no),
//                flightNumber = bookingResponse.flightNumber ?: "",
//                customerName = (bookingResponse.customer?.name
//                    ?: Constants.DEFAULT_STRING).toUiText(),
//                customerPhone = bookingResponse.customer?.let {
//                    (it.countryCode ?: Constants.DEFAULT_STRING) + (it.phone
//                        ?: Constants.DEFAULT_STRING)
//                }?.toUiText() ?: UiText.Value(),
//                operatorName = (bookingResponse.operatorInfo?.firstName
//                    ?: Constants.DEFAULT_STRING).toUiText(),
//                operatorPhone = bookingResponse.operatorInfo?.let {
//                    (it.countryCode ?: Constants.DEFAULT_STRING) + (it.phone
//                        ?: Constants.DEFAULT_STRING)
//                }?.toUiText() ?: UiText.Value(),
//                notes = bookingResponse.notes ?: "",
//                status = bookingStatus,
//                verificationCode = bookingResponse.verificationCode,
//                bookingDocumentsData = BookingDocumentData(
//                    startOdometerImageUrl = bookingResponse.startOdometerImage ?: "",
//                    startOdometerReading = bookingResponse.meterStartDistance?.toString() ?: "",
//                    endOdometerImageUrl = bookingResponse.endOdometerImage ?: "",
//                    endOdometerReading = bookingResponse.meterEndDistance?.toString() ?: "",
//                ),
//            )
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }

    operator fun invoke(bookingListResponse: BookingListResponse): ListData? {
        return try {
            val dateTime =
                bookingListResponse.travelDateAndTime?.let { appTextActions.convertUTC(it) }
                    ?: DateTime()
            ListData(
                id = bookingListResponse.id!!,
                date = dateTime.date,
                time = dateTime.time,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}