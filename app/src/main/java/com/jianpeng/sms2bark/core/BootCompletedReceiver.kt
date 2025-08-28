
package com.jianpeng.sms2bark.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            context.startForegroundService(Intent(context, ForegroundKeepAliveService::class.java))
        }
    }
}
