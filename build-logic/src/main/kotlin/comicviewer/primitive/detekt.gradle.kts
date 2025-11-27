package comicviewer.primitive

import com.sorrowblue.comicviewer.libs
import dev.detekt.gradle.Detekt

plugins {
    dev.detekt
}

dependencies {
    detektPlugins(libs.detekt.compose)
    detektPlugins(libs.detekt.ktlintWrapper)
}

detekt {
    buildUponDefaultConfig.set(true)
    autoCorrect.set(true)
    basePath.set(rootProject.projectDir)
    config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
}

tasks.withType<Detekt>().configureEach {
    reports {
        sarif.required.set(true)
        html.required.set(false)
        markdown.required.set(false)
        checkstyle.required.set(false)
    }
    exclude {
        it.file.path.run { contains("generated") || contains("buildkonfig") }
    }
}

mapOf(
    "detektAndroidAll" to "(?i)^(?!.*(SourceSet|metadata)).*android.*$".toRegex(),
    "detektDesktopAll" to "(?i)^(?!.*(SourceSet|metadata)).*desktop.*$".toRegex(),
    "detektIosAll" to "(?i)^(?!.*(metadata)).*ios.*$".toRegex(),
).forEach { (taskName, regex) ->
    tasks.register(taskName) {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        dependsOn(
            tasks
                .withType<Detekt>()
                .matching { detekt -> detekt.name.contains(regex) },
        )
    }
}
