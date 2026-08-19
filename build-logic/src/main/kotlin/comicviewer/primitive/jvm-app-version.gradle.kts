/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package comicviewer.primitive

import com.sorrowblue.comicviewer.GitTagValueSource
import com.sorrowblue.comicviewer.extractPackageVersion

plugins {
    org.jetbrains.kotlin.multiplatform
}

val gitTagProvider: Provider<String>
    get() = providers.of(GitTagValueSource::class) {}

version = extractPackageVersion(gitTagProvider.get())
logger.lifecycle("jvm: version=$version")
