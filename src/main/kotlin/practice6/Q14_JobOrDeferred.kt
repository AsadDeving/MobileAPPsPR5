package practice6

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Question 14. Job or Deferred
fun main() = runBlocking {
    // launch version: the lambda result is thrown away, Job has no value
    val job: Job = launch {
        val result = 6 * 7
        println("inside launch the result is $result, but it cannot leave the coroutine")
    }
    val fromJoin: Unit = job.join()          // join() returns Unit, never the value
    println("launch -> job.join() returned: $fromJoin")
    // job.await()  <-- does not compile: Job has no await()

    // async version: Deferred<Int> carries the value
    val deferred: Deferred<Int> = async { 6 * 7 }
    println("async -> deferred.await() returned: ${deferred.await()}")
}
