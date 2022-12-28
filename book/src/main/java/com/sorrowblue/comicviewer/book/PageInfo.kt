package com.sorrowblue.comicviewer.book

data class PageInfo(val index: Int, val pos: Pos) {

    enum class Pos {
        UNKNOWN,
        LEFT,
        RIGHT,
        NONE
    }
}
