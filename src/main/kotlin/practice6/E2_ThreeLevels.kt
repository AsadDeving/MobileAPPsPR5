package practice6

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Extra 2. Three levels of coroutines
fun main() = runBlocking {
    coroutineScope {
        launch {                                // child 1
            launch {                            // grandchild
                delay(200L)
                println("grandchild done")
            }
            println("child 1 done")             // body ends, but child 1 completes after grandchild
        }
        launch {                                // child 2
            delay(100L)
            println("child 2 done")
        }
    }
    println("scope done")
}
