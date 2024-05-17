package com.compose.base.domain.entity

import com.compose.base.R
import com.compose.base.presentation.util.UiText

val b2cStatusDataList = arrayOf(
    Pair(BookingStatusLabel.Unknown, 0),
    Pair(BookingStatusLabel.Pending, 1),
    Pair(BookingStatusLabel.Accepted, 2),
    Pair(BookingStatusLabel.Assigned, 3),
    Pair(BookingStatusLabel.DriverAccepted, 4),
    Pair(BookingStatusLabel.DriverRejected, 5),
    Pair(BookingStatusLabel.EnRouteToPickup, 6),
    Pair(BookingStatusLabel.Arrived, 7),
    Pair(BookingStatusLabel.OnTrip, 8),
    Pair(BookingStatusLabel.Cancelled, 9),
    Pair(BookingStatusLabel.Completed, 10),
    Pair(BookingStatusLabel.OnBid, 11),
)

val b2bStatusDataList = arrayOf(
    Pair(BookingStatusLabel.Unknown, 0),
    Pair(BookingStatusLabel.Pending, 1),
    Pair(BookingStatusLabel.OperatorAssigned, 2),
    Pair(BookingStatusLabel.DriverAssigned, 3),
    Pair(BookingStatusLabel.DriverAccepted, 4),
    Pair(BookingStatusLabel.DriverRejected, 5),
    Pair(BookingStatusLabel.EnRouteToPickup, 6),
    Pair(BookingStatusLabel.Arrived, 7),
    Pair(BookingStatusLabel.EmptyTrip, 8),
    Pair(BookingStatusLabel.OnTrip, 9),
    Pair(BookingStatusLabel.Completed, 10),
    Pair(BookingStatusLabel.Cancelled, 11),
    Pair(BookingStatusLabel.NoShow, 17),
    Pair(BookingStatusLabel.PendingForApproval, 21),
)

val etsStatusDataList = arrayOf(
    Pair(BookingStatusLabel.Unknown, 0),
    Pair(BookingStatusLabel.Queued, 1),
    Pair(BookingStatusLabel.PendingForOperatorAssign, 2),
    Pair(BookingStatusLabel.OperatorAssigned, 3),
    Pair(BookingStatusLabel.DriverAssigned, 4),
    Pair(BookingStatusLabel.DriverRejected, 5),
    Pair(BookingStatusLabel.DriverAccepted, 6),
    Pair(BookingStatusLabel.EnRouteToPickup, 7),
    Pair(BookingStatusLabel.Arrived, 8),
    Pair(BookingStatusLabel.Started, 9),
    Pair(BookingStatusLabel.Completed, 10),
    Pair(BookingStatusLabel.Cancelled, 11),
)

val ondcStatusDataList = arrayOf(
    Pair(BookingStatusLabel.Unknown, 0),
    Pair(BookingStatusLabel.Queued, 1),
    Pair(BookingStatusLabel.Search, 2),
    Pair(BookingStatusLabel.Select, 3),
    Pair(BookingStatusLabel.Init, 4),
    Pair(BookingStatusLabel.Confirmed, 5),
    Pair(BookingStatusLabel.Broadcasted, 6),
    Pair(BookingStatusLabel.DriverAssigned, 7),
    Pair(BookingStatusLabel.DriverAccepted, 8),
    Pair(BookingStatusLabel.EnRouteToPickup, 9),
    Pair(BookingStatusLabel.Arrived, 10),
    Pair(BookingStatusLabel.OnTrip, 11),
    Pair(BookingStatusLabel.Completed, 12),
    Pair(BookingStatusLabel.Cancelled, 13),
    Pair(BookingStatusLabel.NoShow, 14),
    Pair(BookingStatusLabel.Invoiced, 15),
    Pair(BookingStatusLabel.DriverRejected, 16),
)

data object BookingStatusLabel {
    val Unknown = UiText.Value()
    val Pending = UiText.Resource(R.string.label_pending)
    val Accepted = UiText.Resource(R.string.label_accepted)
    val Assigned = UiText.Resource(R.string.label_assigned)
    val DriverAccepted = UiText.Resource(R.string.label_driver_accepted)
    val DriverRejected = UiText.Resource(R.string.label_driver_rejected)
    val EnRouteToPickup = UiText.Resource(R.string.label_en_route_to_pickup)
    val Arrived = UiText.Resource(R.string.label_arrived)
    val OnTrip = UiText.Resource(R.string.label_on_trip)
    val Cancelled = UiText.Resource(R.string.label_cancelled)
    val Completed = UiText.Resource(R.string.label_completed)
    val OnBid = UiText.Resource(R.string.label_on_bid)
    val OperatorAssigned = UiText.Resource(R.string.label_operator_assigned)
    val DriverAssigned = UiText.Resource(R.string.label_driver_assigned)
    val EmptyTrip = UiText.Resource(R.string.label_empty_trip)
    val NoShow = UiText.Resource(R.string.label_no_show)
    val PendingForApproval = UiText.Resource(R.string.label_pending_for_approval)
    val Queued = UiText.Resource(R.string.label_queued)
    val Search = UiText.Resource(R.string.label_search)
    val Select = UiText.Resource(R.string.label_select)
    val Init = UiText.Resource(R.string.label_init)
    val Confirmed = UiText.Resource(R.string.label_confirmed)
    val Broadcasted = UiText.Resource(R.string.label_broadcasted)
    val Invoiced = UiText.Resource(R.string.label_invoiced)
    val PendingForOperatorAssign = UiText.Resource(R.string.label_pending_for_operator_assign)
    val Started = UiText.Resource(R.string.label_started)
}

data object TripTypeLabel {
    val RoundTrip = UiText.Resource(R.string.label_round_trip)
    val OneWay = UiText.Resource(R.string.label_one_way)
    val MultiCity = UiText.Resource(R.string.label_multi_city)
    val HourlyRental = UiText.Resource(R.string.label_hourly_rental)
    val Local = UiText.Resource(R.string.label_local)
    val OfficePickup = UiText.Resource(R.string.label_office_pickup)
    val HomePickup = UiText.Resource(R.string.label_home_pickup)
}