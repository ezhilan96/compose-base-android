package com.compose.base.presentation.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.compose.base.core.Constants
import com.compose.base.core.Constants.DEFAULT_STRING
import java.io.Serializable

sealed class UiText : Serializable {

    class Value(val value: String = DEFAULT_STRING) : UiText()

    class Resource(
        val id: Int,
        vararg val args: Any?,
    ) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is Value -> value
            is Resource -> {
                when (LocalContext.current.resources.getResourceTypeName(id)) {
                    Constants.KEY_STRING -> stringResource(
                        id,
                        *args.map { it ?: "" }.toTypedArray()
                    )

                    Constants.KEY_PLURALS -> pluralStringResource(
                        id, args.first() as Int, *args.map { it ?: "" }.toTypedArray()
                    )

                    else -> ""
                }
            }
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is Value -> value
            is Resource -> {
                when (context.resources.getResourceTypeName(id)) {
                    Constants.KEY_STRING -> context.getString(id, *args)
                    Constants.KEY_PLURALS -> context.resources.getQuantityString(
                        id, args.first() as Int, *args
                    )

                    else -> ""
                }

            }
        }
    }
}

fun UiText?.isNullOrEmpty(): Boolean {
    return when (this) {
        is UiText.Value -> value.isEmpty()
        is UiText.Resource -> false
        null -> true
    }
}

fun UiText.isNotEmpty(): Boolean {
    return when (this) {
        is UiText.Value -> value.isNotEmpty()
        is UiText.Resource -> true
    }
}

fun UiText.isEmpty(): Boolean {
    return when (this) {
        is UiText.Value -> value.isEmpty()
        is UiText.Resource -> false
    }
}

fun UiText.contains(key: String): Boolean {
    return when (this) {
        is UiText.Value -> value.lowercase().contains(key)
        is UiText.Resource -> false
    }
}

fun String.toUiText(): UiText.Value = UiText.Value(this)