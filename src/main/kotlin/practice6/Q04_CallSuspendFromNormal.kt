package practice6

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// Question 4. Calling suspend from normal code
suspend fun loadUserFromServer(): String {
    delay(200L)
    return "User(id=7, name=Asad)"
}

// BROKEN version (see compile-errors/Q04_Broken.kt):
//
// fun showUser() {
//     val user = loadUserFromServer()   // <-- compile error
//     println(user)
// }

// Fix 1: make the caller a suspend function too (preferred everywhere).
suspend fun showUserSuspend() {
    val user = loadUserFromServer()
    println("fix 1 (suspend caller): $user")
}

// Fix 2: build a bridge with runBlocking (blocks the current thread until done).
// NEVER do this on the Android UI thread.
fun showUserBlocking() {
    val user = runBlocking { loadUserFromServer() }
    println("fix 2 (runBlocking bridge): $user")
}

fun main() {
    runBlocking { showUserSuspend() }   // fix 1 needs a coroutine at the very top
    showUserBlocking()                  // fix 2 is callable from plain code
}
