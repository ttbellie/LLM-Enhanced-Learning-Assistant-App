package com.example.llmlearningassistant.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray

/**
 * Thin wrapper around SharedPreferences for storing the signed-in user locally.
 * No backend involved - the task brief explicitly allows dummy / local data.
 */
class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        prefs.edit().apply {
            putString(KEY_USERNAME, user.username)
            putString(KEY_EMAIL, user.email)
            putString(KEY_PHONE, user.phone)
            putString(KEY_PASSWORD, user.password)
            putString(KEY_INTERESTS, JSONArray(user.interests).toString())
            putBoolean(KEY_LOGGED_IN, true)
            apply()
        }
    }

    fun updateInterests(interests: List<String>) {
        prefs.edit().putString(KEY_INTERESTS, JSONArray(interests).toString()).apply()
    }

    fun getUser(): User? {
        val username = prefs.getString(KEY_USERNAME, null) ?: return null
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        val phone = prefs.getString(KEY_PHONE, "") ?: ""
        val password = prefs.getString(KEY_PASSWORD, "") ?: ""
        val interestsJson = prefs.getString(KEY_INTERESTS, "[]") ?: "[]"

        val list = mutableListOf<String>()
        try {
            val arr = JSONArray(interestsJson)
            for (i in 0 until arr.length()) list.add(arr.getString(i))
        } catch (_: Exception) { /* ignore malformed */ }

        return User(username, email, phone, password, list)
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)

    fun setLoggedIn(value: Boolean) {
        prefs.edit().putBoolean(KEY_LOGGED_IN, value).apply()
    }

    fun logout() {
        prefs.edit().putBoolean(KEY_LOGGED_IN, false).apply()
    }

    /**
     * For login: validate credentials against the single stored account.
     * Not a real auth system - just enough for the dummy-data demo.
     */
    fun validateCredentials(username: String, password: String): Boolean {
        val savedU = prefs.getString(KEY_USERNAME, null)
        val savedP = prefs.getString(KEY_PASSWORD, null)
        return savedU != null && savedU == username && savedP == password
    }

    companion object {
        private const val PREF_NAME = "llm_assistant_prefs"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phone"
        private const val KEY_PASSWORD = "password"
        private const val KEY_INTERESTS = "interests"
        private const val KEY_LOGGED_IN = "logged_in"
    }
}
