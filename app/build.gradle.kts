plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    val reweVersion = "3.18.6"

    namespace = "com.rewescanmod"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rewescanmod"
        minSdk = 26
        targetSdk = 34
        versionCode = 17
        versionName = "1.5.1-$reweVersion"

        buildConfigField("String", "TARGET_REWE_VERSION", "\"$reweVersion\"")

        setProperty("archivesBaseName", "rewescanmod-v$versionName")
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

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    compileOnly(libs.xposed.api)
}