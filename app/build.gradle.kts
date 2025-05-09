plugins {
    alias(libs.plugins.android.application)


}


android {
    namespace = "com.satnamsinghmaggo.nitnemgurbani"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.satnamsinghmaggo.nitnemgurbani"
        minSdk = 28
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
    implementation("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")

    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.airbnb.android:lottie:6.4.0")
    implementation ("org.jsoup:jsoup:1.15.4")

}


