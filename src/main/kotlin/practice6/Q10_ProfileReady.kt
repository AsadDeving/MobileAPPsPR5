package practice6

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 10. Profile ready
fun main() = runBlocking {
    // Case 1: coroutineScope suspends until BOTH children are finished
    coroutineScope {
        launch {
            delay(200L)
            println("avatar loaded")
        }
        launch {
            delay(100L)
            println("settings loaded")
        }
    }
    println("Profile ready")

    // Case 2: a plain launch after the scope returns immediately
    launch {
        delay(100L)
        println("late coroutine done")
    }
    println("end of main")
}
