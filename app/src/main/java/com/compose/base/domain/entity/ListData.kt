package com.compose.base.domain.entity

import com.compose.base.core.Constants

enum class BookingType { customer, taxida, business, ets }

data class ListData(
    val id: Int = Constants.DEFAULT_INT,
    val date: String = Constants.DEFAULT_STRING,
    val time: String = Constants.DEFAULT_STRING,
)