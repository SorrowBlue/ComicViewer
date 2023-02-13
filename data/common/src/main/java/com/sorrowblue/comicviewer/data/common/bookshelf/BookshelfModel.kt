package com.sorrowblue.comicviewer.data.common.bookshelf

sealed interface BookshelfModel {

    val id: BookshelfModelId
    val name: String
    val fileCount: Int

    data class InternalStorage(
        override val id: BookshelfModelId,
        override val name: String,
        override val fileCount: Int
    ) : BookshelfModel

    data class SmbServer(
        override val id: BookshelfModelId,
        override val name: String,
        val host: String,
        val port: Int,
        val auth: Auth,
        override val fileCount: Int
    ) : BookshelfModel {

        sealed interface Auth

        object Guest : Auth
        data class UsernamePassword(
            val domain: String,
            val username: String,
            val password: String
        ) :
            Auth
    }
}
