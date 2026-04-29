plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "dev.module.betterstatusbar"
    compileSdk = 35
    buildFeatures {
        buildConfig = true
    }
    
    val commitCount = try {
        val process = Runtime.getRuntime().exec("git rev-list --count HEAD")
        process.waitFor()
        process.inputStream.bufferedReader().readText().trim().toInt()
    } catch (e: Exception) {
        1
    }

    defaultConfig {
        applicationId = "dev.module.betterstatusbar"
        minSdk = 33
        targetSdk = 35
        versionCode = commitCount
        versionName = "2.0.${versionCode}"
    }

    signingConfigs {
        create("release") {
            val keystoreFile = System.getenv("KEYSTORE_FILE_PATH")
            if (keystoreFile != null && file(keystoreFile).exists()) {
                storeFile = file(keystoreFile)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
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
            signingConfig = signingConfigs.getByName("release")
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