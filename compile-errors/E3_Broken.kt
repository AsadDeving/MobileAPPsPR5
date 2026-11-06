// NOT part of the build. Copy into src/main/kotlin/practice6 to reproduce the Extra 3 error.
package practice6

import kotlinx.coroutines.launch

fun brokenMain() {
    launch { println("hello") }   // ERROR: launch needs a CoroutineScope receiver
}
