package practice6

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 1. First coroutine
// The order must be confirmed at once; the coffee takes 1 second.
fun main() = runBlocking {
    launch {
        delay(1000L)            // suspends this coroutine, does NOT block the thread
        println("Coffee ready")
    }
    println("Order accepted")   // runs immediately: launch only schedules the coroutine
}
