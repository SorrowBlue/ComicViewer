/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.nav

import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import kotlinx.serialization.Serializable

@Serializable
data class SmartCollectionEditNavKey(val collectionId: CollectionId) : NavKey
