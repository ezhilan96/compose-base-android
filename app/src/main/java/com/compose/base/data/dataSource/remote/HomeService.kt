package com.compose.base.data.dataSource.remote

import com.compose.base.data.model.remote.request.DeviceDataSubmitRequest
import com.compose.base.data.model.remote.request.LiveTrackingRequest
import com.compose.base.data.model.remote.response.AppConfigResponse
import com.compose.base.data.model.remote.response.BookingListResponse
import com.compose.base.data.model.remote.response.DirectionResponse
import com.compose.base.data.model.remote.response.ImageUploadResponse
import com.compose.base.data.model.remote.response.ListResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface HomeService {

    @GET("/config")
    suspend fun getAppConfig(): AppConfigResponse

    @PATCH("/location")
    suspend fun updateLocation(@Body liveTrackingRequest: LiveTrackingRequest): Response<Unit>

    @POST("/device")
    suspend fun submitDeviceData(@Body deviceDataSubmitRequest: DeviceDataSubmitRequest): Response<Unit>

    @Multipart
    @POST("v3/auth/odometer/image/upload/booking")
    suspend fun uploadFile(@Part image: MultipartBody.Part): ImageUploadResponse

    @GET("/list")
    suspend fun getList(
        @Query(value = "skip", encoded = false) skip: Int = 0,
        @Query(value = "limit", encoded = false) limit: Int = 10,
    ): ListResponse<BookingListResponse>

    @GET("https://maps.googleapis.com/maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String,
    ): DirectionResponse

    @POST("/logout")
    suspend fun logout(): Response<Unit>
}