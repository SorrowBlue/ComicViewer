/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package comicviewer.primitive

import com.sorrowblue.comicviewer.AndroidVersionCodeValueSource
import com.sorrowblue.comicviewer.GitPlaneTagValueSource

plugins {
    com.android.application
}

androidComponents {
    onVariants(selector().all()) { variant ->
        variant.outputs.forEach { output ->
            val gitTagCountProvider = providers.of(AndroidVersionCodeValueSource::class) {}
            val gitPlaneTagProvider = providers.of(GitPlaneTagValueSource::class) {}
            output.versionName.set(gitPlaneTagProvider.get())
            output.versionCode.set(gitTagCountProvider.get().toInt())
            logger.lifecycle(
                "${variant.name}: versionName=${output.versionName.get()}, versionCode=${output.versionCode.get()}",
            )
        }
    }
}
