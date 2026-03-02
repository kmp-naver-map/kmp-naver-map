import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.vanniktech.maven.publish.SonatypeHost
import java.util.Base64

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.mavenPublish)
    signing
}

// 1. 루트 프로젝트에서 정의한 통합 시크릿 로더 가져오기
@Suppress("UNCHECKED_CAST")
val getPublishSecret = rootProject.extra.get("getPublishSecret") as (String) -> String?

// 프로젝트 그룹 및 버전 설정 (gradle.properties에서 가져옴)
group = project.property("GROUP") as String
version = project.property("VERSION_NAME") as String

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        // Maven Central 배포를 위해 release 빌드 변수 지정
        publishLibraryVariants("release")
    }
    
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = project.version.toString()
        summary = "Naver Map SDK wrapper for Kotlin Multiplatform Compose"
        homepage = project.property("POM_URL") as String
        ios.deploymentTarget = "13.0"
        
        podfile = project.file("../iosApp/Podfile")

        framework {
            baseName = "naver_map_compose"
            isStatic = false
        }
        
        pod("NMapsMap") {
            version = "3.23.1"
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
        }
        androidMain.dependencies {
            implementation(libs.naver.map.sdk)
            implementation(libs.google.play.services.location)
        }
    }
}

android {
    namespace = "io.github.kmp.naver.map.compose"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// 2. GPG 서명 설정
val keyId = getPublishSecret("signing.keyId")
val password = getPublishSecret("signing.password")
val secretKeyBase64 = getPublishSecret("signing.secretKeyBase64")

if (!secretKeyBase64.isNullOrBlank() && !keyId.isNullOrBlank()) {
    val decodedKey = String(Base64.getMimeDecoder().decode(secretKeyBase64.trim()))
        .replace("\r\n", "\n")
        .replace("\\n", "\n")

    signing {
        @Suppress("UnstableApiUsage")
        useInMemoryPgpKeys(keyId, decodedKey, password ?: "")
    }
}

// 3. Vanniktech Maven Publish 설정 (외부 라이브러리 배포 규격)
mavenPublishing {
    // coordinates()를 직접 호출하지 않습니다.
    // 플러그인이 프로젝트의 group, version, 모듈명(artifactId)을 자동으로 사용합니다.
    
    // 태스크가 항상 생성되도록 호출 (조건문 제거)
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    
    // 서명 정보가 있을 때만 서명 프로세스 활성화
    if (!keyId.isNullOrBlank() && !secretKeyBase64.isNullOrBlank()) {
        signAllPublications()
    }

    pom {
        name.set(project.findProperty("POM_NAME")?.toString() ?: "Naver Map Compose")
        description.set(project.findProperty("POM_DESCRIPTION")?.toString() ?: "A Kotlin Multiplatform Jetpack Compose wrapper for Naver Map SDK.")
        url.set(project.findProperty("POM_URL")?.toString() ?: "https://github.com/kmp-naver-map/ComposeNaverMap")
        licenses {
            license {
                name.set(project.findProperty("POM_LICENCE_NAME")?.toString() ?: "The Apache Software License, Version 2.0")
                url.set(project.findProperty("POM_LICENCE_URL")?.toString() ?: "http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        scm {
            url.set(project.findProperty("POM_URL")?.toString() ?: "https://github.com/kmp-naver-map/ComposeNaverMap")
            connection.set(project.findProperty("POM_SCM_CONNECTION")?.toString() ?: "scm:git:github.com/kmp-naver-map/ComposeNaverMap.git")
            developerConnection.set(project.findProperty("POM_SCM_DEV_CONNECTION")?.toString() ?: "scm:git:ssh://github.com/kmp-naver-map/ComposeNaverMap.git")
        }
        developers {
            developer {
                id.set(project.findProperty("POM_DEVELOPER_ID")?.toString() ?: "hiwhwnsgh")
                name.set(project.findProperty("POM_DEVELOPER_NAME")?.toString() ?: "Jun Cho")
                email.set(project.findProperty("POM_DEVELOPER_EMAIL")?.toString() ?: "hiwhwnsgh@gmail.com")
            }
        }
    }
}
