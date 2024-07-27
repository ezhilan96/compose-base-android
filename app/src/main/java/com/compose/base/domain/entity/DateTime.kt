package com.compose.base.domain.entity

import com.compose.base.core.Constants.DEFAULT_STRING
import java.util.Calendar

data class DateTime(
    var date: String = DEFAULT_STRING,
    var time: String = DEFAULT_STRING,
    var calendar: Calendar = Calendar.getInstance(),
)