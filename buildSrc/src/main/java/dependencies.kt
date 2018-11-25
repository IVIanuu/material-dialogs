@file:Suppress("ClassName", "unused")

object Build {
    const val applicationId = "com.ivianuu.materialdialogs.sample"
    const val buildToolsVersion = "28.0.3"
    const val compileSdk = 28
    const val minSdk = 18
    const val targetSdk = 28

    const val versionCode = 1
    const val versionName = "0.0.1"
}

object Versions {
    const val androidGradlePlugin = "3.2.1"
    const val androidxActivity = "1.0.0-alpha01"
    const val androidxAppCompat = "1.0.0"
    const val androidxFragment = "1.1.0-alpha01"
    const val kotlin = "1.3.10"
    const val mavenGradle = "2.1"
    const val materialComponents = "1.0.0"
    const val viewPagerDots = "1.0.0"
}

object Deps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"

    const val androidxActivity = "androidx.activity:activity:${Versions.androidxActivity}"
    const val androidxAppCompat = "androidx.appcompat:appcompat:${Versions.androidxAppCompat}"
    const val androidxFragment = "androidx.fragment:fragment:${Versions.androidxFragment}"

    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    const val mavenGradlePlugin =
        "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenGradle}"

    const val materialComponents =
        "com.google.android.material:material:${Versions.materialComponents}"

    const val viewPagerDots = "com.afollestad:viewpagerdots:${Versions.viewPagerDots}"
}