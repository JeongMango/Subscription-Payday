pluginManagement {
    // 직접 만든 컨벤션 플러그인(subday.android.feature 등)을 build-logic 폴더에서 가져옵니다.
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// 모듈을 문자열(":core:data") 대신 projects.core.data처럼 코드로 참조합니다. 오타가 컴파일 에러로 잡힙니다.
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "SubDay"
include(":app")

include(":core:model")
include(":core:domain")
include(":core:data")
include(":core:designsystem")
include(":core:notification")

include(":feature:home")
include(":feature:edit")
include(":feature:settings")
