package com.compose.base.presentation.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.compose.base.core.Constants
import com.compose.base.core.Constants.DEFAULT_STRING
import java.io.Serializable

/**
 * Sealed class representing different types of UI text content.
 *
 * This sealed class provides two subclasses for representing UI text content:
 *  - `Value`: Represents a plain text string.
 *  - `Resource`: Represents a reference to a string resource in the application resources.
 *
 * This approach allows for centralized management of UI text strings and the ability to
 * handle different resource types (e.g., strings, plurals) based on the resource ID.
 *
 * UiText implements Serializable for potential use in Parcelable or other serialization scenarios.
 */
sealed class UiText : Serializable {

    /**
     * Represents a plain text string value.
     *
     * @param value The text string content (default: empty string).
     */
    class Value(val value: String = DEFAULT_STRING) : UiText()

    /**
     * Represents a reference to a string resource in the application resources.
     *
     * @param id The resource ID of the string resource.
     * @param args Optional arguments to be used for formatting the string resource
     *             (e.g., for plurals).
     */
    class Resource(
        val id: Int,
        vararg val args: Any?,
    ) : UiText()

    /**
     * Converts a UiText object to a String representation within a composable function.
     *
     * This function checks the type of UiText and returns the appropriate string value:
     *  - For `Value`: Returns the stored string value directly.
     *  - For `Resource`:
     *      - Retrieves the resource type using LocalContext.current.resources.getResourceTypeName.
    - Based on the resource type:
    - `Constants.KEY_STRING`: Uses stringResource with the resource ID and arguments.
    - `Constants.KEY_PLURALS`: Uses pluralStringResource with the resource ID, quantity,
    and arguments.
    - Other resource types: Returns an empty string.
     *
     * @see LocalContext
     * @see stringResource
     * @see pluralStringResource
     */
    @Composable
    fun asString(): String {
        return when (this) {
            is Value -> value
            is Resource -> {
                when (LocalContext.current.resources.getResourceTypeName(id)) {
                    Constants.KEY_STRING -> stringResource(
                        id,
                        *args.map { it ?: "" }.toTypedArray(),
                    )

                    Constants.KEY_PLURALS -> pluralStringResource(
                        id, args.first() as Int,
                        *args.map { it ?: "" }.toTypedArray(),
                    )

                    else -> ""
                }
            }
        }
    }

    /**
     * Converts a UiText object to a String representation using the provided context.
     *
     * This function is similar to the `asString` composable function, but it takes a context
     * argument instead of relying on `LocalContext`. It checks the type of UiText and returns
     * the appropriate string value:
     *  - For `Value`: Returns the stored string value directly.
     *  - For `Resource`:
     *      - Retrieves the resource type using context.resources.getResourceTypeName.
    - Based on the resource type:
    - `Constants.KEY_STRING`: Uses context.getString with the resource ID and arguments.
    - `Constants.KEY_PLURALS`: Uses context.resources.getQuantityString with the resource ID,
    quantity, and arguments.
    - Other resource types: Returns an empty string.
     *
     * @param context The application context required for accessing string resources.
     * @return The string representation of the UiText object.
     *
     * @see UiText.asString
     * @see Constants.KEY_STRING
     * @see Constants.KEY_PLURALS
     */
    fun asString(context: Context): String {
        return when (this) {
            is Value -> value
            is Resource -> {
                when (context.resources.getResourceTypeName(id)) {
                    Constants.KEY_STRING -> context.getString(id, *args)
                    Constants.KEY_PLURALS -> context.resources.getQuantityString(
                        id,
                        args.first() as Int,
                        *args
                    )

                    else -> ""
                }
            }
        }
    }
}

/**
 * Provides a default UiText value if the current UiText is empty.
 *
 * This function checks if the current UiText object is empty (using its `isEmpty` method).
 * If it's empty, the function calls the provided `defaultValue` lambda which should return
 * another UiText object. Otherwise, it returns the current UiText object itself.
 *
 * This can be useful for handling cases where UI text might be missing and you want to
 * display a default message instead.
 *
 * @param defaultValue A lambda function that returns a UiText object to be used as the default value.
 * @return The current UiText object if not empty, or the default value from the lambda.
 */
fun UiText.ifEmpty(defaultValue: () -> UiText): UiText {
    return if (this.isEmpty()) {
        defaultValue()
    } else {
        this
    }
}

/**
 * Provides a default UiText value if the current UiText is blank (empty or whitespace).
 *
 * This function is similar to `ifEmpty` but uses `isBlank` to check for empty or whitespace
 * content in the UiText object. The behavior is otherwise the same.
 *
 * @param defaultValue A lambda function that returns a UiText object to be used as the default value.
 * @return The current UiText object if not blank, or the default value from the lambda.
 */
fun UiText.ifBlank(defaultValue: () -> UiText): UiText {
    return if (this.isBlank()) {
        defaultValue()
    } else {
        this
    }
}

/**
 * Checks if the UiText object is null or represents an empty string value.
 *
 * This function uses a when expression to check for different scenarios:
 *  - If the UiText is null, it returns true.
 *  - If it's a `Resource` type, it always returns false (assuming resources aren't empty).
 *  - If it's a `Value` type, it calls `value.isEmpty` to check for an empty string.
 *
 * This function can be used for conditional logic based on the presence or emptiness of the UiText.
 *
 * @return True if the UiText is null or empty, False otherwise.
 */
fun UiText?.isNullOrEmpty(): Boolean {
    return when (this) {
        null -> true
        is UiText.Resource -> false
        is UiText.Value -> value.isEmpty()
    }
}

/**
 * Checks if the UiText object represents a non-empty string value.
 *
 * This function is the opposite of `isNullOrEmpty`. It returns true if the UiText is not null
 * and represents a non-empty string value (either a resource or a non-empty string).
 *
 * @return True if the UiText is not null and has a non-empty string value, False otherwise.
 */
fun UiText.isNotEmpty(): Boolean {
    return when (this) {
        is UiText.Value -> value.isNotEmpty()
        is UiText.Resource -> true
    }
}

/**
 * Checks if the UiText object represents an empty string value.
 *
 * This function checks if the UiText is a `Value` type and its `value` property is empty.
 * It returns false for all other scenarios (null UiText or Resource type).
 *
 * @return True if the UiText is a Value type with an empty string value, False otherwise.
 */
fun UiText.isEmpty(): Boolean {
    return when (this) {
        is UiText.Value -> value.isEmpty()
        is UiText.Resource -> false
    }
}

/**
 * Checks if the UiText object represents a blank string value (empty or whitespace).
 *
 * This function checks if the UiText is a `Value` type and its `value` property is blank
 * (empty or consisting only of whitespace characters). It returns false for all other
 * scenarios (null UiText or Resource type).
 *
 * This function can be useful for validation or conditional logic based on the content of the UiText.
 *
 * @return True if the UiText is a Value type with a blank string value, False otherwise.
 */
fun UiText.isBlank(): Boolean {
    return when (this) {
        is UiText.Value -> value.isBlank()
        is UiText.Resource -> false
    }
}

/**
 * Checks if the UiText content (converted to lowercase) contains a specific key.
 *
 * This function checks if the UiText is a `Value` type and performs a case-insensitive search
 * for the provided `key` within the lowercase representation of the `value` property.
 * It returns false for all other scenarios (null UiText or Resource type).
 *
 * This function can be useful for filtering or searching based on keywords within the UiText content.
 *
 * @param key The string to search for within the UiText content (case-insensitive).
 * @return True if the lowercase UiText content contains the key, False otherwise.
 */
fun UiText.contains(key: String): Boolean {
    return when (this) {
        is UiText.Value -> value.lowercase().contains(key)
        is UiText.Resource -> false
    }
}

/**
 * Converts a String object to a UiText.Value object.
 *
 * This function creates a new UiText.Value object with the provided String value.
 * This can be useful for creating UiText objects directly from String literals or other String sources.
 *
 * @return A new UiText.Value object with the provided String value.
 */
fun String.toUiText(): UiText.Value = UiText.Value(this)