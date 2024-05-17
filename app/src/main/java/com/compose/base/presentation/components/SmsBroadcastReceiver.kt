package com.compose.base.presentation.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.compose.base.core.Constants.OTP_REGEX
import com.compose.base.domain.repository.AuthRepository
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SmsBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var authRepo: AuthRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
            val status: Status = intent.extras?.get(SmsRetriever.EXTRA_STATUS) as Status
            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val sms = intent.extras?.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                    val otp = sms?.let { OTP_REGEX.toRegex().find(it) }?.value ?: ""
                    authRepo.updateAutoReadOtp(otp)
                }

                else -> authRepo.updateAutoReadOtp("")
            }
        }
    }
}