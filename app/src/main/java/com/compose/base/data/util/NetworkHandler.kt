package com.compose.base.data.util

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonParseException
import com.compose.base.R
import com.compose.base.data.dataSource.remote.UserService
import com.compose.base.data.model.remote.response.ErrorResponse
import com.compose.base.domain.repository.core.NetworkConnectionRepository
import com.compose.base.domain.repository.core.PreferencesRepository
import com.compose.base.presentation.util.UiText
import com.compose.base.presentation.util.toUiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton class responsible for handling network requests and responses.
 *
 * This class acts as a centralized point for network interactions within the application.
 * It injects dependencies for various services like `DriverService`,
 * `NetworkConnectionRepository`, and `PreferencesRepository` to handle different aspects
 * of network operations.
 */
@Singleton
class NetworkHandler @Inject constructor(
    private val userService: UserService,
    private val networkConnectionRepository: NetworkConnectionRepository,
    private val preferencesRepository: PreferencesRepository,
) {

    /**
     * Operator function that simplifies invoking network requests.
     *
     * This function takes a suspending lambda that represents the actual network request
     * to be executed. It returns a Flow of `DataResponse<T>` which emits different states
     * based on the network operation:
     *
     *  * `DataResponse.InProgress`: Emitted initially to indicate the request is ongoing.
     *  * `DataResponse.Success<T>`: Emitted upon successful response containing the
     *      requested data (T).
     *  * `DataResponse.Error`: Emitted in case of any errors during the request.
     *      - `Local`: Represents a local error (e.g., network connectivity issues).
     *      - `Remote`: Represents a remote error (e.g., server error, invalid data).
     *
     * @param apiCall: A suspending lambda that encapsulates the actual network request.
     * @return: A Flow of `DataResponse<T>`.
     */
    operator fun <T : Any> invoke(apiCall: suspend () -> T): Flow<DataResponse<T>> = flow {
        emit(DataResponse.InProgress)
        val response = try {
            val response = apiCall()
            // Handle Response<Unit> (e.g., @PATCH response)
            if (response is Response<*>) {
                if (response.isSuccessful) {
                    DataResponse.Success(response)
                } else {
                    val errorMessage =
                        ErrorResponse.responseConverter(response.errorBody())?.error?.message?.toUiText()
                            ?: UiText.Resource(R.string.message_default_remote)
                    DataResponse.Error.Remote(errorMessage)
                }
            } else {
                DataResponse.Success(response)
            }
        } catch (e: UnknownHostException) {
            Firebase.crashlytics.recordException(e)
            networkConnectionRepository.checkConnection()
            DataResponse.Error.Remote()
        } catch (e: HttpException) {
            Firebase.crashlytics.recordException(e)
            // Logs out user if token is expired or 401 Unauthorized
            if (e.code() == 401) {
                val response: DataResponse<Response<Unit>> =
                    invoke { userService.logout() }.filter { it !is DataResponse.InProgress }
                        .first()
                if (response is DataResponse.Success) {
                    preferencesRepository.logout()
                }
            }
            DataResponse.Error.Remote(message = e.getErrorMessage())
        } catch (e: JsonParseException) {
            Firebase.crashlytics.recordException(e)
            DataResponse.Error.Remote(
                e.message?.toUiText() ?: UiText.Resource(R.string.message_json_mismatch)
            )
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
            DataResponse.Error.Remote(
                e.message?.toUiText() ?: UiText.Resource(R.string.message_default_remote)
            )
        }
        emit(response)
    }
}

/**
 * Extension function on HttpException to retrieve a user readable error message.
 *
 * This function attempts to extract an error message from an HttpException object
 * and return it as a UiText object suitable for user display.
 *
 * It follows these steps:
 *
 * 1. Checks if the HTTP status code belongs to the 5xx category (server errors).
 * If so, it returns a pre-defined `UiText` message indicating a server issue.
 * 2. Attempts to convert the error body of the HttpException to an ErrorResponse object
 * using a potential `ErrorResponse.responseConverter` function (assumed to exist elsewhere).
 * If successful and the error response contains a non-empty message, it returns
 * that message converted to a UiText object.
 * 3. If conversion fails or the error message is missing, it returns a default
 * `UiText` message indicating a generic remote error.
 * 4. Catches any exceptions during the process and logs them using Firebase Crashlytics.
 * In such cases, it also returns the default `UiText` message.
 *
 * @receiver: The [HttpException] object from which to extract the error message.
 * @return: A `UiText` object containing the user readable error message.
 */
fun HttpException.getErrorMessage(): UiText = if (code() / 100 == 5) {
    UiText.Resource(R.string.message_server_issue)
} else try {
    val errorResponse = ErrorResponse.responseConverter(response()?.errorBody())
    if (!errorResponse?.error?.message.isNullOrEmpty()) {
        errorResponse?.error?.message!!.toUiText()
    } else {
        UiText.Resource(R.string.message_default_remote)
    }
} catch (e: Exception) {
    Firebase.crashlytics.recordException(e)
    UiText.Resource(R.string.message_default_remote)
}