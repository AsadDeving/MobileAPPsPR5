package practice6

import kotlinx.coroutines.delay

// Question 3. Chain of suspend functions: load user -> check password -> open profile
suspend fun loadUserStep() {
    println("1. load user: started")
    delay(300L)
    println("1. load user: done")
    checkPasswordStep()
}

suspend fun checkPasswordStep() {
    println("2. check password: started")
    delay(200L)
    println("2. check password: done")
    openProfileStep()
}

suspend fun openProfileStep() {
    println("3. open profile: started")
    delay(100L)
    println("3. open profile: done")
}

// suspend main: the compiler wraps it into a coroutine for us
suspend fun main() {
    println("login flow begins")
    loadUserStep()
    println("login flow finished")
}
