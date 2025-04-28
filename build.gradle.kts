plugins {
    id("com.android.application") apply false
    id("com.google.gms.google-services") apply false
}

tasks.register<Delete>("clean") {
    // delete the root project’s build directory without using the deprecated getter
    delete(layout.buildDirectory)
}
