package com.compose.base.presentation.screens.core

import com.compose.base.presentation.util.UiText
import java.io.Serializable

interface ScreenUiState : Serializable {
    val isLoading: Boolean
    val navigationItems: List<Enum<*>>
    val alertMessage: UiText

    fun copyWith(isLoading: Boolean): ScreenUiState
}