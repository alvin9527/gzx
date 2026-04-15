apply(plugin = "org.jetbrains.kotlin.multiplatform")

kotlin {
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            }
        }
    }
}
