package comicviewer.primitive

import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

plugins {
    org.jetbrains.dokka
}

dokka {
    dokkaSourceSets.configureEach {
        suppressedFiles.setFrom(layout.buildDirectory.dir("generated"))
        documentedVisibilities(VisibilityModifier.Public)
    }
    dokkaGeneratorIsolation = ProcessIsolation {
        maxHeapSize = "4g"
    }
}
