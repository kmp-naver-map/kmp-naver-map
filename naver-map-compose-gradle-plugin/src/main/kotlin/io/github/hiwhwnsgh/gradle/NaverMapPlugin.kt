package io.github.hiwhwnsgh.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.net.URI

/**
 * 네이버 지도 설정을 위한 익스텐션
 */
interface NaverMapComposeExtension {
    /**
     * naver-map-compose 라이브러리의 버전
     */
    val version: Property<String>
}

class NaverMapPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // 1. 익스텐션 생성
        val extension = project.extensions.create("naverMapCompose", NaverMapComposeExtension::class.java)
        
        // 2. 기본값 설정 (gradle.properties의 VERSION_NAME을 참고하거나 기본값 사용)
        val defaultVersion = project.findProperty("VERSION_NAME")?.toString() ?: "1.0.1"
        extension.version.convention(defaultVersion)

        // 3. 네이버 지도 저장소를 프로젝트에 추가
        project.repositories.maven {
            url = URI("https://repository.map.naver.com/archive/maven")
        }

        // 4. 라이브러리 의존성을 자동으로 추가
        project.afterEvaluate {
            val version = extension.version.get()
            val dependency = "io.github.kmp-naver-map:naver-map-compose:$version"
            
            // KMP 프로젝트의 경우 commonMainApi에 추가하도록 유도
            val commonMainApi = project.configurations.findByName("commonMainApi")
            if (commonMainApi != null) {
                commonMainApi.dependencies.add(project.dependencies.create(dependency))
            } else {
                // KMP가 아닌 일반 안드로이드 프로젝트 등의 경우를 위해 implementation에도 추가
                project.configurations.findByName("implementation")?.dependencies?.add(
                    project.dependencies.create(dependency)
                )
            }
        }
    }
}
