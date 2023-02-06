package com.sorrowblue.comicviewer.data.common.bookshelf

sealed interface BookshelfModel {

    val id: BookshelfModelId
    val name: String
    data class InternalStorage(override val id: BookshelfModelId, override val name: String) :
        BookshelfModel

    data class SmbServer(
        override val id: BookshelfModelId,
        override val name: String,
        val host: String,
        val port: Int,
        val auth: Auth
    ) : BookshelfModel {

        sealed interface Auth

        object Guest : Auth
        data class UsernamePassword(val domain: String, val username: String, val password: String) :
            Auth
    }
}
