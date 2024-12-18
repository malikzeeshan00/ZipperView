package com.example.maximustask.CustomZipper.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.maximustask.CustomZipper.services.LockScreenService
import com.example.maximustask.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btn_on)?.setOnClickListener {
            val serviceIntent = Intent(this, LockScreenService::class.java)
            startService(serviceIntent)
        }

        findViewById<Button>(R.id.btn_off)?.setOnClickListener {

            val serviceIntent = Intent(this, LockScreenService::class.java)
            stopService(serviceIntent)
        }
    }
}