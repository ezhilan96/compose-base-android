package com.compose.base.domain.entity

import com.compose.base.core.Constants

enum class UserVerificationStatus { DocumentEmpty, DocumentVerificationPending, Done }

data class UserData(
    val id: Int,
    val name: String,
    val phone: String,
    val token: String,
)

data class LatLng(
    val lat: Double? = Constants.DEFAULT_DOUBLE,
    val lng: Double? = Constants.DEFAULT_DOUBLE,
)