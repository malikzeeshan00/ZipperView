package com.example.maximustask.CustomZipper.recicvers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.example.maximustask.CustomZipper.ui.HomeActivity

class LockScreenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_OFF -> {
                // The device has been locked (screen turned off)
                Log.d("LockScreenReceiver", "Device Locked")
                onDeviceLocked(context)
            }

            Intent.ACTION_SCREEN_ON -> {
                // The device has been unlocked (screen turned on)
                Log.d("LockScreenReceiver", "Device Unlocked")
                onDeviceUnlocked()
            }
        }
    }

    private fun onDeviceLocked(ctx: Context) {
        val activityIntent = Intent(ctx, HomeActivity::class.java)
        activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ctx.startActivity(activityIntent)
        // Handle the event when the device is locked
        // You can add any code here to perform actions when the device is locked.
    }

    private fun onDeviceUnlocked() {
        // Handle the event when the device is unlocked
        // You can add any code here to perform actions when the device is unlocked.
    }
}
