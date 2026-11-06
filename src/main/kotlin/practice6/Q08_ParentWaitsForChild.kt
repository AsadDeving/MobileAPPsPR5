package practice6

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 8. Parent waits for child
fun main() = runBlocking {
    val report = launch {
        launch {
            delay(200L)                 // compress attachments
            println("child done")
        }
        println("parent body done")     // body ends early, but the Job is NOT complete yet
    }
    report.join()                       // completes only when the child is done too
    println("report completed")
}
