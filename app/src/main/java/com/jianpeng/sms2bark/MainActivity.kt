package com.jianpeng.sms2bark

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jianpeng.sms2bark.core.ForegroundKeepAliveService

class MainActivity : ComponentActivity() {
    private lateinit var tvStatus: TextView
    private lateinit var tvLast: TextView
    private lateinit var swService: Switch

    private val lastPushReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.jianpeng.sms2bark.LAST_PUSH_UPDATED") {
                tvLast.text = getSharedPreferences("app", MODE_PRIVATE)
                    .getString("last_push", "(无)")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        tvLast = findViewById(R.id.tvLastPush)
        swService = findViewById(R.id.swService)

        findViewById<Button>(R.id.btnPerm).setOnClickListener { requestAllPermissions() }
        findViewById<Button>(R.id.btnBattery).setOnClickListener { requestIgnoreBatteryOptimizations() }

        swService.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) startKeepAlive() else stopKeepAlive()
            updateStatus()
        }
        tvLast.text = getSharedPreferences("app", MODE_PRIVATE).getString("last_push", "(无)")
        updateStatus()

        // Fix for Android 12+ dynamic broadcast receiver security exception
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.RECEIVER_NOT_EXPORTED
        } else {
            0
        }
        registerReceiver(lastPushReceiver, IntentFilter("com.jianpeng.sms2bark.LAST_PUSH_UPDATED"), flags)

        requestAllPermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(lastPushReceiver)
    }

    private fun updateStatus() {
        val running = ForegroundKeepAliveService.isRunning
        tvStatus.text = if (running) getString(R.string.service_running) else getString(R.string.service_stopped)
        swService.isChecked = running
    }

    private fun startKeepAlive() {
        val i = Intent(this, ForegroundKeepAliveService::class.java)
        ContextCompat.startForegroundService(this, i)
    }

    private fun stopKeepAlive() {
        val i = Intent(this, ForegroundKeepAliveService::class.java)
        stopService(i)
    }

    private fun requestAllPermissions() {
        val perms = mutableListOf(Manifest.permission.RECEIVE_SMS)
        if (Build.VERSION.SDK_INT >= 33) perms += Manifest.permission.POST_NOTIFICATIONS
        ActivityCompat.requestPermissions(this as Activity, perms.toTypedArray(), 100)
    }

    private fun requestIgnoreBatteryOptimizations() {
        try {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            val pkg = packageName
            if (!pm.isIgnoringBatteryOptimizations(pkg)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    .setData(Uri.parse("package:$pkg"))
                startActivity(intent)
            } else {
                AlertDialog.Builder(this)
                    .setMessage("已忽略电池优化")
                    .setPositiveButton("确定", null)
                    .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}