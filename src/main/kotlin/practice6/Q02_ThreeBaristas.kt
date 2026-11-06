package practice6

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 2. Three baristas on ONE thread (runBlocking = single main thread)
private val drinks = listOf("espresso" to 300L, "tea" to 100L, "latte" to 200L)

fun main() {
    println("--- with delay (suspension) ---")
    runBlocking {
        for ((drink, ms) in drinks) {
            launch {
                println("$drink start")
                delay(ms)                 // thread is released for other baristas
                println("$drink end")
            }
        }
    }

    println("--- with Thread.sleep (blocking) ---")
    runBlocking {
        for ((drink, ms) in drinks) {
            launch {
                println("$drink start")
                Thread.sleep(ms)          // the only thread is frozen, nobody else can work
                println("$drink end")
            }
        }
    }
}
