package com.compose.base.data.repository

import android.content.Context
import android.location.Location
import com.compose.base.BuildConfig
import com.compose.base.core.di.IoDispatcher
import com.compose.base.data.dataSource.local.dataStore.UserPreferencesDataStore
import com.compose.base.data.dataSource.remote.HomeService
import com.compose.base.data.dataSource.remote.SocketService
import com.compose.base.data.model.remote.request.DeviceDataSubmitRequest
import com.compose.base.data.model.remote.request.LiveTrackingRequest
import com.compose.base.data.model.remote.request.LocationSocketMessage
import com.compose.base.data.model.remote.response.AppConfigResponse
import com.compose.base.data.model.remote.response.BookingListResponse
import com.compose.base.data.model.remote.response.DirectionResponse
import com.compose.base.data.model.remote.response.ForceUpdateConfig
import com.compose.base.data.model.remote.response.ImageUploadResponse
import com.compose.base.data.model.remote.response.ListResponse
import com.compose.base.data.model.remote.response.SiteSettings
import com.compose.base.data.util.DataState
import com.compose.base.domain.entity.AppConfigData
import com.compose.base.domain.entity.LatLng
import com.compose.base.domain.repository.AuthRepository
import com.compose.base.domain.repository.HomeRepository
import com.compose.base.domain.repository.core.NetworkConnectionRepository
import com.compose.base.domain.repository.core.RemoteRepository
import com.compose.base.presentation.services.LocationTrackingService
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    connectionRepo: NetworkConnectionRepository,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val authRepo: AuthRepository,
    private val homeService: HomeService,
    private val socketService: SocketService,
    private val dataStore: UserPreferencesDataStore,
) : HomeRepository, RemoteRepository(connectionRepo) {

    private var onTripBookingId: String? = null
    private var enableTripDistanceTracking: Boolean = false
    private lateinit var tripLocations: MutableList<Location>

    override fun handleUnAuthorized() {
        authRepo.logout().launchIn(CoroutineScope(ioDispatcher))
    }

    override fun updateLocation(location: Location, deviceId: String) {
        val data: String = Gson().toJson(
            LocationSocketMessage(
                latitude = location.latitude,
                longitude = location.longitude,
                direction = location.bearing,
            )
        )
        socketService.sendMessage(JSONObject(data))
        CoroutineScope(ioDispatcher).launch {
            executeRemoteCall {
                homeService.updateLocation(
                    LiveTrackingRequest(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        bearingAngle = location.bearing,
                        deviceId = deviceId,
                    )
                )
            }.collect()
        }
        if (enableTripDistanceTracking) {
            tripLocations.add(location)
        }
    }

    override fun onLocationUpdateStop() {
        socketService.disconnect()
    }

    override fun enableTripDistanceTracking(bookingId: String) {
        LocationTrackingService.updateLocationUpdateFrequency(context, true)
        onTripBookingId = bookingId
        tripLocations = mutableListOf()
        enableTripDistanceTracking = true
    }

    override fun getOnGoingTripDistance(bookingId: String): Double {
        enableTripDistanceTracking = false
        var distance = 0.0
        if (bookingId == onTripBookingId) {
            var lastLocation: Location? = null
            tripLocations.filter { it.hasAccuracy() && it.accuracy < 25 }
                .filter { it.hasSpeed() && it.speed > 0.1f }.forEach { location ->
                    distance += lastLocation?.distanceTo(location) ?: 0f
                    lastLocation = location
                }
        }
        LocationTrackingService.updateLocationUpdateFrequency(context, false)
        return distance / 1000.0
    }

    override fun getBookingList(
        skip: Int, isPendingList: Boolean,
    ): Flow<DataState<ListResponse<BookingListResponse>>> = executeRemoteCall {
        homeService.getList(skip)
    }

    override fun submitDeviceData(deviceDataSubmitRequest: DeviceDataSubmitRequest): Flow<DataState<Response<Unit>>> =
        executeRemoteCall {
            homeService.submitDeviceData(deviceDataSubmitRequest)
        }

    override fun uploadFile(image: MultipartBody.Part): Flow<DataState<ImageUploadResponse>> =
        executeRemoteCall {
            homeService.uploadFile(image)
        }

    override fun getAppConfig(): Flow<DataState<AppConfigResponse>> = executeRemoteCall {
        homeService.getAppConfig()
    }
        .map {
            DataState.Success(
                AppConfigResponse(
                    SiteSettings = listOf(
                        SiteSettings(
                            id = 0,
                            tollKey = "key",
                            googleKey = "key"
                        )
                    ),
                    KillSwitch = listOf(
                        ForceUpdateConfig(
                            versionCode = "1",
                            isForceUpdate = false,
                            isPartialUpdate = false,
                            `package` = BuildConfig.APPLICATION_ID,
                            block = false,
                            buildNumber = "1.0.0",
                        )
                    )
                )
            )
        }
        .onEach { appConfigResponse ->
            if (appConfigResponse is DataState.Success<AppConfigResponse>) {
                val googleApiKey = appConfigResponse.data.SiteSettings?.first()?.googleKey
                val appUpdateData = appConfigResponse.data.KillSwitch?.first()
                val versionCode = appUpdateData?.versionCode?.toIntOrNull()
                val isForceUpdate = appUpdateData?.isForceUpdate ?: false
                val isPartialUpdate = appUpdateData?.isPartialUpdate ?: false
                if (!googleApiKey.isNullOrEmpty()) {
                    dataStore.putAppConfig(
                        AppConfigData(
                            googleApiKey = googleApiKey,
                            versionCode = versionCode,
                            isForceUpdate = isForceUpdate,
                            isPartialUpdate = isPartialUpdate,
                        )
                    )
                }
            }
        }

    override suspend fun getDirection(
        origin: LatLng, destination: LatLng
    ): Flow<DataState<DirectionResponse>> {
        val googleApiKey = dataStore.appConfig.first().googleApiKey
        return executeRemoteCall {
            homeService.getDirections(
                origin = "${origin.lat},${origin.lng}",
                destination = "${destination.lat},${destination.lng}",
                apiKey = googleApiKey,
            )
        }
    }
}