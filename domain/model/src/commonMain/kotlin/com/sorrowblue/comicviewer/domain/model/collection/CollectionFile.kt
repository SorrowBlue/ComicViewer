/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.collection

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

/**
 * Represents a file entry associated with a [Collection].
 *
 * @property id The identifier of the collection.
 * @property bookshelfId The identifier of the bookshelf containing the file.
 * @property path The path of the file within the bookshelf.
 */
data class CollectionFile(val id: CollectionId, val bookshelfId: BookshelfId, val path: String)
