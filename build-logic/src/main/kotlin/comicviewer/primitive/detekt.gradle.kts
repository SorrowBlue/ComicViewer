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
    autoCorrect = true
    parallel = true
    config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
    buildUponDefaultConfig = true
    basePath.set(rootProject.projectDir)
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
