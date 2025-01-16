plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.ticketeventandroid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ticketeventandroid"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    /*
        Dépenses pour pouvoir utiliser Retrofit (pour simplifier les requêtes http) et Gson de google
        pour serialiser/deserialiser des objets java en json
    */
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    // Pour afficher les choses sous forme de liste
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    implementation("com.github.bumptech.glide:glide:4.13.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")
    implementation("com.squareup.picasso:picasso:2.8")  // Pour charger les images

    // Pour utiliser l'élément Swippe
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Faciliter les opérations réseaux
    implementation("com.android.volley:volley:1.2.1")

    // Pour l'utilisation de Stripe
    implementation("com.stripe:stripe-android:20.21.0")

    implementation("androidx.core:core:1.13.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
}