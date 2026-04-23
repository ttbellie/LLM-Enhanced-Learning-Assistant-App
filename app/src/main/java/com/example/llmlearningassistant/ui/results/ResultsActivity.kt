package com.example.llmlearningassistant.ui.results

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.llmlearningassistant.R
import com.example.llmlearningassistant.data.DummyData
import com.example.llmlearningassistant.data.LearningTask
import com.example.llmlearningassistant.databinding.ActivityResultsBinding
import com.example.llmlearningassistant.ui.home.HomeActivity

/**
 * "Your Results" screen.
 *
 * Lists every question with the user's pick vs the correct answer, plus an
 * AI-generated explanation (LLM learning utility #2 - "explain why the answer
 * is correct/incorrect"). Each row drives its own Gemini call and shows
 * loading / success / error states.
 */
class ResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultsBinding
    private lateinit var task: LearningTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)
        val qIds = intent.getIntArrayExtra(EXTRA_QUESTION_IDS) ?: IntArray(0)
        val picks = intent.getIntArrayExtra(EXTRA_PICKS) ?: IntArray(0)

        val found = DummyData.getTasksForInterests(emptyList()).firstOrNull { it.id == taskId }
        if (found == null) { finish(); return }
        task = found

        binding.header.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.fade_slide_in)
        )

        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Build (question, picked) pairs in the same order as the quiz.
        val rows = task.questions.mapIndexed { i, q ->
            val picked = picks.getOrNull(qIds.indexOf(q.id)) ?: -1
            ResultsAdapter.Row(q, picked)
        }

        val adapter = ResultsAdapter(rows, host = this)
        binding.rvResults.layoutManager = LinearLayoutManager(this)
        binding.rvResults.adapter = adapter
        binding.rvResults.scheduleLayoutAnimation()

        binding.btnContinue.setOnClickListener {
            val i = Intent(this, HomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(i)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }

    companion object {
        const val EXTRA_TASK_ID = "task_id"
        const val EXTRA_QUESTION_IDS = "question_ids"
        const val EXTRA_PICKS = "picks"
    }
}
