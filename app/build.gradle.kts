plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.iread"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.store.iread"
        minSdk = 27
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

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.activity:activity:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    // get color primary in image banner
    implementation ("androidx.palette:palette:1.0.0")
    // Gilde
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")
    //Okhttp
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")

    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.reactivex.rxjava3:rxjava:3.0.0")

    //CircleImageView
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //Gson
    implementation ("com.google.code.gson:gson:2.10.1")
    //SwipeFreshLayout
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    //Navigation Components
    implementation ("com.google.android.material:material:1.10.0")
    //FlexBoxLayout
    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    // PaperDB
    implementation ("io.github.pilgr:paperdb:2.7.2")
    // Lottie
    implementation("com.airbnb.android:lottie:6.4.1")
    //Material Icons
    implementation ("com.google.android.material:material:1.11.0")
     //secutiry
    implementation ("androidx.security:security-crypto:1.1.0-alpha03")
    //MPAndroidChart
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    //Gmail
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))

    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-messaging:23.0.0")

    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.android.exoplayer:extension-okhttp:2.19.1")
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")
    implementation("androidx.media3:media3-datasource-okhttp:1.2.1")

    //MPAndroidChart
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")


}