package com.example.llmlearningassistant.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.llmlearningassistant.R
import com.example.llmlearningassistant.data.PrefsManager
import com.example.llmlearningassistant.databinding.ActivityLoginBinding
import com.example.llmlearningassistant.ui.home.HomeActivity
import com.example.llmlearningassistant.ui.signup.SignUpActivity

/**
 * "Welcome, Student!" screen.
 *
 * - Username + password inputs, Login button, "Need an Account?" link.
 * - Validates against the single account stored in SharedPreferences (dummy auth -
 *   task brief permits dummy data).
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefs: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PrefsManager(this)

        // Fade-in animation for the title block (fluid animations requirement)
        binding.titleBlock.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.fade_slide_in)
        )

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.tvNeedAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun attemptLogin() {
        val user = binding.etUsername.text.toString().trim()
        val pass = binding.etPassword.text.toString()

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, R.string.signup_fill_all, Toast.LENGTH_SHORT).show()
            return
        }

        if (prefs.validateCredentials(user, pass)) {
            prefs.setLoggedIn(true)
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        } else {
            Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show()
        }
    }
}
