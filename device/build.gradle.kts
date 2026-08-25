plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.ar_gunman_android.device"
    compileSdk = 33

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        // 1. Kotlinのバージョン不一致エラーを無視するフラグ
        freeCompilerArgs += listOf("-Xskip-metadata-version-check")
    }
}

// 2. 推移的依存ライブラリをcompileSdk 33 / Kotlin 1.7互換のバージョンに強制固定
configurations.all {
    resolutionStrategy {
        force(
            "org.jetbrains.kotlin:kotlin-stdlib:1.7.20",
            "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.7.20",
            "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.20",
            "androidx.core:core:1.10.1",
            "androidx.core:core-ktx:1.10.1",
            "androidx.annotation:annotation:1.6.0",
            "androidx.annotation:annotation-jvm:1.6.0",
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3",
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
        )
    }
}

dependencies {
    // 3. 直接指定していた最新ライブラリを API 33 / Kotlin 1.7 互換の安定版へ変更
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.7.20"))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    implementation(project(":domain"))
}