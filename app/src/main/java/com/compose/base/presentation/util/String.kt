package com.compose.base.presentation.util

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.compose.base.R
import com.compose.base.core.Constants
import com.compose.base.core.Constants.SINGLE_DECIMAL_WRAPPING
import com.compose.base.domain.entity.DateTime
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

/**
 * The [AppTextActions] class provides a collection of utility functions for manipulating and
 * formatting text data commonly used in an app.
 */
class AppTextActions {

    /**
     * Converts a UTC timestamp string to a DateTime object.
     * Handles parsing exceptions and logs them using Firebase Crashlytics.
     *
     * @param utcTime The UTC timestamp string in the format specified by Constants.UTC_DATE_FORMAT_PATTERN.
     * @return A DateTime object representing the converted date and time.
     */
    fun convertUTC(utcTime: String): DateTime {
        var date = ""
        var time = ""
        val calendar = Calendar.getInstance()
        try {
            SimpleDateFormat(Constants.UTC_DATE_FORMAT_PATTERN, Locale.ENGLISH).parse(utcTime)
                ?.let { parsedDate ->
                    calendar.time = parsedDate
                    //calendar.get(Calendar.AM_PM) returns 0 for AM and 1 for PM
                    val isAm: Boolean = calendar.get(Calendar.AM_PM) == 0
                    date = calendar.getDisplayDate()
                    // Pads the hour with a leading zero if necessary
                    val hour = padZero(calendar.get(Calendar.HOUR)).let {
                        // Converts 00 to 12/Convert 24-hour format to 12-hour format
                        if (it == "00") "12" else it
                    }
                    // Pads the minute with a leading zero if necessary
                    val minute = padZero(calendar.get(Calendar.MINUTE))
                    time = "$hour:$minute " + if (isAm) Constants.AM else Constants.PM
                }
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
        }
        return DateTime(
            date, time, calendar
        )
    }

    /**
     * Pads a numeric value with leading zeros to a specified length.
     *
     * @param number The integer value to pad. Defaults to "0" if null.
     * @param length The desired length of the padded string. Defaults to 2.
     * @return The padded string representation of the number.
     */
    fun padZero(
        number: Int?,
        length: Int = 2,
    ): String = number?.let { String.format("%0${length}d", it) } ?: "0".repeat(length)

    /**
     * Converts a number of hours into a user readable duration string using plurals resources.
     *
     * @param hours The number of hours to represent as a duration.
     * @return A UiText object representing the formatted duration string, or null if hours is null.
     */
    fun getDurationFromHours(hours: Int?): UiText? = when {
        hours == null -> null

        hours / 24 > 0 -> {
            val days = hours / 24
            UiText.Resource(R.plurals.body_days, days)
        }

        else -> UiText.Resource(R.plurals.body_hours, hours)
    }
}

/**
 * Formats the current Calendar object into a human-readable date string (DD Month YYYY).
 *
 * This extension function simplifies date formatting for Calendar instances.
 *
 * @receiver The Calendar object to be formatted.
 * @return The formatted date string.
 */
fun Calendar.getDisplayDate(): String {
    // Pads the day with a leading zero if necessary
    val date = get(Calendar.DATE).padZero()
    // Month is zero-indexed
    val month = Constants.MONTH_LIST[get(Calendar.MONTH)]
    // Takes the last two digits of the year
    val year = get(Calendar.YEAR).toString().takeLast(2)
    return "$date $month $year"
}

/**
 * Limits a floating-point value to one decimal place with an optional decimal wrapping format.
 *
 * This extension function simplifies formatting floating-point values for display purposes.
 *
 * @receiver The nullable Float value to be formatted. Defaults to 0.0f if null.
 * @param decimalWrapping The desired decimal wrapping format string (e.g., "#.0", "#,##.0").
 *                        Defaults to SINGLE_DECIMAL_WRAPPING (e.g., "#.0").
 * @return The formatted string representation of the value.
 */
fun Float?.limitDecimalPoint(
    decimalWrapping: String = SINGLE_DECIMAL_WRAPPING,
): String = (this ?: 0.0f).let {
    DecimalFormat(decimalWrapping).format((it * 10.0).roundToInt() / 10.0)
}

/**
 * Pads an integer value with leading zeros to a specified length.
 *
 * This extension function simplifies padding integer values for display purposes.
 *
 * @receiver The nullable Int value to be padded. Defaults to "0" if null.
 * @param length The desired length of the padded string. Defaults to 2.
 * @return The padded string representation of the number.
 */
fun Int?.padZero(length: Int = 2): String =
    this?.let { String.format("%0${length}d", it) } ?: "0".repeat(length)