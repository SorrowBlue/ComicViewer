package com.sorrowblue.comicviewer.domain.model

import android.os.Parcelable
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Folder
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookshelfFolder(val bookshelf: Bookshelf, val folder: Folder) : Parcelable
