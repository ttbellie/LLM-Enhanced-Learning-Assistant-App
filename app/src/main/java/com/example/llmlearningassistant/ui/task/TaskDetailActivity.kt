package com.example.llmlearningassistant.ui.task

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.llmlearningassistant.R
import com.example.llmlearningassistant.data.DummyData
import com.example.llmlearningassistant.data.LearningTask
import com.example.llmlearningassistant.data.Question
import com.example.llmlearningassistant.databinding.ActivityTaskDetailBinding
import com.example.llmlearningassistant.databinding.DialogHintBinding
import com.example.llmlearningassistant.databinding.ItemQuestionBinding
import com.example.llmlearningassistant.network.GeminiClient
import com.example.llmlearningassistant.network.LlmResult
import com.example.llmlearningassistant.ui.results.ResultsActivity
import kotlinx.coroutines.launch

/**
 * Task detail / quiz screen - "Generated Task 1".
 *
 * Shows a list of questions with radio-button options. Each question has a
 * "💡 Get Hint" button that calls Gemini (LLM learning utility #1) and shows
 * the prompt + the model's response inside a dialog with loading + error states.
 *
 * On Submit, passes the user's picks to ResultsActivity.
 */
class TaskDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailBinding
    private lateinit var task: LearningTask

    /** questionId -> picked option index (or -1) */
    private val picks = mutableMapOf<Int, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)
        val found = DummyData.getTasksForInterests(emptyList()).firstOrNull { it.id == taskId }
        if (found == null) { finish(); return }
        task = found

        binding.header.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.fade_slide_in)
        )

        binding.tvTaskTitle.text = task.title
        binding.tvTaskDesc.text = task.description

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        renderQuestions()

        binding.btnSubmit.setOnClickListener { onSubmit() }
    }

    private fun renderQuestions() {
        val inflater = LayoutInflater.from(this)
        task.questions.forEachIndexed { idx, q ->
            val qb = ItemQuestionBinding.inflate(inflater, binding.questionContainer, false)
            qb.tvQuestionNumber.text = "${idx + 1}. Question ${idx + 1}"
            qb.tvQuestionPrompt.text = q.prompt

            // Build radio options
            q.options.forEachIndexed { oi, optionText ->
                val rb = RadioButton(this).apply {
                    id = View.generateViewId()
                    text = optionText
                    textSize = 15f
                    setTextColor(resources.getColor(R.color.white, theme))
                    buttonTintList = resources.getColorStateList(R.color.white, theme)
                    contentDescription = "Option ${oi + 1}: $optionText"
                }
                qb.rgOptions.addView(rb)
                rb.setOnClickListener { picks[q.id] = oi }
            }

            qb.btnHint.contentDescription = "Get AI hint for question ${idx + 1}"
            qb.btnHint.setOnClickListener { showHintDialog(q) }

            binding.questionContainer.addView(qb.root)
        }
    }

    // ================= LLM Feature #1: Generate Hint =================
    private fun showHintDialog(q: Question) {
        val db = DialogHintBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this)
            .setView(db.root)
            .setCancelable(true)
            .create()

        fun setLoading() {
            db.progressBar.visibility = View.VISIBLE
            db.tvPromptLabel.visibility = View.GONE
            db.tvPromptText.visibility = View.GONE
            db.tvResponseLabel.visibility = View.GONE
            db.tvResponseText.visibility = View.GONE
            db.tvError.visibility = View.GONE
            db.btnRetry.visibility = View.GONE
            db.tvLoadingMsg.visibility = View.VISIBLE
            db.tvLoadingMsg.text = getString(R.string.hint_loading)
        }

        fun setSuccess(prompt: String, response: String) {
            db.progressBar.visibility = View.GONE
            db.tvLoadingMsg.visibility = View.GONE
            db.tvPromptLabel.visibility = View.VISIBLE
            db.tvPromptText.visibility = View.VISIBLE
            db.tvResponseLabel.visibility = View.VISIBLE
            db.tvResponseText.visibility = View.VISIBLE
            db.tvError.visibility = View.GONE
            db.btnRetry.visibility = View.GONE
            db.tvPromptText.text = prompt
            db.tvResponseText.text = response
        }

        fun setError(msg: String) {
            db.progressBar.visibility = View.GONE
            db.tvLoadingMsg.visibility = View.GONE
            db.tvPromptLabel.visibility = View.GONE
            db.tvPromptText.visibility = View.GONE
            db.tvResponseLabel.visibility = View.GONE
            db.tvResponseText.visibility = View.GONE
            db.tvError.visibility = View.VISIBLE
            db.btnRetry.visibility = View.VISIBLE
            db.tvError.text = msg
        }

        fun launchCall() {
            setLoading()
            lifecycleScope.launch {
                when (val r = GeminiClient.generateHint(q.prompt, q.options, q.topic)) {
                    is LlmResult.Success -> setSuccess(r.data.prompt, r.data.response)
                    is LlmResult.Error -> setError(r.message)
                    LlmResult.Loading -> setLoading()
                }
            }
        }

        db.btnRetry.setOnClickListener { launchCall() }
        db.btnClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
        launchCall()
    }

    private fun onSubmit() {
        if (picks.size < task.questions.size) {
            Toast.makeText(this, R.string.please_answer_all, Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, ResultsActivity::class.java).apply {
            putExtra(ResultsActivity.EXTRA_TASK_ID, task.id)
            // packed as parallel arrays: questionId[i] -> pickedIndex[i]
            val qIds = IntArray(task.questions.size) { task.questions[it].id }
            val picksArr = IntArray(task.questions.size) { picks[task.questions[it].id] ?: -1 }
            putExtra(ResultsActivity.EXTRA_QUESTION_IDS, qIds)
            putExtra(ResultsActivity.EXTRA_PICKS, picksArr)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    companion object {
        const val EXTRA_TASK_ID = "task_id"
    }
}
