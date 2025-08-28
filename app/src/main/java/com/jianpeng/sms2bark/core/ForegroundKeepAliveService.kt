
package com.jianpeng.sms2bark.core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.jianpeng.sms2bark.R

class ForegroundKeepAliveService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        startForeground(1, createNotification())
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    private fun createNotification(): Notification {
        val channelId = "sms2bark.channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(channelId, "Sms2Bark", NotificationManager.IMPORTANCE_LOW)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(ch)
        }
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Sms2Bark 正在运行")
            .setContentText("监听短信并转发到 Bark")
            .setOngoing(true)
            .build()
    }

    companion object { @Volatile var isRunning: Boolean = false }
}
