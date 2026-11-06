package practice6

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 13. Builders: launch, async, runBlocking, coroutineScope (once each)
fun main() {
    val fromRunBlocking: String = runBlocking {
        val job = launch { delay(50L) }
        println("launch returned: ${job::class.simpleName}, is Job -> ${job is kotlinx.coroutines.Job}")

        val deferred = async { delay(50L); 10 }
        println("async returned: ${deferred::class.simpleName}, is Deferred -> ${deferred is kotlinx.coroutines.Deferred<*>}, await() = ${deferred.await()}")

        val fromScope: Int = coroutineScope {
            launch { delay(50L) }
            21 * 2
        }
        println("coroutineScope returned the lambda result: $fromScope (${fromScope::class.simpleName})")

        "value of the last expression"
    }
    println("runBlocking returned the lambda result: \"$fromRunBlocking\" (${fromRunBlocking::class.simpleName})")
}
