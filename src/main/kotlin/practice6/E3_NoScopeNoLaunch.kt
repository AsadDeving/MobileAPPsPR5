package practice6

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Extra 3. No scope, no launch
// Broken version: see compile-errors/E3_Broken.kt
//
// fun main() {
//     launch { println("hello") }   // <-- compile error, no CoroutineScope receiver
// }

// Fix: runBlocking (the builder from the first lecture example) gives us a CoroutineScope
fun main() = runBlocking {
    println("runBlocking runs on: ${Thread.currentThread().name}")
    launch {
        delay(100L)
        println("hello from launch")
    }
    println("main body done, runBlocking still waits for its child")
}
