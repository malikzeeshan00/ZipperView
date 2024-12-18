package com.example.maximustask.CustomZipper.services

import android.annotation.SuppressLint
import com.example.maximustask.CustomZipper.recicvers.LockScreenReceiver
import com.example.maximustask.CustomZipper.ui.HomeActivity
import com.example.maximustask.R


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log

class LockScreenService : Service() {

    private lateinit var lockScreenReceiver: LockScreenReceiver

    override fun onCreate() {
        super.onCreate()
        lockScreenReceiver = LockScreenReceiver()
        registerReceiver(lockScreenReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        })

        // Start foreground service
        startForegroundService()
    }

    @SuppressLint("NewApi")
    private fun startForegroundService() {
        val channelId = "lock_screen_channel"
        val channelName = "Lock Screen Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, HomeActivity::class.java)
        var pendingIntent: PendingIntent? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE
            )
        else
            pendingIntent =
                PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )


        val notification: Notification = Notification.Builder(this, channelId)
            .setContentTitle("Lock Screen Service")
            .setContentText("Monitoring screen lock/unlock events.")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your own icon
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(lockScreenReceiver)
    }
}