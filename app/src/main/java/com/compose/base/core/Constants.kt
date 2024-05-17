package com.compose.base.core

const val TAG = "logi"

object Constants {

    const val GOOGLE_DNS = "8.8.8.8"
    const val SUPPORT_CONTACT_NO = "+91 89392 92000"

    const val DEFAULT_STRING: String = ""
    const val DEFAULT_INT: Int = -1
    const val DEFAULT_LONG: Long = -1L
    const val DEFAULT_DOUBLE: Double = -1.0
    const val DEFAULT_FILE_EXTENSION: String = ".jpg"

    val MONTH_LIST: Array<String> =
        arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
//    val DAY_OF_WEEK: Array<String> = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    const val ALPHA_NOTIFICATION_ID = 1
    const val BETA_NOTIFICATION_ID = 2

    const val TWO_MB = 2 * 1024 * 1024
    const val ONE_SEC_IN_MILLIS = 1 * 1000L
    const val SIXTY_SEC_IN_MILLIS = 60 * 1000L
    const val FIFTEEN_MINUTES_IN_MILLIS = 15 * 60 * 1000
    const val TWENTY_MINUTES_IN_MILLIS = 20 * 60 * 1000
    const val ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000

    const val DOUBLE_ZERO_PADDING = "%02d"
    const val SINGLE_DECIMAL_WRAPPING: String = "#.#"
    const val UTC_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val OTP_REGEX = "[0-9]{4}"
    const val AM = "AM"
    const val PM = "PM"

    const val ROUTE_SPLASH = "splashScreen"
    const val ROUTE_AUTH = "auth"
    const val ROUTE_LOGIN = "loginScreen"
    const val ROUTE_OTP = "otpScreen"
    const val ROUTE_HOME = "home"
    const val ROUTE_DASHBOARD = "dashboardScreen"
    const val ROUTE_LIST = "listScreen"
    const val ROUTE_DETAIL = "detailScreen"
    const val ROUTE_PROFILE = "profileScreen"
    const val ROUTE_ARG_PHONE = "phone"
    const val ROUTE_ARG_ID = "id"

    const val KEY_ACTION = "ACTION"
    const val KEY_REFRESH = "refresh"
    const val KEY_NOTIFICATION_ID = "NotificationBookingId"
    const val KEY_STRING = "string"
    const val KEY_PLURALS = "plurals"
    const val KEY_USER_PREFERENCES = "user_preferences"
    const val KEY_INCORRECT = "incorrect"
    const val KEY_INVALID = "invalid"
    const val KEY_CONTENT = "content"

    const val JSON_TITLE = "title"
    const val JSON_BODY = "body"
    const val JSON_TYPE = "type"
    const val JSON_ID = "id"
    const val JSON_ANDROID = "Android"
    const val JSON_MULTIPART = "multipart/form-data"
    const val JSON_IMAGE = "image"
    const val JSON_AUTH_HEADER = "Authorization"
    const val JSON_BEARER_PREFIX = "bearer"
    const val JSON_CORPORATE = "corporate"
    const val JSON_SHUTTLE = "shuttle"
    const val JSON_DRIVER = "driver"
    const val JSON_NIGHT_CHARGE = "NIGHT_CHARGE"
    const val JSON_BASE_FARE = "BASE_FARE"
    const val JSON_OFFICE_PICKUP = "Office pickup"
    const val JSON_HOME_PICKUP = "Home pickup"
    const val JSON_BOOKRIDE = "bookRide"
    const val JSON_OUTSTATION = "outstation"
    const val JSON_MULTICITY = "multiCity"
    const val JSON_PACKAGE = "package"
    const val JSON_HOURLYRENTAL = "hourlyRental"
    const val JSON_HOURLY_RENTAL = "hourly-rental"
    const val JSON_LOCAL = "local"
    const val JSON_ON_BOARDED = "onboarded"
    const val JSON_DROPPED = "dropped"
    const val JSON_NO_SHOW = "no show"
    const val JSON_TO_BE_ONBOARD = "to be onboard"
    const val JSON_TO_BE_DROPPED = "to be dropped"
}