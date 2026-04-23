package com.example.llmlearningassistant.ui.interests

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.llmlearningassistant.R
import com.example.llmlearningassistant.data.DummyData
import com.example.llmlearningassistant.data.PrefsManager
import com.example.llmlearningassistant.databinding.ActivityInterestsBinding
import com.example.llmlearningassistant.ui.home.HomeActivity
import com.google.android.flexbox.FlexboxLayout

/**
 * "Your Interests" screen.
 * Displays a flexbox grid of topic chips. User can select up to 10.
 * Selection is persisted locally and used by Home to personalise task suggestions.
 */
class InterestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInterestsBinding
    private lateinit var prefs: PrefsManager

    private val selected = mutableSetOf<String>()
    private val maxSelections = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterestsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsManager(this)

        binding.titleBlock.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.fade_slide_in)
        )

        buildChips(binding.chipContainer)

        binding.btnNext.setOnClickListener {
            if (selected.isEmpty()) {
                Toast.makeText(this, R.string.interests_select_one, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            prefs.updateInterests(selected.toList())
            prefs.setLoggedIn(true)
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }

    private fun buildChips(container: FlexboxLayout) {
        val inflater = LayoutInflater.from(this)
        DummyData.AVAILABLE_TOPICS.forEach { topic ->
            val chip = inflater.inflate(R.layout.item_interest_chip, container, false) as TextView
            chip.text = topic
            chip.contentDescription = "Interest topic: $topic"
            chip.setOnClickListener {
                if (chip.isSelected) {
                    chip.isSelected = false
                    chip.setTextColor(resources.getColor(R.color.brand_cyan_dark, theme))
                    selected.remove(topic)
                } else {
                    if (selected.size >= maxSelections) {
                        Toast.makeText(
                            this, R.string.interests_max_reached, Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    chip.isSelected = true
                    chip.setTextColor(resources.getColor(R.color.white, theme))
                    selected.add(topic)
                }
            }
            container.addView(chip)
        }
    }
}
