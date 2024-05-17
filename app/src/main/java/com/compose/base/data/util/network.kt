package com.compose.base.data.util

import com.compose.base.R
import com.compose.base.data.model.remote.response.ErrorResponse
import com.compose.base.presentation.util.UiText
import com.compose.base.presentation.util.toUiText
import retrofit2.HttpException

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
    e.printStackTrace()
    UiText.Resource(R.string.message_default_remote)
}