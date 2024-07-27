package com.compose.base.data.repository.user

import com.compose.base.data.dataSource.remote.UserService
import com.compose.base.data.model.remote.request.DeviceDataSubmitRequest
import com.compose.base.data.model.remote.response.DirectionResponse
import com.compose.base.data.model.remote.response.ImageUploadResponse
import com.compose.base.data.util.DataResponse
import com.compose.base.data.util.NetworkHandler
import com.compose.base.domain.entity.LatLng
import com.compose.base.domain.entity.UserData
import com.compose.base.domain.repository.core.PreferencesRepository
import com.compose.base.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val executeApiRequest: NetworkHandler,
    private val preferencesRepository: PreferencesRepository,
    private val userService: UserService,
) : UserRepository {

    override val loginStatusFlow: Flow<Boolean>
        get() = preferencesRepository.loginStatusFlow

    override val userDetailFlow: Flow<UserData?> = preferencesRepository.userDataFlow

    override fun submitDeviceData(deviceDataSubmitRequest: DeviceDataSubmitRequest): Flow<DataResponse<Response<Unit>>> =
        executeApiRequest {
            userService.submitDeviceData(deviceDataSubmitRequest)
        }

    override fun uploadFile(image: MultipartBody.Part): Flow<DataResponse<ImageUploadResponse>> =
        executeApiRequest {
            userService.uploadFile(image)
        }

    override suspend fun getDirection(
        origin: LatLng, destination: LatLng
    ): Flow<DataResponse<DirectionResponse>> {
        val googleApiKey = preferencesRepository.appConfigFlow.first().googleApiKey
        return executeApiRequest {
            userService.getDirections(
                origin = "${origin.lat},${origin.lng}",
                destination = "${destination.lat},${destination.lng}",
                apiKey = googleApiKey,
            )
        }
    }
}