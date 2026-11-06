/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package comicviewer.primitive

import com.sorrowblue.comicviewer.libs
import dev.detekt.gradle.Detekt
import dev.detekt.gradle.report.ReportMergeTask

plugins {
    dev.detekt
}

dependencies {
    detektPlugins(libs.bundles.detekt)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(isolated.rootProject.projectDirectory.file("config/detekt/detekt.yml"))
    basePath.set(isolated.rootProject.projectDirectory)
    autoCorrect = true
    parallel = true
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

if (!providers.gradleProperty(
        "org.gradle.isolated-projects",
    ).map(String::toBoolean).getOrElse(false)
) {
    rootProject.tasks.withType<ReportMergeTask>().configureEach {
        input.from(
            tasks.withType<Detekt>()
                .map { it.reports.sarif.outputLocation },
        )
    }
}
