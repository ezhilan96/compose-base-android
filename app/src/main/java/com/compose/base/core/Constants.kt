package com.compose.base.core

import com.compose.base.BuildConfig

const val TAG = BuildConfig.APPLICATION_ID

object Constants {

    const val GOOGLE_DNS = "8.8.8.8" // Public DNS server address
    const val SUPPORT_CONTACT_NO = "+91 1234567890" // Support phone number

    // Default Values
    const val DEFAULT_STRING: String = ""
    const val DEFAULT_INT: Int = -1
    const val DEFAULT_LONG: Long = -1L
    const val DEFAULT_DOUBLE: Double = -1.0
    const val DEFAULT_FILE_EXTENSION: String = ".jpg"

    // Months and Days of the week
    val MONTH_LIST: Array<String> =
        arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val DAY_OF_WEEK: Array<String> = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    // Notification IDs
    const val bookingNotificationId = 1
    const val locationNotificationId = 2

    // Time Conversions
    const val TWO_MB = 2 * 1024 * 1024
    const val ONE_SEC_IN_MILLIS = 1 * 1000L
    const val SIXTY_SEC_IN_MILLIS = 60 * 1000L
    const val FORTY_FIVE_MINUTES_IN_MILLIS = 45 * 60 * 1000
    const val FIFTEEN_MINUTES_IN_MILLIS = 15 * 60 * 1000
    const val TWENTY_MINUTES_IN_MILLIS = 20 * 60 * 1000
    const val ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000

    // Date Formats
    const val DOUBLE_ZERO_PADDING = "%02d"
    const val SINGLE_DECIMAL_WRAPPING: String = "#.#"
    const val UTC_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val OTP_REGEX = "[0-9]{4}"
    const val AM = "AM"
    const val PM = "PM"

    // Navigation Routes
    const val ROUTE_SPLASH = "splash"
    const val ROUTE_AUTH = "auth"
    const val ROUTE_LOGIN = "login"
    const val ROUTE_OTP = "otp"
    const val ROUTE_HOME = "home"
    const val ROUTE_DASHBOARD = "dashboard"
    const val ROUTE_PROFILE = "profileScreen"
    const val ROUTE_CAMERA = "cameraCapture"
    const val ROUTE_ONE = "one"
    const val ROUTE_ONE_INFO = "one info"
    const val ROUTE_TWO = "two"
    const val ROUTE_THREE = "three"

    const val ROUTE_ARG_PHONE = "phone"

    // Local Keys
    const val KEY_ACTION = "ACTION"
    const val KEY_REFRESH = "refresh"
    const val KEY_NOTIFICATION_TYPE = "NotificationType"
    const val KEY_NOTIFICATION_BOOKING_TYPE = "NotificationBookingType"
    const val KEY_NOTIFICATION_BOOKING_ID = "NotificationBookingId"
    const val KEY_STRING = "string"
    const val KEY_PLURALS = "plurals"
    const val KEY_USER_PREFERENCES = "user_preferences"
    const val KEY_INCORRECT = "incorrect"
    const val KEY_INVALID = "invalid"
    const val KEY_PHOTO_URI = "photoUri"
    const val KEY_CONTENT = "content"

    // Network keys
    const val JSON_TITLE = "title"
    const val JSON_BODY = "body"
    const val JSON_TYPE = "type"
    const val JSON_BOOKING_TYPE = "bookingType"
    const val JSON_ID = "id"
    const val JSON_MULTIPART = "multipart/form-data"
    const val JSON_IMAGE = "image"
    const val JSON_AUTH_HEADER = "Authorization"
    const val JSON_BEARER_PREFIX = "bearer"
}