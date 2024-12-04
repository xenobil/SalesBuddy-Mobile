import org.gradle.internal.impldep.bsh.commands.dir

plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.salesbuddy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.salesbuddy"
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.12.0") // Use a versão mais recente do Material
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("fr.tvbarthel.blurdialogfragment:lib:2.2.0")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // AndroidX Test dependencies
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")

    // AndroidX Core dependencies
    implementation ("androidx.core:core:1.13.1") // Use a versão mais recente do Core

    //SDK DA LIO
    //implementation ("com.cielo.lio:order-manager:1.8.7")

}