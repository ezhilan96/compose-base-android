package com.compose.base.presentation.util

import com.compose.base.R
import com.compose.base.core.Constants
import com.compose.base.core.Constants.SINGLE_DECIMAL_WRAPPING
import com.compose.base.domain.entity.DateTime
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

class AppTextActions {
    fun convertCalendar(calendar: Calendar): String {
        val date = Constants.DOUBLE_ZERO_PADDING.format(calendar.get(Calendar.DATE))
        val month = Constants.MONTH_LIST[calendar.get(Calendar.MONTH)]
        val year = calendar.get(Calendar.YEAR).toString().takeLast(2)
        return "$date $month $year"
    }

    fun convertUTC(utcTime: String): DateTime {
        var date = ""
        var time = ""
        val calendar = Calendar.getInstance()
        try {
            SimpleDateFormat(Constants.UTC_DATE_FORMAT_PATTERN, Locale.ENGLISH).parse(utcTime)
                ?.let { parsedDate ->
                    calendar.time = parsedDate
                    val isAm: Boolean = calendar.get(Calendar.AM_PM) == 0
                    date = convertCalendar(calendar)
                    val hour =
                        Constants.DOUBLE_ZERO_PADDING.format(calendar.get(Calendar.HOUR)).let {
                            if (it == "00") "12" else it
                        }
                    val minute = Constants.DOUBLE_ZERO_PADDING.format(calendar.get(Calendar.MINUTE))
                    time = "$hour:$minute " + if (isAm) Constants.AM else Constants.PM
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return DateTime(
            date, time, calendar
        )
    }

    fun limitDecimalPoint(
        value: Float?,
        decimalWrapping: String = SINGLE_DECIMAL_WRAPPING,
    ): String = (value ?: 0.0f).let {
        DecimalFormat(decimalWrapping).format((it * 10.0).roundToInt() / 10.0)
    }

    fun padZero(
        number: Int?,
        length: Int = 2,
    ): String = number?.let { String.format("%0${length}d", it) } ?: "0".repeat(length)

    fun getDurationFromHours(hours: Int?): UiText? = when {
        hours == null -> null

        hours / 24 > 0 -> {
            val days = hours / 24
            UiText.Resource(R.plurals.body_days, days)
        }

        else -> UiText.Resource(R.plurals.body_hours, hours)
    }
}