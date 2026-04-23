package com.example.llmlearningassistant.data

/**
 * In-memory source of sample tasks and the list of selectable interest topics.
 * Task brief explicitly allows dummy data - no backend is wired in.
 */
object DummyData {

    /** List of available topics shown on the Interests screen. */
    val AVAILABLE_TOPICS: List<String> = listOf(
        "Algorithms",
        "Data Structures",
        "Web Development",
        "Testing",
        "Databases",
        "Operating Systems",
        "Machine Learning",
        "Mobile Development",
        "Cybersecurity",
        "Cloud Computing",
        "Computer Networks",
        "Software Engineering"
    )

    /**
     * Returns a list of learning tasks. If the user has selected interests,
     * only tasks whose topic matches one of those interests are returned.
     * This is the "personalization" based on initial account setup (task brief req).
     */
    fun getTasksForInterests(interests: List<String>): List<LearningTask> {
        val all = allTasks()
        if (interests.isEmpty()) return all
        val filtered = all.filter { it.topic in interests }
        // Always surface something even if the user picked niche topics we don't have quizzes for
        return filtered.ifEmpty { all.take(2) }
    }

    private fun allTasks(): List<LearningTask> = listOf(
        LearningTask(
            id = 1,
            title = "Generated Task 1",
            description = "Small Description for the generated Task",
            topic = "Algorithms",
            questions = listOf(
                Question(
                    id = 101,
                    prompt = "What is the time complexity of binary search on a sorted array?",
                    options = listOf("O(n)", "O(log n)", "O(n log n)"),
                    correctIndex = 1,
                    topic = "Algorithms"
                ),
                Question(
                    id = 102,
                    prompt = "Which sorting algorithm has the best average-case performance among these?",
                    options = listOf("Bubble sort", "Insertion sort", "Merge sort"),
                    correctIndex = 2,
                    topic = "Algorithms"
                ),
                Question(
                    id = 103,
                    prompt = "Which data structure is most commonly used to implement BFS?",
                    options = listOf("Stack", "Queue", "Priority queue"),
                    correctIndex = 1,
                    topic = "Algorithms"
                )
            )
        ),
        LearningTask(
            id = 2,
            title = "Generated Task 2",
            description = "Small Description for the generated Task",
            topic = "Data Structures",
            questions = listOf(
                Question(
                    id = 201,
                    prompt = "Which data structure uses LIFO order?",
                    options = listOf("Queue", "Stack", "Linked List"),
                    correctIndex = 1,
                    topic = "Data Structures"
                ),
                Question(
                    id = 202,
                    prompt = "What is the average-case lookup time of a hash table?",
                    options = listOf("O(1)", "O(log n)", "O(n)"),
                    correctIndex = 0,
                    topic = "Data Structures"
                ),
                Question(
                    id = 203,
                    prompt = "A binary heap is typically stored using which underlying structure?",
                    options = listOf("Linked list", "Array", "Tree of pointers"),
                    correctIndex = 1,
                    topic = "Data Structures"
                )
            )
        ),
        LearningTask(
            id = 3,
            title = "Generated Task 3",
            description = "Small Description for the generated Task",
            topic = "Web Development",
            questions = listOf(
                Question(
                    id = 301,
                    prompt = "Which HTTP status code means 'Not Found'?",
                    options = listOf("200", "301", "404"),
                    correctIndex = 2,
                    topic = "Web Development"
                ),
                Question(
                    id = 302,
                    prompt = "Which language runs natively in the browser?",
                    options = listOf("Python", "JavaScript", "Ruby"),
                    correctIndex = 1,
                    topic = "Web Development"
                ),
                Question(
                    id = 303,
                    prompt = "What does CSS primarily control?",
                    options = listOf("Server logic", "Page styling", "Database schema"),
                    correctIndex = 1,
                    topic = "Web Development"
                )
            )
        ),
        LearningTask(
            id = 4,
            title = "Generated Task 4",
            description = "Small Description for the generated Task",
            topic = "Testing",
            questions = listOf(
                Question(
                    id = 401,
                    prompt = "What does a unit test primarily verify?",
                    options = listOf("End-to-end user flow", "A single small piece of logic", "Network performance"),
                    correctIndex = 1,
                    topic = "Testing"
                ),
                Question(
                    id = 402,
                    prompt = "What is a test double used for?",
                    options = listOf("Stand in for a real dependency", "Double the test speed", "Run tests twice"),
                    correctIndex = 0,
                    topic = "Testing"
                ),
                Question(
                    id = 403,
                    prompt = "Which of these is a characteristic of a good test?",
                    options = listOf("Depends on other tests", "Fast and deterministic", "Hits the real database"),
                    correctIndex = 1,
                    topic = "Testing"
                )
            )
        ),
        LearningTask(
            id = 5,
            title = "Generated Task 5",
            description = "Small Description for the generated Task",
            topic = "Databases",
            questions = listOf(
                Question(
                    id = 501,
                    prompt = "What does SQL stand for?",
                    options = listOf("Structured Query Language", "Simple Query Logic", "Server Quick List"),
                    correctIndex = 0,
                    topic = "Databases"
                ),
                Question(
                    id = 502,
                    prompt = "Which clause filters rows in a SELECT query?",
                    options = listOf("GROUP BY", "WHERE", "ORDER BY"),
                    correctIndex = 1,
                    topic = "Databases"
                ),
                Question(
                    id = 503,
                    prompt = "What guarantees that a transaction is all-or-nothing?",
                    options = listOf("Consistency", "Atomicity", "Durability"),
                    correctIndex = 1,
                    topic = "Databases"
                )
            )
        )
    )
}
