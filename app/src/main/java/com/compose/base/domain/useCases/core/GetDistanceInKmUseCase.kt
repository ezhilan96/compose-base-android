package com.compose.base.domain.useCases.core

import com.compose.base.data.util.DataResponse
import com.compose.base.domain.entity.LatLng
import com.compose.base.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case class responsible for fetching the distance in kilometers between two LatLng points.
 *
 * This class injects a UserRepository dependency to access direction information. The
 * `invoke` function takes origin and destination LatLng objects and returns a Flow of DataResponse.
 * The Flow emits InProgress while fetching directions, Success with the distance in kilometers
 * upon successful retrieval, or Error if an error occurs.
 */
class GetDistanceInKmUseCase @Inject constructor(private val userRepository: UserRepository) {

    /**
     * Retrieves the distance in kilometers between two LatLng points.
     *
     * @param origin The origin LatLng object.
     * @param destination The destination LatLng object.
     * @return A Flow of DataResponse<Float> representing the distance in kilometers.
     */
    suspend operator fun invoke(
        origin: LatLng,
        destination: LatLng,
    ): Flow<DataResponse<Float>> {
        return userRepository.getDirection(origin, destination).map { dataResponse ->
            when (dataResponse) {
                is DataResponse.InProgress -> dataResponse // Pass through InProgress state

                is DataResponse.Success -> {
                    // Extract distance from first route and first leg (if available)
                    val route = dataResponse.data.routes?.firstOrNull()
                    val leg = route?.legs?.firstOrNull()
                    val distance = leg?.distance?.value.toKm()
                    DataResponse.Success(distance) // Flow emits Success with distance in km
                }

                is DataResponse.Error -> dataResponse  // Pass through Error state
            }
        }
    }
}

fun Int?.toKm(): Float = this?.div(1000.0f) ?: 0.0f