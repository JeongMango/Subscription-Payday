// build-logic: 모듈마다 반복되는 Gradle 설정을 플러그인으로 만들어 한곳에서 관리합니다.
// 본 프로젝트와 같은 버전 카탈로그(gradle/libs.versions.toml)를 함께 씁니다.
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
