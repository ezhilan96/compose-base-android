package com.compose.base.presentation.screens.core

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.compose.base.R
import com.compose.base.data.util.DataResponse
import com.compose.base.presentation.util.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.Serializable

/**
 * Base class for ViewModels that manage UI state and navigation for a specific screen.
 *
 * This abstract class provides common functionalities for handling UI events,
 * updating screen state, and interacting with the UI layer. It leverages SavedStateHandle
 * to persist the screen state across configuration changes.
 *
 * @param initialState The initial state of the screen.
 * @param savedStateHandle An optional SavedStateHandle for persisting state.
 */
abstract class ScreenViewModel<state : Serializable, event>(
    private val initialState: state,
    private val savedStateHandle: SavedStateHandle? = null,
) : ViewModel() {

    private val savedStateKey: String = this::class.java.simpleName

    private val _uiState: MutableStateFlow<ScreenUiState<state>> by lazy {
        MutableStateFlow(
            savedStateHandle?.get(savedStateKey) ?: ScreenUiState(screenState = initialState)
        )
    }
    val uiState: StateFlow<ScreenUiState<state>> by lazy { _uiState }

    /**
     * Accesses the current screen state from the UI state.
     */
    val screenState: state
        get() = uiState.value.screenState

    /**
     * Same as [MutableStateFlow.update]. But in addition, it persists the updated state using SavedStateHandle.
     *
     * @param function A lambda function that takes the current state and returns the updated state.
     */
    private fun updateUiState(function: (ScreenUiState<state>) -> ScreenUiState<state>) {
        _uiState.update(function)
        savedStateHandle?.set(savedStateKey, _uiState.value)
    }

    /**
     * Collects the response from a Flow of DataResponse and performs actions based on the response type.
     *
     * This function simplifies handling of success, error, and in-progress states from data flows.
     *  - On success: Calls the provided `onSuccess` function with the data.
     *  - On error:
     *      - If the error is a local error (Input validation error), updates the UI state with the error message.
     *      - Otherwise, shows an alert dialog with the error message.
     *  - On in-progress: Updates the loader visibility (implementation left to the subclass).
     *
     * This method can be used like [Flow.collect]:
     * ```
     * getList().collectDataResponse { successResponse ->
     *      // Handle success
     * }
     *
     * resendOtp().collectDataResponse (
     *      onFailure = { error ->
     *          // Handle error
     *      },
     *      onSuccess = { data ->
     *          // Handle success
     *      },
     * )
     * ```
     *
     * @param onFailure An optional callback function to handle data errors.
     * @param onSuccess A callback function to handle successful data fetching.
     */
    protected suspend fun <T> Flow<DataResponse<T>>.collectDataResponse(
        onFailure: ((DataResponse.Error) -> Unit)? = null,
        onSuccess: (DataResponse.Success<T>) -> Unit = {},
    ) = collect { dataResponse ->
        when (dataResponse) {
            is DataResponse.InProgress -> setLoader(true)

            is DataResponse.Success -> {
                setLoader(false)
                onSuccess(dataResponse)
            }

            is DataResponse.Error -> {
                setLoader(false)
                onFailure?.invoke(dataResponse) ?: showAlert(dataResponse.message)
            }
        }
    }

    /**
     * Updates the screen state using a function that transforms the current state.
     *
     * This function updates the screen state portion of the UI state.
     *
     * @param function A lambda function that takes the current screen state and returns the updated state.
     */
    protected fun updateScreenState(function: (state) -> state) {
        updateUiState { currentState ->
            currentState.copy(
                screenState = function(currentState.screenState)
            )
        }
    }

    /**
     * Updates the loader visibility in the UI state.
     *
     * This function is typically used to show or hide a progress indicator while data is being fetched.
     * Subclasses should implement the logic to update the UI based on the `isLoading` value.
     *
     * @param isLoading A boolean indicating whether to show the loader.
     */
    protected fun setLoader(isLoading: Boolean) {
        updateUiState { currentState ->
            currentState.copy(
                isLoading = isLoading
            )
        }
    }

    /**
     * Pushes a new navigation item onto the navigation stack in the UI state.
     *
     * This function updates the screen stack portion of the UI state to indicate a navigation
     * to a new screen represented by the provided `navItem` (an Enum). Subclasses typically use
     * enums specific to their navigation flow for `navItem`.
     *
     * @param navItem An enum representing the navigation item to be pushed onto the stack.
     */
    protected fun pushStack(navItem: Enum<*>) {
        updateUiState { currentState ->
            currentState.copy(
                screenStack = currentState.screenStack.toMutableList().apply {
                    add(navItem)
                },
            )
        }
    }

    /**
     * Pops the topmost navigation item from the navigation stack in the UI state.
     *
     * This function removes the top item from the screen stack in the UI state. Optionally, you
     * can provide a specific `navItem` to pop. If provided, it will only pop the stack if the
     * `navItem` exists on the stack. Otherwise, it removes the last item (top of the stack).
     *
     * @param navItem An optional enum representing the specific navigation item to pop (if exists).
     */
    protected fun popStack(navItem: Enum<*>? = null) {
        updateUiState { currentState ->
            currentState.copy(
                screenStack = currentState.screenStack.toMutableList().apply {
                    if (navItem != null && contains(navItem)) {
                        remove(navItem)
                    } else if (isNotEmpty()) {
                        removeLast()
                    }
                },
            )
        }
    }

    /**
     * Clears the entire navigation stack in the UI state.
     *
     * This function removes all items from the screen stack in the UI state, effectively
     * resetting the navigation history for the screen.
     */
    protected fun clearStack() {
        updateUiState { currentState ->
            currentState.copy(
                screenStack = currentState.screenStack.toMutableList().apply {
                    clear()
                },
            )
        }
    }

    /**
     * Shows an alert dialog with the provided message.
     *
     * This function updates the UI state to display an alert dialog with the given `message`.
     * It also pushes the `ScreenNavItem.ALERT_DIALOG` onto the navigation stack to indicate
     * the alert dialog is being shown. Subclasses likely handle the actual presentation of the
     * alert dialog based on the UI framework.
     *
     * @param message The message to be displayed in the alert dialog (defaults to a generic message).
     */
    protected fun showAlert(message: UiText = UiText.Resource(R.string.message_default_remote)) {
        updateUiState { currentState ->
            currentState.copy(
                alertMessage = message,
                screenStack = currentState.screenStack.toMutableList().apply {
                    add(ScreenNavItem.ALERT_DIALOG)
                },
                isLoading = false,
            )
        }
    }

    /**
     * Abstract method to be implemented by subclasses to handle UI events specific to the screen.
     *
     * This method takes a UI event of type `event` (specific to the subclass) and performs
     * the necessary actions to update the UI state and interact with the data layer. Subclasses
     * should define their own event types and handle them appropriately in this method.
     *
     * @param event A UI event specific to the screen.
     */
    abstract fun onUiEvent(event: event)

    /**
     * Called when the composable lifecycle reaches the `ON_STOP` state.
     *
     * This method is a good place to perform cleanup tasks when the screen is no longer visible
     * to the user. The base implementation clears the navigation stack and hides any loaders.
     * Subclasses can override this method to perform additional cleanup or state management
     * specific to their needs.
     */
    open fun onStop() {
        clearStack()
        setLoader(false)
    }
}

/**
 * Enum representing the navigation items within a screen.
 *
 * This enum likely defines navigation options specific to the screen managed by the ScreenViewModel.
 * By convention, it uses the `ALERT_DIALOG` constant to indicate showing an alert dialog. Subclasses
 * can add more navigation items that are common for all screens as needed.
 */
enum class ScreenNavItem { ALERT_DIALOG }

/**
 * Data class representing the UI state for a screen.
 *
 * This class holds various properties that represent the current UI state of the screen.
 * - `isLoading`: A boolean indicating whether a loader or progress indicator should be shown.
 * - `screenStack`: A list of enums representing the navigation history within the screen
(likely uses enums from ScreenNavItem).
 * - `alertMessage`: A UiText instance representing the message to be displayed in an alert dialog
(if any).
 * - `screenState`: The state object specific to the screen managed by the ScreenViewModel.
 *
 * This class is serializable to allow persisting the UI state across configuration changes
 * using SavedStateHandle.
 */
open class ScreenUiState<state>(
    val isLoading: Boolean = false,
    val screenStack: List<Enum<*>> = listOf(),
    val alertMessage: UiText = UiText.Value(),
    val screenState: state,
) : Serializable {

    /**
     * Creates a copy of the ScreenUiState with modified properties.(Similar to [data class]::copy())
     *
     * This function allows updating specific parts of the UI state while keeping others unchanged.
     * It provides default values for all properties, so you only need to specify the ones you want to modify.
     *
     * @param isLoading An optional boolean to update the loading state.
     * @param screenStack An optional list of enums to update the navigation stack.
     * @param alertMessage An optional UiText to update the alert message.
     * @param screenState An optional state object to update the screen state.
     *
     * @return A new ScreenUiState object with the updated properties.
     */
    fun copy(
        isLoading: Boolean = this.isLoading,
        screenStack: List<Enum<*>> = this.screenStack,
        alertMessage: UiText = this.alertMessage,
        screenState: state = this.screenState,
    ): ScreenUiState<state> = ScreenUiState(
        isLoading = isLoading,
        screenStack = screenStack,
        alertMessage = alertMessage,
        screenState = screenState,
    )
}