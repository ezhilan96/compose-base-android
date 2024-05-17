package com.compose.base.data.model.remote.request

import com.compose.base.core.Constants

data class DeviceDataSubmitRequest(
    val deviceType: String = Constants.JSON_ANDROID,
    val deviceId: String? = null,
)