package com.sorrowblue.comicviewer.domain.entity.bookshelf

data class SmbServer(
    override val id: BookshelfId,
    override val displayName: String,
    val host: String,
    val port: Int,
    val auth: Auth,
    override val fileCount: Int
) : Bookshelf {

    constructor(displayName: String, host: String, port: Int, auth: Auth) : this(
        BookshelfId(0),
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
            val password: String
        ) : Auth

        data object Guest : Auth
    }
}
