package com.compose.base.domain.entity

enum class UserVerificationStatus { DocumentEmpty, DocumentVerificationPending, Done }

data class UserData(
    val id: Int,
    val name: String,
    val phone: String,
    val token: String,
)