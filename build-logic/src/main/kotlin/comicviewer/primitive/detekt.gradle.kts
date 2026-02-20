@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package comicviewer.primitive

import com.sorrowblue.comicviewer.libs
import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.plugin.internal.DetektAndroidCompilations

plugins {
    dev.detekt
}

plugins.withId("com.android.application") {
    val extension = project.extensions.getByType<DetektExtension>()
    DetektAndroidCompilations.registerTasks(project, extension)
    DetektAndroidCompilations.linkTasks(project, extension)
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
