 import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

// 1. 시크릿 로더 정의
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}

fun getSecret(key: String): String {
    return System.getenv(key) 
        ?: project.findProperty(key)?.toString() 
        ?: localProperties.getProperty(key)
        ?: ""
}

val naverClientId = getSecret("NAVER_CLIENT_ID")

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain {
            // 2. BuildConfig 처럼 사용할 수 있도록 소스 생성 또는 Config 주입
            val packageName = "io.github.kmp.naver.map"
            val generateSecrets = tasks.register("generateSecrets") {
                val outDir = project.layout.buildDirectory.dir("generated/secrets/kotlin/io/github/kmp/naver/map")
                outputs.dir(outDir)
                doLast {
                    val secretsFile = outDir.get().file("Secrets.kt").asFile
                    secretsFile.parentFile.mkdirs()
                    secretsFile.writeText(
                        """
                        package $packageName
                        
                        object Secrets {
                            const val NAVER_CLIENT_ID = "$naverClientId"
                        }
                        """.trimIndent()
                    )
                }
            }

            kotlin.srcDir(project.layout.buildDirectory.dir("generated/secrets/kotlin"))
            
            dependencies {
                implementation(project(":naver-map-compose"))
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.navigation.compose)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "io.github.kmp.naver.map"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.kmp.naver.map"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}
