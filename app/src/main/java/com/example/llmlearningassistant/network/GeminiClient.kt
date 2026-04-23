package com.example.llmlearningassistant.network

import com.example.llmlearningassistant.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Thin client around the Google Gemini REST API.
 *
 * The API key is read from BuildConfig.GEMINI_API_KEY (set in app/build.gradle.kts).
 * We use the `generateContent` endpoint on the free `gemini-1.5-flash-latest` model.
 *
 * Supports the two LLM-powered learning utilities required by the task brief:
 *  1. generateHint(question)           -> hint for a quiz question
 *  2. explainAnswer(question, picked)  -> explains why a picked answer is right/wrong
 *
 * Each public method returns an LlmResult that the UI observes so we can show
 * loading / success / error states exactly as required.
 */
object GeminiClient {

    private const val MODEL = "gemini-2.5-flash"
    private const val BASE_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()

    /** Data class returned to the UI: we expose both prompt and response (brief requires this). */
    data class PromptResponse(val prompt: String, val response: String)

    private fun apiKey(): String = BuildConfig.GEMINI_API_KEY

    private fun isApiKeyValid(): Boolean {
        val k = apiKey()
        return k.isNotBlank() && k != "PASTE_YOUR_API_KEY_HERE"
    }

    // ========== LLM FEATURE #1: Generate Hint for a question ==========
    suspend fun generateHint(
        questionPrompt: String,
        options: List<String>,
        topic: String
    ): LlmResult<PromptResponse> {
        val prompt = buildString {
            append("You are a helpful tutor for a computer science student studying ")
            append(topic)
            append(".\n\n")
            append("Give ONE short hint (max 2 sentences) for the following multiple-choice question. ")
            append("Do NOT reveal the answer - only nudge the student toward the right way to think about it.\n\n")
            append("Question: ").append(questionPrompt).append("\n")
            append("Options:\n")
            options.forEachIndexed { i, o -> append("  ${i + 1}. ").append(o).append("\n") }
            append("\nHint:")
        }
        return callGemini(prompt)
    }

    // ========== LLM FEATURE #2: Explain Answer (why correct / why incorrect) ==========
    suspend fun explainAnswer(
        questionPrompt: String,
        options: List<String>,
        pickedIndex: Int,
        correctIndex: Int,
        topic: String
    ): LlmResult<PromptResponse> {
        val picked = options.getOrNull(pickedIndex) ?: "(no answer)"
        val correct = options.getOrNull(correctIndex) ?: "(unknown)"
        val isCorrect = pickedIndex == correctIndex

        val prompt = buildString {
            append("You are a tutor helping a student learning ").append(topic).append(".\n\n")
            append("The student answered this multiple-choice question:\n")
            append("Question: ").append(questionPrompt).append("\n")
            append("Options:\n")
            options.forEachIndexed { i, o -> append("  ${i + 1}. ").append(o).append("\n") }
            append("\nThe student chose: \"").append(picked).append("\".\n")
            append("The correct answer is: \"").append(correct).append("\".\n")
            if (isCorrect) {
                append("\nIn 2-3 short sentences, explain WHY this answer is correct. ")
                append("Be encouraging. Plain text only, no markdown.")
            } else {
                append("\nIn 2-3 short sentences, explain WHY the student's choice is incorrect ")
                append("and why the correct answer is right. Be kind and encouraging. Plain text only, no markdown.")
            }
        }
        return callGemini(prompt)
    }

    /** Shared HTTP + JSON plumbing for Gemini. */
    private suspend fun callGemini(prompt: String): LlmResult<PromptResponse> =
        withContext(Dispatchers.IO) {
            if (!isApiKeyValid()) {
                return@withContext LlmResult.Error(
                    "Gemini API key is not set. Open app/build.gradle.kts and paste your key " +
                        "into GEMINI_API_KEY. Get a free key at https://aistudio.google.com/apikey."
                )
            }

            try {
                // Build request body:
                // { "contents": [ { "parts": [ { "text": "<prompt>" } ] } ] }
                val part = JSONObject().put("text", prompt)
                val parts = JSONArray().put(part)
                val content = JSONObject().put("parts", parts)
                val contents = JSONArray().put(content)
                val body = JSONObject().put("contents", contents).toString()

                val url = "$BASE_URL?key=${apiKey()}"
                val request = Request.Builder()
                    .url(url)
                    .post(body.toRequestBody(JSON_MEDIA))
                    .addHeader("Content-Type", "application/json")
                    .build()

                client.newCall(request).execute().use { response ->
                    val raw = response.body?.string().orEmpty()
                    if (!response.isSuccessful) {
                        return@withContext LlmResult.Error(
                            "Gemini API error ${response.code}: ${raw.take(200)}"
                        )
                    }

                    // Parse: candidates[0].content.parts[0].text
                    val json = JSONObject(raw)
                    val candidates = json.optJSONArray("candidates")
                    if (candidates == null || candidates.length() == 0) {
                        return@withContext LlmResult.Error("Gemini returned no candidates.")
                    }
                    val first = candidates.getJSONObject(0)
                    val contentObj = first.optJSONObject("content")
                        ?: return@withContext LlmResult.Error("Malformed Gemini response.")
                    val partsArr = contentObj.optJSONArray("parts")
                        ?: return@withContext LlmResult.Error("Malformed Gemini response.")
                    if (partsArr.length() == 0) {
                        return@withContext LlmResult.Error("Empty response from Gemini.")
                    }
                    val text = partsArr.getJSONObject(0).optString("text", "").trim()
                    if (text.isEmpty()) {
                        return@withContext LlmResult.Error("Gemini returned empty text.")
                    }
                    LlmResult.Success(PromptResponse(prompt = prompt, response = text))
                }
            } catch (e: Exception) {
                LlmResult.Error("Network error: ${e.localizedMessage ?: "unknown"}")
            }
        }
}
