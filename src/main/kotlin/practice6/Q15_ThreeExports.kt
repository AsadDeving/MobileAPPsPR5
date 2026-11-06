package practice6

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 15. Three exports in one scope: no join, no delay in the main body
fun main() = runBlocking {
    println("Start")
    coroutineScope {
        launch { delay(300L); println("X") }
        launch { delay(100L); println("Y") }
        launch { delay(200L); println("Z") }
    }
    println("All done")
}
