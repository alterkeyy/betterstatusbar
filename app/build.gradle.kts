plugins {
    alias(libs.plugins.android.application)
}
android {
    namespace = "dev.module.betterstatusbar"
    compileSdk = 35
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "dev.module.betterstatusbar"
        minSdk = 33
        targetSdk = 35
        versionCode = 8
        versionName = "2.0.0"
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("x86_64", "arm64-v8a", "armeabi-v7a")
            isUniversalApk = false
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
dependencies {
    compileOnly(libs.libxposed.api)
    implementation("com.google.android.material:material:1.12.0")
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
}