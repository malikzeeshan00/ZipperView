package com.example.maximustask.CustomZipper.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter

import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.maximustask.CustomZipper.recicvers.LockScreenReceiver
import com.example.maximustask.CustomZipper.services.LockScreenService
import com.example.maximustask.CustomZipper.utils.VerticalLocker
import com.example.maximustask.CustomZipper.utils.ZipperLock
import com.example.maximustask.R


class HomeActivity : AppCompatActivity(), View.OnTouchListener {

    private var imgZipper: ImageView? = null

    private var imgFront: ImageView? = null

    private var mZipperLock: ZipperLock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.pin_lock)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        initLocker()
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
        findViewById<ImageView>(R.id.menu_btn)?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
//       val lockScreenReceiver = LockScreenReceiver()
//
//        // Register the receiver to listen for screen lock/unlock events
//        val filter = IntentFilter()
//        filter.addAction(Intent.ACTION_SCREEN_OFF)  // Device locked
//        filter.addAction(Intent.ACTION_SCREEN_ON)   // Device unlocked
//        registerReceiver(lockScreenReceiver, filter)
        if (Settings.canDrawOverlays(this)) {
            // Start the service to display the zipper unlock screen
//            startService(Intent(this, ZipperOverlayService::class.java))
        } else {
            // Request the permission
            startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION), 123)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        mZipperLock!!.checkMotionEvent(p1)
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initLocker() {

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        this.mZipperLock =
            VerticalLocker(
                metrics.widthPixels,
                metrics.heightPixels,
                this
            )
        this.imgZipper = findViewById<View>(R.id.imgZipper) as ImageView
        imgZipper!!.setOnTouchListener(this)
        this.imgFront = findViewById<View>(R.id.imgFront) as ImageView
        imgFront!!.setOnTouchListener(this)
        mZipperLock?.init(
            this.imgZipper, this.imgFront
        ) {

            val biometricManager = BiometricManager.from(this)
            when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    Toast.makeText(this, "No biometric hardware available.", Toast.LENGTH_SHORT)
                        .show()
                    initLocker()
                }

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    Toast.makeText(this, "Biometric hardware is unavailable.", Toast.LENGTH_SHORT)
                        .show()
                    initLocker()

                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    // Prompt user to register biometric data
                    Toast.makeText(this, "No biometric data enrolled.", Toast.LENGTH_SHORT).show()
                    initLocker()

                }

                BiometricManager.BIOMETRIC_SUCCESS -> {
                    // Proceed with authentication
                    authenticateWithBiometrics()
                }
            }

        }

    }

    private fun authenticateWithBiometrics() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Handle successful authentication
                    Toast.makeText(
                        applicationContext,
                        "Authentication Succeeded!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Handle failed authentication
                    Toast.makeText(applicationContext, "Authentication Failed!", Toast.LENGTH_SHORT)
                        .show()
                    initLocker()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Handle error during authentication
                    Toast.makeText(
                        applicationContext,
                        "Authentication Error: $errString",
                        Toast.LENGTH_SHORT
                    ).show()
                    initLocker()
                }
            })

        // Biometric Prompt Info (e.g., title, description)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setDescription("Use your face or fingerprint to login.")
            .setConfirmationRequired(false)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .build()

        // Start the biometric authentication process
        biometricPrompt.authenticate(promptInfo)


    }

    val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // Handle the back press here
            Toast.makeText(applicationContext, "Back press is disabled", Toast.LENGTH_SHORT).show()
        }
    }
}