/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.add.component

import comicviewer.feature.collection.add.generated.resources.Res
import comicviewer.feature.collection.add.generated.resources.collection_add_label_created
import comicviewer.feature.collection.add.generated.resources.collection_add_label_recent
import org.jetbrains.compose.resources.StringResource

internal enum class CollectionSort(val labelRes: StringResource) {
    Recent(Res.string.collection_add_label_recent),
    Created(Res.string.collection_add_label_created),
}
