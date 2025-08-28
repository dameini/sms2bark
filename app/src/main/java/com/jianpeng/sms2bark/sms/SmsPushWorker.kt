
package com.jianpeng.sms2bark.sms

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

class SmsPushWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val sender = inputData.getString("sender") ?: "Unknown"
        val body = inputData.getString("body") ?: ""
        try {
            val title = "SMS来自:" + sender
            val encTitle = URLEncoder.encode(title, "UTF-8")
            val encBody = URLEncoder.encode(body.take(500), "UTF-8")
            val url = BARK_BASE + encTitle + "/" + encBody + "?group=sms&isArchive=1"
            val req = Request.Builder().url(url).build()
            OkHttpClient().newCall(req).execute().use { }

            // save last push
            val sp = applicationContext.getSharedPreferences("app", Context.MODE_PRIVATE)
            sp.edit().putString("last_push", "${'$'}{System.currentTimeMillis()/1000}: ${'$'}sender: ${'$'}body").apply()
            applicationContext.sendBroadcast(Intent("com.jianpeng.sms2bark.LAST_PUSH_UPDATED"))
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        // Your Bark key base
        const val BARK_BASE = "https://api.day.app/ygJByJyhXtBbEqJCkGfdUo/"
    }
}
