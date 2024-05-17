package com.compose.base.data.model.remote.response

import com.compose.base.domain.entity.LatLng

data class DirectionResponse(
    val routes: List<Route>?,
)

data class Route(
    val legs: List<Leg>?,
)

data class Leg(
    val distance: Distance?,
    val duration: Duration?,
    val end_address: String?,
    val end_location: LatLng?,
    val start_address: String?,
    val start_location: LatLng?,
    val steps: List<Step>?,
)

data class Distance(
    val text: String?,
    val value: Int?,
)

data class Duration(
    val text: String?,
    val value: Int?,
)

data class Step(
    val polyline: Polyline?,
)

data class Polyline(
    val points: String?,
)
