plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.iread"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.iread"
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



}