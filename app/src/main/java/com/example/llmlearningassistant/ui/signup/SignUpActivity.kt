package com.example.llmlearningassistant.ui.signup

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.llmlearningassistant.R
import com.example.llmlearningassistant.data.PrefsManager
import com.example.llmlearningassistant.data.User
import com.example.llmlearningassistant.databinding.ActivitySignUpBinding
import com.example.llmlearningassistant.ui.interests.InterestsActivity

/**
 * "Lets get you Setup!" screen.
 * Collects username, email (with confirm), password (with confirm), phone.
 * Stores the new account locally, then proceeds to the Interests screen.
 */
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var prefs: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsManager(this)

        binding.titleBlock.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.fade_slide_in)
        )

        binding.btnCreateAccount.setOnClickListener { attemptSignUp() }
    }

    private fun attemptSignUp() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val confirmEmail = binding.etConfirmEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        val phone = binding.etPhone.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || confirmEmail.isEmpty() ||
            password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty()
        ) {
            toast(R.string.signup_fill_all); return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast(R.string.signup_invalid_email); return
        }
        if (email != confirmEmail) { toast(R.string.signup_emails_mismatch); return }
        if (password != confirmPassword) { toast(R.string.signup_passwords_mismatch); return }

        val user = User(username, email, phone, password, interests = emptyList())
        prefs.saveUser(user)
        // Not marking logged-in yet; we do that after interests are picked.
        prefs.setLoggedIn(false)

        startActivity(Intent(this, InterestsActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun toast(resId: Int) = Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}
