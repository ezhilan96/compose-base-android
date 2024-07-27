package com.compose.base.presentation.screens.shared.screen.digitalSignature

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.compose.base.domain.useCases.user.UploadFileUseCase
import com.compose.base.presentation.screens.core.ScreenViewModel
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

enum class DigitalSignatureNavItem { Done }

data class DigitalSignatureState(
    val sign: String? = null,
) : Serializable

sealed class DigitalSignatureUiEvent {
    class OnSubmit(val uri: Uri) : DigitalSignatureUiEvent()
    data object OnDismiss : DigitalSignatureUiEvent()
}

class DigitalSignatureViewModel @Inject constructor(private val uploadFile: UploadFileUseCase) :
    ScreenViewModel<DigitalSignatureState, DigitalSignatureUiEvent>(DigitalSignatureState()) {

    override fun onUiEvent(event: DigitalSignatureUiEvent) {
        when (event) {

            is DigitalSignatureUiEvent.OnSubmit -> uploadSign(event.uri)

            DigitalSignatureUiEvent.OnDismiss -> popStack()
        }
    }

    private fun uploadSign(uri: Uri) {
        viewModelScope.launch {
            uploadFile(uri).collectDataResponse { dataResponse ->
                updateScreenState { currentState ->
                    currentState.copy(sign = dataResponse.data)
                }
                pushStack(DigitalSignatureNavItem.Done)
            }
        }
    }
}