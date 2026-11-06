package practice6

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToLong
import kotlin.system.measureTimeMillis

// Extra 1. Delay or sleep: measure it
private fun Long.toHundreds(): Long = (this / 100.0).roundToLong() * 100

fun main() = runBlocking {
    val withDelay = measureTimeMillis {
        coroutineScope {
            repeat(3) { launch { delay(300L) } }
        }
    }
    val withSleep = measureTimeMillis {
        coroutineScope {
            repeat(3) { launch { Thread.sleep(300L) } }
        }
    }
    println("3 x delay(300)       took ~${withDelay.toHundreds()} ms (raw $withDelay ms)")
    println("3 x Thread.sleep(300) took ~${withSleep.toHundreds()} ms (raw $withSleep ms)")
}
