package practice6

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// Question 5. Suspend is not a thread
suspend fun whereAmI(): String {
    delay(100L) // even after a real suspension we come back to the same thread
    return Thread.currentThread().name
}

fun main() = runBlocking {
    println("thread before the call: ${Thread.currentThread().name}")
    val inside = whereAmI()
    println("thread inside the suspend function: $inside")
    println("same thread? ${inside == Thread.currentThread().name}")
}
