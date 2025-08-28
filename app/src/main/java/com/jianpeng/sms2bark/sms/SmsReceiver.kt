
package com.jianpeng.sms2bark.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (msgs.isNotEmpty()) {
                val sender = msgs[0].originatingAddress ?: "Unknown"
                val body = msgs.joinToString(separator = "") { it.messageBody ?: "" }

                val input = Data.Builder()
                    .putString("sender", sender)
                    .putString("body", body)
                    .build()
                val work = OneTimeWorkRequestBuilder<SmsPushWorker>().setInputData(input).build()
                WorkManager.getInstance(context).enqueue(work)
            }
        }
    }
}
