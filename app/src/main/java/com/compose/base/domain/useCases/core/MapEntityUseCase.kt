package com.compose.base.domain.useCases.core

import com.compose.base.presentation.util.AppTextActions
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

/**
 * Use case class responsible for converting various booking response objects to a common format.
 *
 * This class leverages helper functions from other UseCases for specific conversions
 * and relies on injected dependencies for date/time formatting and duration calculation.
 */
class MapEntityUseCase {

    private val appTextActions = AppTextActions()

    /**
     * Overloaded invoke function for converting different booking response types.
     *
     * @param response The response object to be converted.
     * @return A object representing the converted response, or null if conversion fails.
     */
    operator fun invoke(response: Any): Unit {
        try {

        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
        }
    }
}