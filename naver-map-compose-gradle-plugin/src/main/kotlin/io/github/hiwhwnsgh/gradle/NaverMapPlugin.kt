package io.github.hiwhwnsgh.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.net.URI

class NaverMapPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // 1. 네이버 지도 저장소를 프로젝트에 추가
        project.repositories.maven {
            url = URI("https://repository.map.naver.com/archive/maven")
        }

        // 2. 라이브러리 의존성을 자동으로 추가
        // KMP 프로젝트의 경우 commonMainApi에 추가하도록 유도
        project.afterEvaluate {
            val version = "1.0.1" // TODO: 버전을 동적으로 관리할 수 있는 방법 고려
            val dependency = "io.github.kmp-naver-map:naver-map-compose:$version"
            
            val commonMainApi = project.configurations.findByName("commonMainApi")
            if (commonMainApi != null) {
                commonMainApi.dependencies.add(project.dependencies.create(dependency))
            } else {
                // KMP가 아닌 일반 안드로이드 프로젝트 등의 경우를 위해 implementation에도 추가 고려 가능
                project.configurations.findByName("implementation")?.dependencies?.add(
                    project.dependencies.create(dependency)
                )
            }
        }
    }
}
