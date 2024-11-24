plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.genz_fashion"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.genz_fashion"
        minSdk = 29
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
        dataBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database)
    implementation(fileTree(mapOf(
        "dir" to "C:\\Users\\acerez1\\OneDrive\\Máy tính\\ZaloPay",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))
    implementation("com.squareup.okhttp3:okhttp:4.6.0")
    implementation("commons-codec:commons-codec:1.14")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))

    //_sdp
    implementation ("com.intuit.sdp:sdp-android:1.1.0")

    // Circle Image View ( Hỗ trợ làm ảnh tròn )
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //Picasso để load ảnh , tất cả load ảnh trong app phải dùng Picasso
    implementation ("com.squareup.picasso:picasso:2.71828")

    // thư viện hỗ trợ pick số điện thoại theo mã vùng
    implementation ("com.hbb20:ccp:2.5.0")

    //config okHttp
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.android.volley:volley:1.2.1")

    //dot
    implementation ("com.tbuonomo:dotsindicator:4.2")
    //retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.google.code.gson:gson:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    // Image Picker
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation ("me.relex:circleindicator:2.1.6")
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")

    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")

}