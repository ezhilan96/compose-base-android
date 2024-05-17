package com.compose.base.data.model.remote.response

data class AppConfigResponse(
    val SiteSettings: List<SiteSettings>?,
    val KillSwitch: List<ForceUpdateConfig>? = listOf(),
)

data class SiteSettings(
    val id: Int?,
    val tollKey: String?,
    val googleKey: String?,
)

data class ForceUpdateConfig(
    val versionCode: String,
    val `package`: String,
    val buildNumber: String,
    val isForceUpdate: Boolean?,
    val isPartialUpdate: Boolean?,
    val block: Boolean?,
)
