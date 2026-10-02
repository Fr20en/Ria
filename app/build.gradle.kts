plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.ria.hooks"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.ria.hooks"
        minSdk = 26
        targetSdk = 37
        versionCode = 5
        versionName = "1.4.0"
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("ria.jks")
            storePassword = "lfnbb123lgm"
            keyAlias = "ria"
            keyPassword = "lfnbb123lgm"
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

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.06.00")

    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3:1.5.0-alpha24")

    debugImplementation("androidx.compose.ui:ui-tooling")
    compileOnly("io.github.libxposed:api:102.0.0")
}
