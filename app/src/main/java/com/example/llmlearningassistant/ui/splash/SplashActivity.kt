package com.example.llmlearningassistant.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.llmlearningassistant.R
import com.example.llmlearningassistant.data.PrefsManager
import com.example.llmlearningassistant.ui.home.HomeActivity
import com.example.llmlearningassistant.ui.login.LoginActivity

/**
 * Brief splash - animates in the logo, then routes the user based on login state.
 * Contributes to the "fluid animations" requirement from the task brief.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = PrefsManager(this)
            val next = if (prefs.isLoggedIn() && prefs.getUser() != null) {
                HomeActivity::class.java
            } else {
                LoginActivity::class.java
            }
            startActivity(Intent(this, next))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 1200L)
    }
}
