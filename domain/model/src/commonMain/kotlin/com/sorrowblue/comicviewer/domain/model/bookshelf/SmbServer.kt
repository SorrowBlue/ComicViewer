package com.sorrowblue.comicviewer.domain.model.bookshelf

data class SmbServer(
    override val id: BookshelfId,
    override val displayName: String,
    val host: String,
    val port: Int,
    val auth: Auth,
    override val fileCount: Int,
    override val isDeleted: Boolean = false,
) : Bookshelf {

    constructor(displayName: String, host: String, port: Int, auth: Auth) : this(
        BookshelfId(),
        displayName,
        host,
        port,
        auth,
        0
    )

    constructor(id: BookshelfId, displayName: String, host: String, port: Int, auth: Auth) : this(
        id,
        displayName,
        host,
        port,
        auth,
        0
    )

    sealed interface Auth {

        data class UsernamePassword(
            val domain: String,
            val username: String,
            val password: String,
        ) : Auth

        data object Guest : Auth
    }
}
