plugins {
    id("com.android.application")
}

android {
    namespace = "com.reiserx.screenshot"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.reiserx.screenshot"
        minSdk = 30
        targetSdk = 34
        versionCode = 10
        versionName = "1.0.8"

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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("com.github.stfalcon-studio:StfalconImageViewer:v1.0.1")
    implementation("com.github.yalantis:ucrop:2.2.8-native")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    //DataStore instead of SharedPreferences
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    //RxJava2 support
    implementation ("androidx.datastore:datastore-preferences-rxjava2:1.0.0")

    implementation ("com.google.android.play:integrity:1.3.0")
    implementation ("com.github.MikeOrtiz:TouchImageView:3.1.1")
}