import java.util.Base64
import org.gradle.plugins.signing.SigningExtension

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.mavenPublish)
    signing
}

// 루트의 gradle.properties 정보를 공유하여 버전 일관성 유지
group = project.property("GROUP") as String
version = project.property("VERSION_NAME") as String

gradlePlugin {
    plugins {
        create("naverMapCompose") {
            id = "io.github.kmp-naver-map"
            implementationClass = "io.github.hiwhwnsgh.gradle.NaverMapPlugin"
            displayName = "Naver Map Compose Helper Plugin"
            description = "Automatically adds Naver Map repositories and dependencies."
        }
    }
}

// 1. 루트 프로젝트에서 정의한 통합 시크릿 로더 가져오기 (배포용)
@Suppress("UNCHECKED_CAST")
val getPublishSecret = rootProject.extra.get("getPublishSecret") as? (String) -> String?

// 2. GPG 서명 설정
val keyId = getPublishSecret?.invoke("signing.keyId")
val password = getPublishSecret?.invoke("signing.password")
val secretKeyBase64 = getPublishSecret?.invoke("signing.secretKeyBase64")

if (!secretKeyBase64.isNullOrBlank() && !keyId.isNullOrBlank()) {
    val decodedKey = String(Base64.getMimeDecoder().decode(secretKeyBase64.trim()))
        .replace("\r\n", "\n")
        .replace("\\n", "\n")

    configure<SigningExtension> {
        @Suppress("UnstableApiUsage")
        useInMemoryPgpKeys(keyId, decodedKey, password ?: "")
    }
}

mavenPublishing {
    // Gradle 플러그인 모듈은 고유한 artifactId를 가지도록 명시적으로 설정
    coordinates(group.toString(), "naver-map-compose-gradle-plugin", version.toString())

    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    
    // 서명 정보가 있을 때만 서명 프로세스 활성화
    if (!keyId.isNullOrBlank() && !secretKeyBase64.isNullOrBlank()) {
        signAllPublications()
    }

    pom {
        name.set("Naver Map Compose Gradle Plugin")
        description.set("Gradle plugin to automatically configure Naver Map repository for Compose Multiplatform.")
        url.set(project.property("POM_URL") as? String ?: "https://github.com/hiwhwnsgh/ComposeNaverMap")
        licenses {
            license {
                name.set(project.property("POM_LICENCE_NAME") as? String ?: "The Apache Software License, Version 2.0")
                url.set(project.property("POM_LICENCE_URL") as? String ?: "http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set(project.property("POM_DEVELOPER_ID") as? String ?: "hiwhwnsgh")
                name.set(project.property("POM_DEVELOPER_NAME") as? String ?: "Jun Cho")
                email.set(project.property("POM_DEVELOPER_EMAIL") as? String ?: "hiwhwnsgh@gmail.com")
            }
        }
        scm {
            url.set(project.property("POM_URL") as? String ?: "https://github.com/hiwhwnsgh/ComposeNaverMap")
        }
    }
}
