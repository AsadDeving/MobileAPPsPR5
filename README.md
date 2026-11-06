# Practice 6 — Coroutines introduction

Kotlin/JVM · `kotlinx-coroutines-core 1.10.2` · Kotlin 2.1.21 · JDK 21

Every question is a separate file with its own `main` in
[`src/main/kotlin/practice6`](src/main/kotlin/practice6).
Code that does **not** compile (needed for Question 4 and Extra 3) lives in
[`compile-errors/`](compile-errors) so the project still builds.

## How to run

Open the folder in IntelliJ IDEA and press ▶ next to any `main`, or use Gradle:

```bash
./gradlew run -q -Pq=Q07
```

`-Pq` takes the file prefix: `Q01` … `Q15`, `E1` … `E5`.

| File | Question |
|---|---|
| `Q01_FirstCoroutine.kt` | 1. First coroutine |
| `Q02_ThreeBaristas.kt` | 2. Three baristas |
| `Q03_SuspendChain.kt` | 3. Chain of suspend functions |
| `Q04_CallSuspendFromNormal.kt` | 4. Calling suspend from normal code |
| `Q05_SuspendIsNotAThread.kt` | 5. Suspend is not a thread |
| `Q06_TenDownloadsOneThread.kt` | 6. Ten downloads, one thread |
| `Q07_OrderOfLaunch.kt` | 7. Order of launch |
| `Q08_ParentWaitsForChild.kt` | 8. Parent waits for child |
| `Q09_ScreenClosedCancel.kt` | 9. Screen closed, requests cancelled |
| `Q10_ProfileReady.kt` | 10. Profile ready |
| `Q11_ReusableCounter.kt` | 11. Reusable counter |
| `Q12_ReturnsAtOnceOrWaits.kt` | 12. Returns at once or waits |
| `Q13_FiveBuilders.kt` | 13. Five builders |
| `Q14_JobOrDeferred.kt` | 14. Job or Deferred |
| `Q15_ThreeExports.kt` | 15. Three exports in a scope |
| `E1_DelayOrSleepMeasure.kt` | Extra 1. Delay or sleep: measure it |
| `E2_ThreeLevels.kt` | Extra 2. Three levels of coroutines |
| `E3_NoScopeNoLaunch.kt` | Extra 3. No scope, no launch |
| `E4_WhatTheCompilerSees.kt` | Extra 4. What the compiler sees |
| `E5_GreetEverybody.kt` | Extra 5. Greet everybody |

---

## Suspending vs blocking (my explanation)

```text
BLOCKING (Thread.sleep, runBlocking, heavy loop, blocking I/O)
  - The THREAD itself stops and waits.
  - While it waits it cannot run anything else: other coroutines on it,
    UI drawing, touch events... all stand still.
  - 3 x Thread.sleep(300) on one thread = 900 ms, one after another.
  - On Android's main thread that means a frozen UI and, after ~5 s, an ANR.

SUSPENDING (delay, await, join, withContext, any suspend fun call)
  - Only the COROUTINE pauses. The compiler turned the function into a
    state machine; at a suspension point it saves its local variables and
    the "where to continue" label into a Continuation object and returns
    COROUTINE_SUSPENDED to its caller.
  - The thread is released immediately and the dispatcher gives it other
    work (other coroutines, UI events).
  - When the reason for waiting is over (timer fired, result arrived), the
    dispatcher calls continuation.resumeWith(...) and the coroutine goes on
    from the exact line where it stopped, usually on the same thread.
  - 3 x delay(300) in three coroutines on one thread = ~300 ms in total,
    because all three waits overlap.

Short version:
  blocking   = "the worker sits in front of the oven and does nothing else"
  suspending = "the worker sets a timer, serves other customers, and comes
                back to the oven when the timer rings"

`suspend` itself does NOT create a thread and does NOT move work to the
background (see Question 5). It only means "this function is allowed to
pause without blocking". Switching threads is done explicitly with
withContext(Dispatchers.IO / Default).
```

---

## Question 1. First coroutine

**Output**

```text
Order accepted
Coffee ready
```

**Why "Order accepted" comes first:** `launch` doesn't run the coroutine on the spot. It only
schedules the coroutine on the `runBlocking` event loop and returns a `Job` straight away.
The main body keeps the single thread and prints "Order accepted". It then reaches the end of
its block, so the thread is free and the launched coroutine starts. That coroutine suspends in
`delay(1000)` and prints "Coffee ready" one second later. `runBlocking` doesn't finish until
its child is done.

## Question 2. Three baristas

**Output with `delay`**

```text
espresso start
tea start
latte start
tea end
latte end
espresso end
```

**Output with `Thread.sleep`**

```text
espresso start
espresso end
tea start
tea end
latte start
latte end
```

**What the thread does:** with `delay` the thread is released at every wait and runs the other
baristas, so the drinks finish in order of their duration (100, 200, 300 ms) after ~300 ms
total. With `Thread.sleep` the one thread is blocked inside each barista, so the drinks are made
strictly one after another (~600 ms total). The teammate is wrong: sleeping is not the same as
suspending.

## Question 3. Chain of suspend functions

**Output**

```text
login flow begins
1. load user: started
1. load user: done
2. check password: started
2. check password: done
3. open profile: started
3. open profile: done
login flow finished
```

`loadUserStep()` → `checkPasswordStep()` → `openProfileStep()` are called from `suspend fun main()`.
Each one suspends in `delay`, but the lines still come out in the order they appear in the code.
Inside one coroutine, code is **sequential by default**. Suspending pauses the coroutine and
later resumes it at the same place. It never reorders lines. Concurrency happens only when you
start more coroutines with `launch`/`async`.

## Question 4. Calling suspend from normal code

**Broken code** ([`compile-errors/Q04_Broken.kt`](compile-errors/Q04_Broken.kt)):

```kotlin
fun showUserBroken() {
    val user = loadUserBroken()   // suspend fun called from a normal fun
    println(user)
}
```

**Compiler error (Kotlin 2.1.21):**

```text
e: Q04_Broken.kt:12:16 Suspend function 'suspend fun loadUserBroken(): String' can only be called from a coroutine or another suspend function.
```

**Two fixes** (in `Q04_CallSuspendFromNormal.kt`):

1. Make the caller `suspend` as well: `suspend fun showUserSuspend() { ... }`.
2. Bridge into coroutines with `runBlocking { loadUserFromServer() }` inside the normal function.

**Output**

```text
fix 1 (suspend caller): User(id=7, name=Asad)
fix 2 (runBlocking bridge): User(id=7, name=Asad)
```

**Which fix never goes on the Android UI thread:** **`runBlocking`**. It *blocks* the thread
that calls it until the coroutine inside finishes. On the main thread this means no frame
drawing and no touch handling, so the UI freezes. If it lasts ~5 s the system shows an
**ANR** ("Application Not Responding"). On Android you call suspend functions from a coroutine
started in a lifecycle-aware scope, e.g. `viewModelScope.launch { ... }` or
`lifecycleScope.launch { ... }`. `runBlocking` is only for `main` functions and tests.

## Question 5. Suspend is not a thread

**Output**

```text
thread before the call: main
thread inside the suspend function: main
same thread? true
```

**Verdict: the belief is disproved.** The suspend function, even after a real suspension
(`delay`), runs and comes back on the **same `main` thread**. `suspend` only lets a function
pause without blocking. It doesn't change the thread. Only the *dispatcher* decides the
thread, e.g. `withContext(Dispatchers.IO)`.

**Hidden parameter:** the compiler adds a **`Continuation<T>`** parameter (named
`$completion`) to every suspend function. This is *Continuation-Passing Style* (CPS). See
Extra 4.

## Question 6. Ten downloads, one thread

**Output**

```text
download 1 started on main
download 2 started on main
download 3 started on main
download 4 started on main
download 5 started on main
download 6 started on main
download 7 started on main
download 8 started on main
download 9 started on main
download 10 started on main
download 1 finished
download 2 finished
download 3 finished
download 4 finished
download 5 finished
download 6 finished
download 7 finished
download 8 finished
download 9 finished
download 10 finished
```

All ten print "started" before any prints "finished", so all ten are in progress at the same
time.

**Concurrency or parallelism?** This is **concurrency, not parallelism.** Every download runs
on the one thread, `main`. At any single instant only one coroutine is executing code. Their
*lifetimes overlap* because each suspends in `delay` and hands the thread to the next one.
Parallelism would need several threads/cores executing at the same instant, e.g.
`Dispatchers.Default`.

## Question 7. Order of launch

**Prediction (written before running):** `1 4 2 5 3`

**Output**

```text
1
4
2
5
3
```

**Why "2" doesn't come right after "1":** `launch` only *schedules* the new coroutine
(the default `CoroutineStart.DEFAULT`) and returns at once. The `runBlocking` body still owns
the single thread, so it keeps going and prints "4". It gives the thread away only at its first
suspension point, `delay(50)`. Then the launched coroutine runs and prints "2", then suspends
for 100 ms. At 50 ms the main body resumes and prints "5". At 100 ms the child prints "3".

## Question 8. Parent waits for child

**Output**

```text
parent body done
child done
report completed
```

**Rule:** in **structured concurrency, a parent coroutine is not complete until all its
children are complete.** The parent's body finishes first and prints "parent body done".
Its `Job` then moves to the *completing* state and waits for the child. `report.join()` waits
for the parent's *completion*, not for the end of its body. So "report completed" comes only
after "child done".

## Question 9. Screen closed, requests cancelled

**Output**

```text
request A: started
request B: started
user closes the screen
request A: cleanup
request B: cleanup
end
```

**Why the children stop although only the parent was cancelled:** cancellation travels **down
the job hierarchy**. Both requests were launched inside the parent, so their `Job`s are children
of the parent's `Job`. When the parent is cancelled, it cancels every child. Each child is
suspended in `delay(1000)`, which is cancellable, so `delay` throws `CancellationException`. The
`finally` block runs the cleanup, and "finished" is never printed. `cancelAndJoin()` also waits
until the cleanups are done, so "end" is last.

## Question 10. Profile ready

**Output**

```text
settings loaded
avatar loaded
Profile ready
end of main
late coroutine done
```

**Difference between the two cases:**

* `coroutineScope { ... }` is a **suspend function that waits for all of its children**. The
  line after it ("Profile ready") runs only after both the avatar and the settings are loaded.
* `launch { ... }` after the scope **returns at once**. It just starts a child of `runBlocking`,
  so "end of main" is printed straight away. The late coroutine finishes 100 ms later, and
  `runBlocking` waits for it before the program exits.

## Question 11. Reusable counter

```kotlin
fun CoroutineScope.startCounter(name: String, upTo: Int): Job = launch {
    for (i in 1..upTo) { delay(100L); println("$name: $i") }
}
```

**Output**

```text
launched (A active=true, B active=true)
counter A: 1
counter B: 1
counter A: 2
counter B: 2
counter A: 3
counter B: 3
counter B: 4
counter B: 5
both counters finished
```

**Why the receiver is needed:** `launch` is itself an extension on `CoroutineScope`, so it can
only be called where a scope exists. Making `startCounter` an extension on `CoroutineScope`
gives the function that scope: `launch` inside it really means `this.launch`. That has two
effects:

1. The new coroutine becomes a **child of the caller's scope** (here `coroutineScope`). The
   scope waits for it, and cancelling the scope cancels the counter. Nothing leaks.
2. The function **returns the `Job` at once** and doesn't suspend, so "launched" is printed
   before any number. By convention, an extension on `CoroutineScope` means "starts work and
   returns immediately".

## Question 12. Returns at once or waits

* `fun CoroutineScope.startGreeter(name)` is an extension that launches a coroutine and
  **returns at once**.
* `suspend fun greetEveryone(names) = coroutineScope { ... }` **returns only after all
  greetings**.

**Output**

```text
after startGreeter -> returned at once
Hello, Ada
Hello, Linus
Hello, Grace
after greetEveryone -> returned after all greetings
background greeter: Hello, Team
```

"after startGreeter" is the first line, before any greeting, so `startGreeter` returned at
once. "after greetEveryone" comes only after Ada, Linus and Grace, so `greetEveryone` waited.
The background greeter (300 ms) is still a child of `runBlocking`, so it prints last. The
program waits for it before it exits.

## Question 13. Five builders

**Output**

```text
launch returned: StandaloneCoroutine, is Job -> true
async returned: DeferredCoroutine, is Deferred -> true, await() = 10
coroutineScope returned the lambda result: 42 (Int)
runBlocking returned the lambda result: "value of the last expression" (String)
```

**Cheat sheet**

| Builder | What it is | Returns | Blocks the thread? | Waits for its children before returning? |
|---|---|---|---|---|
| `launch` | extension on `CoroutineScope`, normal fun | `Job` (no result) | No | No, returns at once (the *scope* waits for it later) |
| `async` | extension on `CoroutineScope`, normal fun | `Deferred<T>` (get the value with `await()`) | No | No, returns at once; `await()` suspends until the result is ready |
| `runBlocking` | normal (non-suspend) fun, bridge from plain code | `T`, the lambda's last expression | **Yes**, blocks the current thread | Yes |
| `coroutineScope` | `suspend` fun | `T`, the lambda's last expression | No, it suspends | Yes |
| `withContext(ctx)` | `suspend` fun, switches dispatcher/context | `T`, the lambda's last expression | No, it suspends | Yes |

Rule of thumb: `launch` = fire and forget. `async` = start now, need a result later.
`runBlocking` = only in `main`/tests. `coroutineScope` = run things in parallel and wait for all
of them. `withContext` = run this block on another thread pool and give me the result.

## Question 14. Job or Deferred

**Output**

```text
inside launch the result is 42, but it cannot leave the coroutine
launch -> job.join() returned: kotlin.Unit
async -> deferred.await() returned: 42
```

**Why `launch` can't give a result:** the lambda of `launch` has type
`suspend CoroutineScope.() -> Unit`, so its value is thrown away. `launch` returns a `Job`, and
`Job` has no `await()` (`job.await()` doesn't compile). `job.join()` returns `Unit`. You can
only wait for a `Job` or cancel it.

**Job vs Deferred:**

* `Job` is a handle to a coroutine's **lifecycle**: `isActive`, `isCompleted`, `cancel()`,
  `join()`, children. It carries **no result**.
* `Deferred<T>` is a **`Job` with a result** (`interface Deferred<out T> : Job`). It has
  everything `Job` has, plus `await(): T`. `await()` suspends until the value is ready and
  returns it, or rethrows the exception the coroutine failed with.

## Question 15. Three exports in a scope

```kotlin
println("Start")
coroutineScope {
    launch { delay(300L); println("X") }
    launch { delay(100L); println("Y") }
    launch { delay(200L); println("Z") }
}
println("All done")
```

**Output**

```text
Start
Y
Z
X
All done
```

No `join` and no `delay` in the main body. `coroutineScope` waits for all three children
automatically, so "All done" comes after the slowest export (X, 300 ms). The exports finish in
order of their duration: Y (100), Z (200), X (300).

---

## Extra 1. Delay or sleep: measure it

**Output**

```text
3 x delay(300)       took ~300 ms (raw 311 ms)
3 x Thread.sleep(300) took ~900 ms (raw 902 ms)
```

**Why the numbers differ on the same thread:** `delay` suspends only its coroutine and frees the
thread. All three coroutines start their 300 ms timers at almost the same moment and wait **in
overlap**, so the total is ~300 ms. `Thread.sleep` blocks the one thread. While the first
coroutine sleeps, the second can't even start, so the waits run **one after another**:
3 × 300 = ~900 ms. The project manager is right only for blocking code.

## Extra 2. Three levels of coroutines

**Output**

```text
child 1 done
child 2 done
grandchild done
scope done
```

**Coroutine tree**

```text
runBlocking
└── coroutineScope                        → "scope done" printed after it completes (~200 ms)
    ├── child 1   (body prints "child 1 done" at 0 ms, completes at 200 ms)
    │   └── grandchild  (delay 200 → "grandchild done")
    └── child 2   (delay 100 → "child 2 done")
```

**Why "scope done" is last:** child 1's body doesn't wait for the grandchild, so "child 1 done"
appears immediately. But child 1 is *completed* only after its grandchild finishes, and
`coroutineScope` completes only after **all descendants** (children *and* grandchildren) are
done. The latest of them is the grandchild at 200 ms, so "scope done" is printed after
"grandchild done".

## Extra 3. No scope, no launch

**Broken code** ([`compile-errors/E3_Broken.kt`](compile-errors/E3_Broken.kt)):

```kotlin
fun brokenMain() {
    launch { println("hello") }
}
```

**Compiler error (Kotlin 2.1.21 + kotlinx-coroutines 1.10.2):**

```text
e: E3_Broken.kt:7:5 'fun launch(context: CoroutineContext = ..., start: CoroutineStart = ..., block: suspend CoroutineScope.() -> Unit): Job' is deprecated. 'launch' can not be called without the corresponding coroutine scope. Consider wrapping 'launch' in 'coroutineScope { }', using 'runBlocking { }', or using some other 'CoroutineScope'.
```

(Without the library's special deprecated overload the IDE would report
`Unresolved reference 'launch'`. Either way the cause is that `launch` needs a `CoroutineScope`
receiver.)

**Fix:** wrap the body in **`runBlocking`**, the builder from the first lecture example:

```text
runBlocking runs on: main
main body done, runBlocking still waits for its child
hello from launch
```

**What `runBlocking` does with the current thread:** it **blocks the current thread** (here
`main`) and turns it into an event loop that runs the coroutine and all its children. It returns
only when every child has finished. That's why the JVM doesn't exit before "hello from launch".

## Extra 4. What the compiler sees

```kotlin
data class User(val id: Int, val name: String)

suspend fun loadUserById(id: Int): User { delay(100L); return User(id, "user-$id") }
```

**Generated signature** (Tools → Kotlin → Show Kotlin Bytecode → Decompile; checked with
`javap` on the compiled class):

```java
// javap -p practice6.E4_WhatTheCompilerSeesKt
public static final java.lang.Object loadUserById(int, kotlin.coroutines.Continuation<? super practice6.User>);

// IntelliJ "Decompile" view shows the same as:
@Nullable
public static final Object loadUserById(int id, @NotNull Continuation $completion)
```

**Output**

```text
User(id=42, name=user-42)
```

* **Extra parameter:** `Continuation<? super User> $completion`.
* **New return type:** `Object` (Kotlin `Any?`) instead of `User`. The function returns either
  the real `User`, or the special marker `COROUTINE_SUSPENDED` when it paused.
* **What the Continuation is for:** it's the callback that says "how to continue". It holds the
  caller's context (`CoroutineContext`, so the dispatcher too) and the state machine
  (`label` + saved local variables). When `delay` finishes, the library calls
  `$completion.resumeWith(Result.success(user))`. Execution then goes on from the saved
  `label`, and the `User` is delivered to the caller. This is *Continuation-Passing Style*.

## Extra 5. Greet everybody

**Output**

```text
before
Hello, Ada
Hello, Linus
Hello, Grace
after
```

**Why "after" is last:** `greetAll` is a `suspend` function built with `coroutineScope`. It
launches one coroutine per name (Ada 100 ms, Linus 200 ms, Grace 300 ms), and `coroutineScope`
**doesn't return until all of its children have completed**. So `main` is suspended at the
`greetAll(...)` call for ~300 ms and prints "after" only once Grace has been greeted.
