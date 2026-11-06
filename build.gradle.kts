plugins {
    kotlin("jvm") version "2.1.21"
    application
}

group = "uz.ttpu.mobileapps"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

kotlin {
    jvmToolchain(21)
}

// Run any question with:  ./gradlew run -Pq=Q01
// The value is the file name prefix in src/main/kotlin/practice6 (Q01..Q15, E1..E5).
application {
    val q = (findProperty("q") as String?) ?: "Q01"
    val file = file("src/main/kotlin/practice6").listFiles()
        ?.map { it.nameWithoutExtension }
        ?.firstOrNull { it.startsWith(q + "_") }
        ?: error("No file for question '$q'")
    mainClass.set("practice6.${file}Kt")
}
