# LLM-Enhanced Learning Assistant App

**SIT305 — Task 6.1D (HD)**
**Author:** Thuy Tien NGO — Student ID **224899442**

An Android app that uses a Large Language Model (Google Gemini) to help students
learn. Users sign up, pick their topics of interest, and are given personalised
practice quizzes. At any point they can tap **"💡 Get Hint"** to receive an
AI-generated hint for a question, and after submitting the quiz the
**Your Results** screen automatically asks the AI to explain *why* each answer
is right or wrong.

The app satisfies the task brief's requirement of **at least two LLM-powered
learning utilities** integrated into the existing screens (not just a chat).

---

## ✨ Features

### Screens (all six required by the brief)
1. **Welcome / Login** — `LoginActivity`
2. **Sign Up / Setup** — `SignUpActivity`
3. **Interests** — `InterestsActivity` (up to 10 topics)
4. **Home** — `HomeActivity` (personalised list of generated tasks)
5. **Task Detail / Quiz** — `TaskDetailActivity` (multiple choice + Hint button)
6. **Your Results** — `ResultsActivity` (per-question AI explanation)

A short **SplashActivity** routes returning users straight to Home.

### Two LLM-Powered Learning Utilities
| # | Feature | Where it lives | What it does |
|---|---------|----------------|--------------|
| 1 | **Generate Hint for a question** | `TaskDetailActivity` → "💡 Get Hint" button | Asks Gemini for a one-to-two-sentence hint for the active question *without* revealing the answer. |
| 2 | **Explain why an answer is correct/incorrect** | `ResultsActivity` (one call per question) | For each question on the Results screen, asks Gemini to explain, in 2–3 sentences, why the student's answer was right or wrong. |

Both features:
- **Show the prompt *and* the response in the UI** (as required by the brief)
- Handle **loading**, **success**, and **failure / retry** states

### Personalisation
Tasks shown on Home are filtered by the topics the user ticked on the Interests
screen during sign-up. This directly implements the brief's requirement that
generated experiences are *"based on the student's initial account setup"*.

### Polish
- Fade/slide entry animations on every screen
- RecyclerView layout animation ("fall down") for lists
- Consistent Back navigation; every screen has a working Back button
- Content descriptions on every interactive element for accessibility
- Readable font sizes (minimum 13 sp for body text)

---

## 🛠️ Tech Stack

| Layer | Choice |
|-------|--------|
| Language | Kotlin |
| UI | XML Views + ViewBinding |
| Min / Target SDK | 24 / 34 |
| LLM | Google Gemini (`gemini-1.5-flash-latest`) via REST |
| HTTP | OkHttp 4 |
| Concurrency | Kotlin coroutines + `lifecycleScope` |
| Storage | SharedPreferences (local; dummy data, as permitted by the brief) |
| Layout helper | Google Flexbox (for the interests chip grid) |

No backend server is used. The task brief explicitly allows dummy/local data,
and allows (but does not require) the unit team's backend code. This project
calls Gemini directly from the device.

---

## 🚀 Setup (5 minutes)

### 1. Clone
```bash
git clone https://github.com/ttbellie/<this-repo>.git
```

### 2. Get a free Gemini API key
1. Go to <https://aistudio.google.com/apikey>
2. Sign in with a Google account
3. Click **Create API key** → copy the value

### 3. Paste the key into `app/build.gradle.kts`
Open `app/build.gradle.kts` and find this line:

```kotlin
buildConfigField("String", "GEMINI_API_KEY", "\"PASTE_YOUR_API_KEY_HERE\"")
```

Replace `PASTE_YOUR_API_KEY_HERE` with the key you just copied, keeping the
escaped quotes intact.

### 4. Open and run
1. Open the project in **Android Studio Panda 2 (2025.3.2)** or newer
2. Let Gradle sync (first sync will download dependencies)
3. Run on an emulator or physical device (API 24+)

The first launch shows the Sign-Up → Interests flow, then Home.

---

## 📱 How to use

1. **Sign up** with any username / email / password (stored locally only)
2. On the **Interests** screen, tap up to 10 topics, then **Next**
3. On **Home**, tap a generated task card
4. Inside a quiz, pick an answer for each question. Tap **💡 Get Hint** on
   any question for an AI-generated hint
5. Tap **Submit** — the **Your Results** screen shows the AI explaining each
   of your answers
6. Tap the profile avatar on Home to sign out

---

## 🗂️ Project layout

```
app/src/main/
├── AndroidManifest.xml
├── java/com/example/llmlearningassistant/
│   ├── data/
│   │   ├── DummyData.kt          ← sample topics + 5 quizzes
│   │   ├── LearningTask.kt
│   │   ├── PrefsManager.kt       ← SharedPreferences wrapper
│   │   ├── Question.kt
│   │   └── User.kt
│   ├── network/
│   │   ├── GeminiClient.kt       ← ★ both LLM features live here
│   │   └── LlmResult.kt
│   └── ui/
│       ├── splash/SplashActivity.kt
│       ├── login/LoginActivity.kt
│       ├── signup/SignUpActivity.kt
│       ├── interests/InterestsActivity.kt
│       ├── home/{HomeActivity.kt, TaskAdapter.kt}
│       ├── task/TaskDetailActivity.kt   ← ★ LLM feature #1 (Hint)
│       └── results/{ResultsActivity.kt, ResultsAdapter.kt}  ← ★ LLM feature #2
└── res/
    ├── layout/      (8 screen layouts + 4 item layouts + 1 dialog)
    ├── drawable/    (shape backgrounds, launcher vector)
    ├── anim/        (screen + RecyclerView animations)
    ├── values/      (colors, strings, themes)
    └── mipmap-*/    (launcher icons)
```

---

## 🔒 LLM Declaration

A Large Language Model (Claude, by Anthropic) was used while preparing this
submission. The parts influenced by AI are declared in the accompanying
submission PDF (`HD_Task 6.1_Thuy Tien NGO_224899442.pdf`) and the chat
history is linked there as well.

---

## 📝 License

Submitted for academic assessment (Deakin University, SIT305). Not licensed for
redistribution.
