package com.sorrowblue.comicviewer.book

import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.file.Book

fun BookFragmentArgs(book: Book, transitionName: String? = null, favoriteId: FavoriteId = FavoriteId(-1)) =
    BookFragmentArgs(book.bookshelfId.value, book.base64Path(), transitionName, book.lastPageRead, favoriteId = favoriteId.value)
