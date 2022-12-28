package com.sorrowblue.comicviewer.domain.entity

@JvmInline
value class ServerBookshelf(val value: Pair<Server, Bookshelf>) {
    val server get() = value.first
    val bookshelf get() = value.second
}

@JvmInline
value class ServerBook(val value: Pair<Server, Book>) {
    val server get() = value.first
    val book get() = value.second
}

@JvmInline
value class ServerFile(val value: Pair<Server, File>) {
    val server get() = value.first
    val file get() = value.second
}
