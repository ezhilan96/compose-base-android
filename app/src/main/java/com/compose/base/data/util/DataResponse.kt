package com.compose.base.data.util

import com.compose.base.R
import com.compose.base.presentation.util.UiText

/**
 * Sealed class representing the possible response states from a request.
 *
 * This class provides a structured way to handle different response outcomes,
 * including success with data, in-progress state, and various error conditions.
 */
sealed class DataResponse<out T : Any?> {

    /**
     * Represents an ongoing request that has not yet completed.
     */
    data object InProgress : DataResponse<Nothing>()

    /**
     * Represents a successful response with the requested data.
     *
     * @param data: The actual data object retrieved from the request.
     */
    data class Success<out T : Any?>(val data: T) : DataResponse<T>()

    /**
     * Sealed class representing different error conditions during a data request.
     *
     * This class provides a way to differentiate between errors originating locally
     * (e.g., parsing issues) and those originating remotely (e.g., server errors).
     */
    sealed class Error(val message: UiText) : DataResponse<Nothing>() {

        /**
         * Represents a local error condition that occurred on the device.
         *
         * @param message: A UiText object containing a user readable message describing the error.
         * Defaults to a generic "local error" message.
         */
        class Local(message: UiText = UiText.Resource(R.string.message_default_local)) :
            Error(message)

        /**
         * Represents a remote error condition originating from the server.
         *
         * @param message: A UiText object containing a user readable message describing the error.
         * Defaults to a generic "remote error" message.
         */
        class Remote(message: UiText = UiText.Resource(R.string.message_default_remote)) :
            Error(message)
    }
}