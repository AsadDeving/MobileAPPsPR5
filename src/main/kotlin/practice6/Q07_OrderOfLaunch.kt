package practice6

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 7. Order of launch
// Prediction (written BEFORE running): 1, 4, 2, 5, 3
fun main() = runBlocking {
    println("1")
    launch {
        println("2")
        delay(100L)
        println("3")
    }
    println("4")
    delay(50L)
    println("5")
}
