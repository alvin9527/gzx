buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
    }
}

allprojects {
    group = "com.aicore"
    version = "0.1.0"

    repositories {
        mavenCentral()
        google()
    }
}
