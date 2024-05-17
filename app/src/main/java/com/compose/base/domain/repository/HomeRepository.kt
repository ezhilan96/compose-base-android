package com.compose.base.domain.repository

import android.location.Location
import com.compose.base.data.model.remote.request.DeviceDataSubmitRequest
import com.compose.base.data.model.remote.response.AppConfigResponse
import com.compose.base.data.model.remote.response.BookingListResponse
import com.compose.base.data.model.remote.response.DirectionResponse
import com.compose.base.data.model.remote.response.ImageUploadResponse
import com.compose.base.data.model.remote.response.ListResponse
import com.compose.base.data.util.DataState
import com.compose.base.domain.entity.LatLng
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.Response

interface HomeRepository {

    fun getAppConfig(): Flow<DataState<AppConfigResponse>>

    fun updateLocation(location: Location, deviceId: String)

    fun onLocationUpdateStop()

    fun enableTripDistanceTracking(bookingId: String)

    fun getOnGoingTripDistance(bookingId: String): Double?

    fun getBookingList(
        skip: Int,
        isPendingList: Boolean = true,
    ): Flow<DataState<ListResponse<BookingListResponse>>>

    fun submitDeviceData(deviceDataSubmitRequest: DeviceDataSubmitRequest): Flow<DataState<Response<Unit>>>

    fun uploadFile(image: MultipartBody.Part): Flow<DataState<ImageUploadResponse>>

    suspend fun getDirection(
        origin: LatLng,
        destination: LatLng,
    ): Flow<DataState<DirectionResponse>>
}