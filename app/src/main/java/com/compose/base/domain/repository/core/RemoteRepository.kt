package com.compose.base.domain.repository.core

import com.compose.base.R
import com.compose.base.data.model.remote.response.ErrorResponse
import com.compose.base.data.util.DataState
import com.compose.base.data.util.getErrorMessage
import com.compose.base.presentation.util.UiText
import com.compose.base.presentation.util.toUiText
import com.google.gson.JsonParseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

abstract class RemoteRepository(private val connectionRepo: NetworkConnectionRepository) {

    abstract fun handleUnAuthorized()

    protected fun <T : Any> executeRemoteCall(apiCall: suspend () -> T): Flow<DataState<T>> = flow {
        emit(DataState.InProgress)
        val response = try {
            val response = apiCall()
            if (response is Response<*>) {
                if (response.isSuccessful) {
                    DataState.Success(response)
                } else {
                    val errorMessage =
                        ErrorResponse.responseConverter(response.errorBody())?.error?.message?.toUiText()
                            ?: UiText.Resource(R.string.message_default_remote)
                    DataState.Error.Remote(errorMessage)
                }
            } else {
                DataState.Success(response)
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            connectionRepo.checkConnection()
            DataState.Error.Remote()
        } catch (e: HttpException) {
            e.printStackTrace()
            if (e.code() == 401) handleUnAuthorized()
            DataState.Error.Remote(message = e.getErrorMessage())
        } catch (e: JsonParseException) {
            e.printStackTrace()
            DataState.Error.Remote(
                e.message?.toUiText() ?: UiText.Resource(R.string.message_json_mismatch)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            DataState.Error.Remote(
                e.message?.toUiText() ?: UiText.Resource(R.string.message_default_remote)
            )
        }
        emit(response)
    }
}