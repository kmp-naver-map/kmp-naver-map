import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinCocoapods) apply false
    alias(libs.plugins.mavenPublish) apply false
}

// 1. local.properties 로드
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

// 2. 통합 시크릿 로더 정의
fun getPublishSecret(key: String): String? {
    return System.getenv(key) 
        ?: project.findProperty(key)?.toString() 
        ?: localProperties.getProperty(key)
}

// 3. 배포용 자격 증명 추출 및 시스템 프로퍼티 강제 주입
// 값이 없더라도 빈 문자열이라도 넣어주어야 서비스 종료 시 에러가 발생하지 않습니다.
val mavenUser = getPublishSecret("MAVEN_CENTRAL_USERNAME") ?: ""
val mavenPassword = getPublishSecret("MAVEN_CENTRAL_PASSWORD") ?: ""

System.setProperty("org.gradle.project.mavenCentralUsername", mavenUser)
System.setProperty("org.gradle.project.mavenCentralPassword", mavenPassword)
System.setProperty("org.gradle.project.sonatypeUsername", mavenUser)
System.setProperty("org.gradle.project.sonatypePassword", mavenPassword)

// 4. 서브 프로젝트에서 사용할 수 있도록 extra 속성에 함수 저장
allprojects {
    extra.set("getPublishSecret", ::getPublishSecret)
}
