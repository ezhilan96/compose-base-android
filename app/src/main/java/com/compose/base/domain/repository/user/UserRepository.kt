package com.compose.base.domain.repository.user

import com.compose.base.data.model.remote.request.DeviceDataSubmitRequest
import com.compose.base.data.model.remote.response.DirectionResponse
import com.compose.base.data.model.remote.response.ImageUploadResponse
import com.compose.base.data.util.DataResponse
import com.compose.base.domain.entity.LatLng
import com.compose.base.domain.entity.UserData
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.Response

/**
 * Interface for user-related operations.
 */
interface UserRepository {

    /**
     * Flow of booleans indicating the user's login status.
     * Emits updates whenever the login state changes.
     */
    val loginStatusFlow: Flow<Boolean>

    /**
     * Flow of nullable `UserData` objects representing the user's information.
     * Emits updates whenever the user data changes or becomes available.
     */
    val userDetailFlow: Flow<UserData?>

    /**
     * Submits device data (FCM token for now) to the server.
     *
     * @param deviceDataSubmitRequest: An object containing the device data to be submitted.
     * @return: A Flow of `DataResponse<Response<Unit>>>`.
     *      - Success: Emits a successful response if the submission was successful.
     *      - Error: Emits a `DataResponse.Error` indicating the submission failure reason.
     */
    fun submitDeviceData(deviceDataSubmitRequest: DeviceDataSubmitRequest): Flow<DataResponse<Response<Unit>>>

    /**
     * Uploads an image file to the server.
     *
     * @param image: A MultipartBody.Part representing the image data.
     * @return: A Flow of `DataResponse<ImageUploadResponse>>`.
     *      - Success: Emits an `[ImageUploadResponse]` object containing details about the uploaded image.
     *      - Error: Emits a `DataResponse.Error` indicating the image upload failure reason.
     */
    fun uploadFile(image: MultipartBody.Part): Flow<DataResponse<ImageUploadResponse>>

    /**
     * Retrieves directions for a route (likely uses a mapping service).
     *
     * @param origin: The origin location (LatLng object).
     * @param destination: The destination location (LatLng object).
     * @return: A Flow of `DataResponse<DirectionResponse>>`.
     */
    suspend fun getDirection(
        origin: LatLng,
        destination: LatLng,
    ): Flow<DataResponse<DirectionResponse>>
}