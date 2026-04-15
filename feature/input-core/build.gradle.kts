apply(plugin = "org.jetbrains.kotlin.multiplatform")

kotlin {
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core:common-interfaces"))
            }
        }
    }
}
