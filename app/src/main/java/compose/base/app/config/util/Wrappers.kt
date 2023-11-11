package compose.base.app.config.util

import compose.base.app.config.Constants.DEFAULT_INT

sealed class NetworkResponse<T : Any> {
    class Success<T : Any>(val responseData: T) : NetworkResponse<T>()
    class Error<T : Any>(
        val code: Int = DEFAULT_INT,
        val message: String = "Something went wrong!"
    ) : NetworkResponse<T>()
    class Exception<T : Any>(val e: Throwable) : NetworkResponse<T>()
}