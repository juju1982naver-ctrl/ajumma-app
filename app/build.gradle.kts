plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.ajumma.five"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ajumma.five"
        minSdk = 26
        targetSdk = 34
        // GitHub Actions에서 빌드할 때마다 번호가 올라갑니다.
        versionCode = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1
        versionName = "1.0.$versionCode"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // 개인용이라 따로 서명 키를 두지 않고 디버그 키로 서명합니다.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    // 저장소의 data/events.json을 앱에도 넣어서, 처음 켤 때 인터넷이 없어도 목록이 보이게 합니다.
    sourceSets["main"].assets.srcDirs("../data")

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.maxHeapSize = "2g"
                it.systemProperty("shots.dir", project.layout.buildDirectory.get().asFile.resolve("ui-shots").absolutePath)
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.14.1")
}
