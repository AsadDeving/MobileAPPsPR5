// NOT part of the build. Copy into src/main/kotlin/practice6 to reproduce the Question 4 error.
package practice6

import kotlinx.coroutines.delay

suspend fun loadUserBroken(): String {
    delay(200L)
    return "User(id=7)"
}

fun showUserBroken() {
    val user = loadUserBroken()   // ERROR: suspend call from a normal function
    println(user)
}
