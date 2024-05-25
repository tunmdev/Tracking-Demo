plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.tunm17.trackingapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tunm17.trackingapp"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
    }
    secrets {
        // Optionally specify a different file name containing your secrets.
        // The plugin defaults to "local.properties"
        propertiesFileName = "secrets.properties"

        // A properties file containing default secret values. This file can be
        // checked in version control.
        defaultPropertiesFileName = "local.defaults.properties"

        // Configure which keys should be ignored by the plugin by providing regular expressions.
        // "sdk.dir" is ignored by default.
        ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
        ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
    }
    viewBinding {
        enable = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")

    val nav_version = "2.7.7"
    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    // Maps SDK for Android
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.2.0")

    implementation ("com.jakewharton.timber:timber:5.0.1")
    implementation ("pub.devrel:easypermissions:3.0.0")

    val lifecycle_version = "2.8.0"
    val arch_version = "2.2.0"
    implementation("androidx.lifecycle:lifecycle-service:$lifecycle_version")

    implementation("androidx.fragment:fragment-ktx:1.7.1")
}