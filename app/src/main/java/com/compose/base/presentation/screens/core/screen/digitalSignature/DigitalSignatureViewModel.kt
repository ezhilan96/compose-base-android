package com.compose.base.presentation.screens.core.screen.digitalSignature

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.compose.base.domain.useCases.home.UploadFileUseCase
import com.compose.base.presentation.screens.core.ScreenUiState
import com.compose.base.presentation.screens.core.ScreenViewModel
import com.compose.base.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class DigitalSignatureNavItem { AlertDialog, Done }

data class DigitalSignatureUiState(
    override val isLoading: Boolean = false,
    override val navigationItems: List<DigitalSignatureNavItem> = listOf(),
    val signUrl: String? = null,
    override val alertMessage: UiText = UiText.Value(),
) : ScreenUiState {
    override fun copyWith(isLoading: Boolean) = copy(isLoading = isLoading)
}

sealed class DigitalSignatureUiEvent {
    class OnSubmit(val uri: Uri) : DigitalSignatureUiEvent()
    class OnDismiss(val dismissAll: Boolean = false) : DigitalSignatureUiEvent()
}

@HiltViewModel
class DigitalSignatureViewModel @Inject constructor(private val uploadFile: UploadFileUseCase) :
    ScreenViewModel<DigitalSignatureUiState, DigitalSignatureUiEvent>(DigitalSignatureUiState()) {

    override fun onUiEvent(event: DigitalSignatureUiEvent) {
        when (event) {

            is DigitalSignatureUiEvent.OnSubmit -> uploadSign(event.uri)

            is DigitalSignatureUiEvent.OnDismiss -> updateUiState { currentState ->
                currentState.copy(
                    navigationItems = currentState.navigationItems.toMutableList()
                        .apply {
                            if (event.dismissAll) clear()
                            else if (isNotEmpty()) removeLast()
                        })
            }
        }
    }

    private fun uploadSign(uri: Uri) {
        viewModelScope.launch {
            uploadFile(uri).collectDataState { dataState ->
                updateUiState { currentState ->
                    currentState.copy(
                        signUrl = dataState.data,
                        navigationItems = currentState.navigationItems.toMutableList().apply {
                            add(DigitalSignatureNavItem.Done)
                        },
                    )
                }
            }
        }
    }

    override fun showAlert(message: UiText) {
        updateUiState { currentState ->
            currentState.copy(
                navigationItems = currentState.navigationItems.toMutableList()
                    .apply { add(DigitalSignatureNavItem.AlertDialog) },
                alertMessage = message,
            )
        }
    }

    override fun onStop() {
        updateUiState { currentState ->
            currentState.copy(navigationItems = mutableListOf())
        }
    }
}