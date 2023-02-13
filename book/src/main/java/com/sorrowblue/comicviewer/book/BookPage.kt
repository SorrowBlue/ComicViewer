package com.sorrowblue.comicviewer.book

enum class BookViewType {
    NExT,
    SPLIT,
}

sealed interface BookPage {

    val viewType: BookViewType

    data class Next(val isNext: Boolean) : BookPage {

        override val viewType = BookViewType.NExT
    }

    data class Split(val index: Int, val state: State) : BookPage {

        override val viewType = BookViewType.SPLIT

        enum class State {
            NOT_LOADED,
            LOADED_SPLIT_NON,
            LOADED_SPLIT_LEFT,
            LOADED_SPLIT_RIGHT,
        }
    }
}
