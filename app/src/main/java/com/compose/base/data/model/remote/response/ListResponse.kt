package com.compose.base.data.model.remote.response

data class ListResponse<T : Any>(
    val data: List<T>? = listOf(),
    val count: Int = data?.count() ?: 0,
)