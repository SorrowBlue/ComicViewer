/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package comicviewer.primitive

import com.sorrowblue.comicviewer.AndroidVersionCodeValueSource
import com.sorrowblue.comicviewer.GitPlaneTagValueSource

plugins {
    com.android.application
}

val gitVersionCodeProvider =
    providers.of(AndroidVersionCodeValueSource::class) {}.map(String::toInt)
val gitVersionNameProvider = providers.of(GitPlaneTagValueSource::class) {}

androidComponents {
    onVariants(selector().all()) { variant ->
        variant.outputs.forEach { output ->
            output.versionName.set(gitVersionNameProvider)
            output.versionCode.set(gitVersionCodeProvider)
        }
    }
}
