plugins {
    // **Now** actually apply them—no versions here:
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace   = "com.unsalgasimliapplicationsnsug.projectnsug"
    compileSdk  = 35

    defaultConfig {
        applicationId             = "com.unsalgasimliapplicationsnsug.projectnsug"
        minSdk                    = 34
        targetSdk                 = 35
        versionCode               = 1
        versionName               = "1.0"
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
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth")

    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.coordinatorlayout)
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity:1.10.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
