plugins {
    id(Plugins.library)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinExtensions)
}

android {
    compileSdkVersion(BuildConfigVersions.compileSdkVersion)
    buildToolsVersion(BuildConfigVersions.buildToolsVersion)

    defaultConfig {
        minSdkVersion(BuildConfigVersions.minSdkVersion)
        targetSdkVersion(BuildConfigVersions.targetSdkVersion)
        versionCode(BuildConfigVersions.versionCode)
        versionName(BuildConfigVersions.versionName)

        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            minifyEnabled(false)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
}


dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(KotlinDependencies.kotlin)
    //androidX
    implementation(AndroidxDependencies.coreKtx)
    implementation(AndroidxDependencies.lifecycleLiveDataKtx)
    implementation(AndroidxDependencies.appcompat)
    implementation(AndroidxDependencies.constraintLayout)

    //to get dynamic feature module
    implementation(GooglePlayDependencies.playCore)

    // rx
    implementation(RxJavaDependencies.rxKotlin)
    implementation(RxJavaDependencies.rxJava)

    //Material
    implementation(MaterialDesignDependencies.materialDesign)

    // Koin for Kotlin
    implementation(KoinDependencies.koinAndroid)
    implementation(KoinDependencies.koinViewModel)

    // Logging
    implementation(MiscellaneousDependencies.timber)

    // RXBinding
    implementation(RxJavaDependencies.rxBinding)

    implementation(MiscellaneousDependencies.shimmer)

}