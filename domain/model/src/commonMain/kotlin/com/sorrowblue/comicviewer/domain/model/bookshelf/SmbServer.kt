/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.bookshelf

import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi

/**
 * Represents an SMB network server [Bookshelf].
 *
 * @property id The unique identifier of the bookshelf.
 * @property displayName The display name of the bookshelf.
 * @property fileCount The number of files in the bookshelf.
 * @property isDeleted Whether the bookshelf is logically deleted.
 * @property host The hostname or IP address of the SMB server.
 * @property port The port number of the SMB server.
 * @property auth The authentication method used to connect to the server.
 */
data class SmbServer private constructor(
    override val id: BookshelfId,
    override val displayName: String,
    override val fileCount: Int,
    override val isDeleted: Boolean,
    val host: String,
    val port: Int,
    val auth: Auth,
) : Bookshelf {
    override val type = BookshelfType.SMB

    /**
     * Returns a copy of this [SmbServer] with updated connection settings.
     */
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
        auth = auth,
    )

    companion object {
        /**
         * Creates a new [SmbServer] instance with the given connection settings.
         */
        operator fun invoke(displayName: String, host: String, port: Int, auth: Auth) = SmbServer(
            id = BookshelfId(),
            displayName = displayName,
            host = host,
            port = port,
            auth = auth,
            fileCount = 0,
            isDeleted = false,
        )

        /**
         * Reconstructs an [SmbServer] instance from persisted fields.
         */
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
            isDeleted = isDeleted,
        )
    }

    /**
     * Authentication configuration for connecting to an [SmbServer].
     */
    sealed interface Auth {
        /**
         * Username and password authentication.
         *
         * @property domain The authentication domain or workgroup.
         * @property username The account username.
         * @property password The account password.
         */
        data class UsernamePassword(
            val domain: String,
            val username: String,
            val password: String,
        ) : Auth {
            override fun toString(): String =
                "UsernamePassword(domain=$domain, username=$username, password=***)"
        }

        /**
         * Anonymous guest authentication.
         */
        data object Guest : Auth
    }
}
