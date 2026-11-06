package practice6

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// Extra 4. What the compiler sees
data class User(val id: Int, val name: String)

suspend fun loadUserById(id: Int): User {
    delay(100L)
    return User(id, "user-$id")
}

fun main() = runBlocking {
    println(loadUserById(42))
}
