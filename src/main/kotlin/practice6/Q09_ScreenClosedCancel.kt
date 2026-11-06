package practice6

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 9. Screen closed, requests cancelled
fun main() = runBlocking {
    val screen = launch {
        launch {
            try {
                println("request A: started")
                delay(1000L)
                println("request A: finished")      // never printed
            } finally {
                println("request A: cleanup")
            }
        }
        launch {
            try {
                println("request B: started")
                delay(1000L)
                println("request B: finished")      // never printed
            } finally {
                println("request B: cleanup")
            }
        }
    }
    delay(100L)
    println("user closes the screen")
    screen.cancelAndJoin()
    println("end")
}
