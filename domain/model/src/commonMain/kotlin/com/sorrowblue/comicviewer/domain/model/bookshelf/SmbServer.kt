package com.sorrowblue.comicviewer.domain.model.bookshelf

import com.sorrowblue.comicviewer.domain.model.InternalDataApi

data class SmbServer private constructor(
    override val id: BookshelfId,
    override val displayName: String,
    override val fileCount: Int,
    override val isDeleted: Boolean,
    val host: String,
    val port: Int,
    val auth: Auth,
) : Bookshelf {

    fun copy(
        displayName: String = this.displayName,
        host: String = this.host,
        port: Int = this.port,
        auth: Auth = this.auth,
    ) = copy(
        id = id,
        displayName = displayName,
        host = host,
        port = port,
        auth = auth
    )

    companion object {

        operator fun invoke(displayName: String, host: String, port: Int, auth: Auth) = SmbServer(
            id = BookshelfId(),
            displayName = displayName,
            host = host,
            port = port,
            auth = auth,
            fileCount = 0,
            isDeleted = false
        )

        @InternalDataApi
        operator fun invoke(
            id: BookshelfId,
            displayName: String,
            host: String,
            port: Int,
            auth: Auth,
            fileCount: Int = 0,
            isDeleted: Boolean = false,
        ) = SmbServer(
            id = id,
            displayName = displayName,
            host = host,
            port = port,
            auth = auth,
            fileCount = fileCount,
            isDeleted = isDeleted
        )
    }

    sealed interface Auth {

        data class UsernamePassword(
            val domain: String,
            val username: String,
            val password: String,
        ) : Auth

        data object Guest : Auth
    }
}
