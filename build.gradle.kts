plugins {
    // 如果这里没有，就在下面 buildscript 里加
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.0")   // 原来是 8.2.0，改为 8.7.0
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")  // 保持不变
    }
}
