package com.compose.base.data.model.remote.response

import com.google.gson.Gson
import okhttp3.ResponseBody

data class ErrorResponse(var error: Error?) {
    companion object {
        fun responseConverter(errorBody: ResponseBody?): ErrorResponse? {
            return try {
                errorBody?.source()?.let { bufferedSource ->
                    Gson().fromJson(bufferedSource.readUtf8(), ErrorResponse::class.java)
                        ?.let { errorResponse ->
                            if (errorResponse.error?.message.isNullOrEmpty()) null
                            else errorResponse
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

data class Error(
    var statusCode: Int?,
    var name: String?,
    var message: String?,
)