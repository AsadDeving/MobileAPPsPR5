package practice6

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 11. Reusable counter
// Extension on CoroutineScope: returns at once, the new coroutine is a child of the caller scope.
fun CoroutineScope.startCounter(name: String, upTo: Int): Job = launch {
    for (i in 1..upTo) {
        delay(100L)
        println("$name: $i")
    }
}

fun main() = runBlocking {
    coroutineScope {
        val a = startCounter("counter A", 3)
        val b = startCounter("counter B", 5)
        println("launched (A active=${a.isActive}, B active=${b.isActive})")
    }
    println("both counters finished")
}
