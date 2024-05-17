package com.compose.base.data.util

import com.compose.base.R
import com.compose.base.presentation.util.UiText

sealed class DataState<out T : Any?> {

    data object InProgress : DataState<Nothing>()

    class Success<out T : Any?>(val data: T) : DataState<T>()

    sealed class Error(val message: UiText) : DataState<Nothing>() {
        class Local(message: UiText = UiText.Resource(R.string.message_default_local)) :
            Error(message)

        class Remote(message: UiText = UiText.Resource(R.string.message_default_remote)) :
            Error(message)
    }
}