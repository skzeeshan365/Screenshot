plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.android.gms.oss-licenses-plugin")
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
        versionCode = 21
        versionName = "1.1.8"

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

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("com.github.stfalcon-studio:StfalconImageViewer:v1.0.1")
    implementation("com.google.firebase:firebase-messaging:24.0.0")
    implementation("com.google.firebase:firebase-analytics:22.0.1")
    implementation("com.google.firebase:firebase-crashlytics:19.0.1")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //DataStore instead of SharedPreferences
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    //RxJava2 support
    implementation ("androidx.datastore:datastore-preferences-rxjava2:1.0.0")

    implementation ("com.google.android.play:integrity:1.3.0")

    // To recognize Latin script
    implementation ("com.google.mlkit:text-recognition:16.0.0")
    // To recognize Devanagari script
    implementation ("com.google.mlkit:text-recognition-devanagari:16.0.0")

    // To recognize Japanese script
    implementation ("com.google.android.gms:play-services-mlkit-text-recognition-japanese:16.0.0")
    // To recognize Korean script
    implementation ("com.google.android.gms:play-services-mlkit-text-recognition-korean:16.0.0")
    // To recognize Chinese script
    implementation ("com.google.android.gms:play-services-mlkit-text-recognition-chinese:16.0.0")

    implementation ("com.google.android.gms:play-services-mlkit-image-labeling:16.0.8")

    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")

    implementation ("androidx.work:work-runtime:2.9.0")
    implementation ("com.google.guava:guava:30.1-jre")

    implementation ("com.google.android.gms:play-services-ads:23.1.0")

    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.github.deano2390:FlowTextView:2.0.5")

    val markwonVersion = "4.6.2"
    implementation("io.noties.markwon:core:$markwonVersion")
    implementation("io.noties.markwon:ext-tasklist:$markwonVersion")
    implementation("io.noties.markwon:html:$markwonVersion")
    implementation("io.noties.markwon:inline-parser:$markwonVersion")
    implementation("io.noties.markwon:linkify:$markwonVersion")
    implementation("io.noties.markwon:simple-ext:$markwonVersion")

    implementation ("androidx.security:security-crypto:1.1.0-alpha06")

    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

    implementation("com.google.android.gms:play-services-oss-licenses:17.1.0")
    implementation ("com.google.android.gms:play-services-location:20.0.0")

}