package com.compose.base.data.dataSource.remote

import com.compose.base.data.model.remote.request.DeviceDataSubmitRequest
import com.compose.base.data.model.remote.response.DirectionResponse
import com.compose.base.data.model.remote.response.ImageUploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/**
 * Interface for user-related services.
 */
interface UserService {

    /**
     * Submits device data (FCM token for now) associated with the driver's device.
     *
     * @param deviceDataSubmitRequest: An object containing details about the driver's device.
     * @return: A [Response] indicating the success or failure of the update.
     */
    @POST("/v1/devices")
    suspend fun submitDeviceData(@Body deviceDataSubmitRequest: DeviceDataSubmitRequest): Response<Unit>

    /**
     * Uploads an image file
     *
     * @param image: A MultipartBody.Part containing the image data.
     * @return: An [ImageUploadResponse] object containing information about the uploaded image or an error response.
     */
    @Multipart
    @POST("v3/image/upload")
    suspend fun uploadFile(@Part image: MultipartBody.Part): ImageUploadResponse

    /**
     * Retrieves directions between two locations using Google Maps API.
     *
     * @param origin: The starting location (e.g., address or coordinates).
     * @param destination: The ending location (e.g., address or coordinates).
     * @param apiKey: The Google Maps API key for authentication.
     * @return: A [DirectionResponse] object containing navigation directions or an error response.
     */
    @GET("https://maps.googleapis.com/maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String,
    ): DirectionResponse

    /**
     * Logs out the user.
     *
     * @return: A [Response] indicating the success or failure of the request.
     */
    @POST("/v3/logout")
    suspend fun logout(): Response<Unit>
}