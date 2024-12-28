plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.incomeexpensetracker"
    compileSdk = 34

    viewBinding {
        enable = true
    }
    defaultConfig {
        applicationId = "com.example.incomeexpensetracker"
        minSdk = 26
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    val room_version = "2.6.1"
    // Room for persistent local storage
    implementation("androidx.room:room-runtime:$room_version")

    implementation("androidx.room:room-ktx:$room_version")   // Optional: Room Coroutines support

    // KSP annotation processor for Room
    ksp("androidx.room:room-compiler:$room_version")

    // For ViewModel and viewModelScope
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0") // Check for the latest version
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0") // Or the latest version
    implementation("androidx.activity:activity-ktx:1.8.2") // Or latest version
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.0") // Or the latest version
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
