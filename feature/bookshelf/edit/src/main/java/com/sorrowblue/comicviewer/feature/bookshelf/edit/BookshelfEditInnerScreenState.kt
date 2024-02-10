package com.sorrowblue.comicviewer.feature.bookshelf.edit

sealed class BookshelfEditInnerScreenState<T : BookshelfEditScreenUiState> {
    abstract var uiState: T
        protected set
}

data object BookshelfEditLoading : BookshelfEditInnerScreenState<UnitUiState>() {
    override var uiState: UnitUiState = UnitUiState
}
