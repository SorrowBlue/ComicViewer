/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.permission.nav

import androidx.navigation3.runtime.MetadataScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

/**
 * Metadata indicating that a [NavEntry] requires local network access permission.
 *
 * @property bookshelfId Optional bookshelf ID. When specified, local network permission is
 * dynamically required only if the bookshelf is an SMB bookshelf. When null, local network
 * permission is unconditionally required (e.g. SMB bookshelf wizard/editor).
 */
data class LocalNetworkPermissionMetadata(val bookshelfId: BookshelfId? = null)

/** Key used to store [LocalNetworkPermissionMetadata] in [NavEntry.metadata]. */
data object LocalNetworkPermissionKey : NavMetadataKey<LocalNetworkPermissionMetadata>

fun MetadataScope.localNetworkPermission(bookshelfId: BookshelfId? = null) =
    put(LocalNetworkPermissionKey, LocalNetworkPermissionMetadata(bookshelfId))
