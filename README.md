# LLM-Enhanced Learning Assistant App

## Overview
This project is an Android application developed for **SIT305 Task 6.1D**.  
The app is designed to support student learning by combining a simple learning task system with **LLM-powered features** using the **Google Gemini API**.

The app provides a more personalised learning experience based on the user’s selected interests. It also includes AI-supported learning tools inside the existing screens, not just a general chat function.

---

## Features

### Core App Features
- User login / sign up flow
- Interest selection during account setup
- Personalised Home screen with recommended learning tasks
- Task detail screen with multiple-choice questions
- Result screen showing the user’s answer and the correct answer
- Dummy data used to populate tasks and topics

### LLM-Powered Learning Features
- **Generate Hint**
  - Gives one short hint for the question
  - Does not reveal the correct answer directly
  - Helps guide the student toward the correct way of thinking

- **Explain Why Answer is Correct/Incorrect**
  - Explains whether the selected answer is right or wrong
  - Uses a short and encouraging explanation
  - Focuses on the computer science concept behind the question

### UI and Usability
- Loading states while waiting for Gemini API responses
- Failure states with retry option
- Prompt and response displayed in the UI
- Clear button labels and readable text
- Back navigation works across screens

---

## Technologies Used
- **Kotlin**
- **Android Studio**
- **OkHttp**
- **Kotlin Coroutines**
- **Google Gemini REST API**
- **ViewModel / StateFlow** (if used in your project)
- **Material Design components** (if used)

---

## Project Structure
Example structure of the main app files:

- `MainActivity.kt`
- `LoginActivity.kt`
- `SignupActivity.kt`
- `InterestsActivity.kt`
- `HomeActivity.kt`
- `TaskDetailActivity.kt`
- `ResultActivity.kt`
- `GeminiApiService.kt`
- `HintViewModel.kt` / `ResultViewModel.kt`
- `res/layout/...`

You can edit this section if your file names are different.

---

## How the Personalisation Works
The app uses the user’s selected interests to make the Home screen feel more personalised.

- Tasks that exactly match the selected interests are shown first
- Related or general computer science tasks are shown next
- If there are no exact matches, fallback tasks are still shown so the screen is never empty

This approach helps the app feel personalised while still remaining usable.

---

## How the Gemini API is Used
The app sends a prompt to the Gemini API and receives a text response for learning support.

Two main prompts are used:

1. **Generate Hint**
   - Input: question text, options, topic
   - Output: one short hint, maximum 2 sentences

2. **Explain Correct/Incorrect**
   - Input: question text, options, student answer, correct answer, topic
   - Output: short explanation, 2–3 sentences

The app handles three UI states for each API request:
- **Loading**
- **Success**
- **Error**

---

## API Key Notice
For security reasons, the real API key is **not included** in this public repository.

Please add your own Gemini API key in `BuildConfig` before running the app.

Example:

```kotlin
buildConfigField("String", "GEMINI_API_KEY", "\"YOUR_API_KEY_HERE\"")
