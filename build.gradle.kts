buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.android.tools.build:gradle:8.5.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.1")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.6")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.5.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}