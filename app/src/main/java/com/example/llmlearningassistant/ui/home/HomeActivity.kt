package com.example.llmlearningassistant.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.llmlearningassistant.R
import com.example.llmlearningassistant.data.DummyData
import com.example.llmlearningassistant.data.PrefsManager
import com.example.llmlearningassistant.databinding.ActivityHomeBinding
import com.example.llmlearningassistant.ui.login.LoginActivity
import com.example.llmlearningassistant.ui.task.TaskDetailActivity

/**
 * "Hello, <name>" home screen. Shows a list of AI-generated tasks personalised to
 * the user's interests (selected during sign-up). Each card opens TaskDetailActivity.
 *
 * Tapping the profile avatar logs the user out (primary way to reach Login again for demo).
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var prefs: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsManager(this)

        val user = prefs.getUser()
        if (user == null || !prefs.isLoggedIn()) {
            goToLogin(); return
        }

        binding.tvName.text = user.username

        // Fluid list entrance animation
        binding.greeting.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.fade_slide_in)
        )

        val tasks = DummyData.getTasksForInterests(user.interests)
        binding.tvTaskDue.text = getString(R.string.task_due_count, tasks.size)

        val adapter = TaskAdapter(tasks) { task ->
            val intent = Intent(this, TaskDetailActivity::class.java)
            intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.id)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter
        binding.rvTasks.scheduleLayoutAnimation()

        binding.ivProfile.setOnClickListener {
            prefs.logout()
            goToLogin()
        }
    }

    private fun goToLogin() {
        val i = Intent(this, LoginActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
        finish()
    }
}
