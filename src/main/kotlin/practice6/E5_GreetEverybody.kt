package practice6

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Extra 5. Greet everybody
suspend fun greetAll(names: List<String>) = coroutineScope {
    names.forEachIndexed { index, name ->
        val i = index + 1
        launch {
            delay(i * 100L)
            println("Hello, $name")
        }
    }
}

fun main() = runBlocking {
    println("before")
    greetAll(listOf("Ada", "Linus", "Grace"))
    println("after")
}
