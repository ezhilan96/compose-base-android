package com.compose.base.data.model.remote.response

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
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
                Firebase.crashlytics.recordException(e)
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