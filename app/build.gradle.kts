plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.pmp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pmp"
        minSdk = 26
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.6.1")//自动下载了Retrofit，okHttp和Okio，Okio是OkHttp的通信基础
    implementation("com.squareup.retrofit2:converter-gson:2.6.1")//自动下载了GSON转换库
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.dmytrodanylyk:circular-progress-button:1.4")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui.graphics.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.github.notice501:coolMenu:v1.2")                                            //卡片式ViewPager
    implementation("com.github.dmytrodanylyk:circular-progress-button:1.4")                         //状态按钮
    implementation("de.hdodenhof:circleimageview:3.1.0")                                            //圆形头像
    implementation("com.github.wangjiegulu:rfab:2.0.0")                                             //悬浮菜单
    implementation("com.android.support:appcompat-v7:28.0.0")                                       //兼容旧版
    implementation("com.ramotion.foldingcell:folding-cell:1.2.3")                                   //折叠子项
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")                                   //Retrofit
    implementation("com.squareup.okhttp3:okhttp:4.9.3")                                             //okhttp
    implementation("com.github.bumptech.glide:glide:4.15.1")                                        //glide
    implementation("androidx.databinding:databinding-runtime:7.2.0")
    implementation("androidx.databinding:databinding-runtime:7.2.0")                                //kapt
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.github.mittsu333:MarkedView-for-Android:1.0.7")
}