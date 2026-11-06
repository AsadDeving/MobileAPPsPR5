package practice6

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 12. Returns at once or waits

// Helper 1: fire-and-forget, returns immediately (convention: extension on CoroutineScope)
fun CoroutineScope.startGreeter(name: String): Job = launch {
    delay(300L)
    println("background greeter: Hello, $name")
}

// Helper 2: suspend + coroutineScope, returns only when ALL greetings are printed
suspend fun greetEveryone(names: List<String>) = coroutineScope {
    for (name in names) {
        launch {
            delay(100L)
            println("Hello, $name")
        }
    }
}

fun main() = runBlocking {
    startGreeter("Team")
    println("after startGreeter -> returned at once")

    greetEveryone(listOf("Ada", "Linus", "Grace"))
    println("after greetEveryone -> returned after all greetings")
}
