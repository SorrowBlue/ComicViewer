package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface BookshelfEditScreenUiState : Parcelable

@Parcelize
data object UnitUiState : BookshelfEditScreenUiState
