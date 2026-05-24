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
        val count = Runtime.getRuntime().exec("git rev-list --count HEAD").let {
            it.waitFor()
            it.inputStream.bufferedReader().readText().trim().toInt()
        }
        val isDirty = Runtime.getRuntime().exec("git status --porcelain").let {
            it.waitFor()
            it.inputStream.bufferedReader().readText().trim().isNotEmpty()
        }
        if (isDirty) count + 1 else count
    } catch (e: Exception) {
        1
    }

    val versionSuffix = try {
        val isDirty = Runtime.getRuntime().exec("git status --porcelain").let {
            it.waitFor()
            it.inputStream.bufferedReader().readText().trim().isNotEmpty()
        }
        if (isDirty) "-dirty" else ""
    } catch (e: Exception) {
        ""
    }

    defaultConfig {
        applicationId = "dev.module.betterstatusbar"
        minSdk = 33
        targetSdk = 35
        versionCode = commitCount
        versionName = "2.0.${commitCount}${versionSuffix}"

        resourceConfigurations += listOf("en", "zh-rCN")
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

    // No native libraries, so splits are unnecessary.
    // A single APK will work on all ABIs.


    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = if (signingConfigs.getByName("release").storeFile != null) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
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