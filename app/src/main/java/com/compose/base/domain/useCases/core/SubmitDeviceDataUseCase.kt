package com.compose.base.domain.useCases.core

import com.compose.base.data.model.remote.request.DeviceDataSubmitRequest
import com.compose.base.domain.repository.HomeRepository
import javax.inject.Inject

class SubmitDeviceDataUseCase @Inject constructor(private val repo: HomeRepository) {
    operator fun invoke(token: String) =
        repo.submitDeviceData(DeviceDataSubmitRequest(deviceId = token))
}