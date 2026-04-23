package com.example.llmlearningassistant.ui.results

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.llmlearningassistant.R
import com.example.llmlearningassistant.data.Question
import com.example.llmlearningassistant.databinding.ItemResultCardBinding
import com.example.llmlearningassistant.network.GeminiClient
import com.example.llmlearningassistant.network.LlmResult
import kotlinx.coroutines.launch

/**
 * RecyclerView adapter for the Results screen.
 *
 * Each row independently calls Gemini (explain-answer LLM feature) the first
 * time it's bound. The result is cached so scrolling does not re-trigger the
 * request. The task brief requires we show prompt + response and handle loading
 * and failure states - each row does all three.
 */
class ResultsAdapter(
    private val rows: List<Row>,
    private val host: AppCompatActivity
) : RecyclerView.Adapter<ResultsAdapter.VH>() {

    data class Row(val question: Question, val pickedIndex: Int)

    private sealed class State {
        object Idle : State()
        object Loading : State()
        data class Success(val prompt: String, val response: String) : State()
        data class Error(val msg: String) : State()
    }

    private val states = MutableList<State>(rows.size) { State.Idle }

    inner class VH(val b: ItemResultCardBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemResultCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun getItemCount(): Int = rows.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val row = rows[position]
        val q = row.question

        holder.b.tvNumber.text = "${position + 1}. ${q.prompt}"
        val pickedText = q.options.getOrNull(row.pickedIndex) ?: "(no answer)"
        val correctText = q.options.getOrNull(q.correctIndex) ?: "?"
        val isCorrect = row.pickedIndex == q.correctIndex

        holder.b.tvYourAnswer.text = "Your answer: $pickedText"
        holder.b.tvCorrectAnswer.text = "Correct answer: $correctText"
        holder.b.tvVerdict.text = if (isCorrect) {
            holder.itemView.context.getString(R.string.result_correct)
        } else {
            holder.itemView.context.getString(R.string.result_incorrect)
        }
        holder.b.tvVerdict.setTextColor(
            holder.itemView.context.resources.getColor(
                if (isCorrect) R.color.accent_green_dark else R.color.error_red, null
            )
        )

        renderState(holder, position)

        holder.b.btnRetry.setOnClickListener {
            fetchExplanation(holder, position)
        }

        // Kick off fetch if we haven't yet
        if (states[position] is State.Idle) {
            fetchExplanation(holder, position)
        }
    }

    private fun fetchExplanation(holder: VH, position: Int) {
        val row = rows[position]
        states[position] = State.Loading
        renderState(holder, position)

        host.lifecycleScope.launch {
            val result = GeminiClient.explainAnswer(
                questionPrompt = row.question.prompt,
                options = row.question.options,
                pickedIndex = row.pickedIndex,
                correctIndex = row.question.correctIndex,
                topic = row.question.topic
            )
            states[position] = when (result) {
                is LlmResult.Success -> State.Success(result.data.prompt, result.data.response)
                is LlmResult.Error -> State.Error(result.message)
                LlmResult.Loading -> State.Loading
            }
            notifyItemChanged(position)
        }
    }

    private fun renderState(holder: VH, position: Int) {
        val b = holder.b
        when (val s = states[position]) {
            State.Idle, State.Loading -> {
                b.progressBar.visibility = View.VISIBLE
                b.tvLoading.visibility = View.VISIBLE
                b.tvPromptLabel.visibility = View.GONE
                b.tvPromptText.visibility = View.GONE
                b.tvResponseLabel.visibility = View.GONE
                b.tvResponseText.visibility = View.GONE
                b.tvError.visibility = View.GONE
                b.btnRetry.visibility = View.GONE
            }
            is State.Success -> {
                b.progressBar.visibility = View.GONE
                b.tvLoading.visibility = View.GONE
                b.tvPromptLabel.visibility = View.VISIBLE
                b.tvPromptText.visibility = View.VISIBLE
                b.tvResponseLabel.visibility = View.VISIBLE
                b.tvResponseText.visibility = View.VISIBLE
                b.tvError.visibility = View.GONE
                b.btnRetry.visibility = View.GONE
                b.tvPromptText.text = s.prompt
                b.tvResponseText.text = s.response
            }
            is State.Error -> {
                b.progressBar.visibility = View.GONE
                b.tvLoading.visibility = View.GONE
                b.tvPromptLabel.visibility = View.GONE
                b.tvPromptText.visibility = View.GONE
                b.tvResponseLabel.visibility = View.GONE
                b.tvResponseText.visibility = View.GONE
                b.tvError.visibility = View.VISIBLE
                b.btnRetry.visibility = View.VISIBLE
                b.tvError.text = s.msg
            }
        }
    }
}
