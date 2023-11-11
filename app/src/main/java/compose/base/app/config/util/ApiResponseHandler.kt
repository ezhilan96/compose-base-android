package compose.base.app.config.util

import android.util.Log
import com.squareup.moshi.Moshi
import compose.base.app.config.Constants.TAG
import compose.base.app.data.model.response.ErrorResponse
import retrofit2.HttpException

suspend fun <T : Any> apiResponseHandler(
    apiCall: suspend () -> T
): NetworkResponse<T> {
    return try {
        val response = apiCall()
        Log.d(TAG, "apiResponseHandler: success")
        NetworkResponse.Success(response)
    } catch (e: HttpException) {
        val errorResponse = responseConverter(e)
        Log.d(TAG, "apiResponseHandler: HttpException $errorResponse")
        NetworkResponse.Error(
            code = errorResponse.error.statusCode,
            message = errorResponse.error.message
        )
    } catch (e: Exception) {
        Log.d(TAG, "apiResponseHandler: Exception $e")
        NetworkResponse.Exception(e)
    }
}

fun responseConverter(throwable: HttpException): ErrorResponse {
    return try {
        throwable.response()?.errorBody()?.source()?.let {
            Moshi.Builder().build().adapter(ErrorResponse::class.java).fromJson(it)
        } ?: ErrorResponse()
    } catch (e: Exception) {
        ErrorResponse()
    }
}