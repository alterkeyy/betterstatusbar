plugins {
    alias(libs.plugins.android.application)
}
android {
    namespace = "dev.module.statusbarbrightnessgesture"
    compileSdk = 35
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "dev.module.statusbarbrightnessgesture"
        minSdk = 33
        targetSdk = 35
        versionCode = 7
        versionName = "1.6.0"
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