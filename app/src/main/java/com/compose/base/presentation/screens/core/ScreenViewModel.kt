package com.compose.base.presentation.screens.core

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.compose.base.R
import com.compose.base.data.util.DataState
import com.compose.base.presentation.util.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Suppress("UNCHECKED_CAST")
abstract class ScreenViewModel<state : ScreenUiState, event>(
    private val initUiState: state,
    private val savedStateHandle: SavedStateHandle? = null,
) : ViewModel() {

    private val savedStateKey: String = this::class.java.simpleName

    private val _uiState: MutableStateFlow<state> by lazy {
        MutableStateFlow(savedStateHandle?.get(savedStateKey) ?: initUiState)
    }
    val uiState: StateFlow<state> by lazy { _uiState }

    protected suspend fun <T> Flow<DataState<T>>.collectDataState(
        onFailure: ((DataState.Error) -> Unit)? = null,
        onSuccess: (DataState.Success<T>) -> Unit = {},
    ) = collect { dataState ->
        when (dataState) {
            is DataState.InProgress -> {
                _uiState.update { it.copyWith(true) as state }
            }

            is DataState.Success -> {
                _uiState.update { it.copyWith(false) as state }
                onSuccess(dataState)
            }

            is DataState.Error -> {
                _uiState.update { it.copyWith(false) as state }
                onFailure?.invoke(dataState) ?: showAlert(dataState.message)
            }
        }
    }

    protected fun updateUiState(function: (state) -> state) {
        _uiState.update(function)
        savedStateHandle?.set(savedStateKey, _uiState.value)
    }

    protected abstract fun showAlert(message: UiText = UiText.Resource(R.string.message_default_remote))

    abstract fun onUiEvent(event: event)

    abstract fun onStop()
}