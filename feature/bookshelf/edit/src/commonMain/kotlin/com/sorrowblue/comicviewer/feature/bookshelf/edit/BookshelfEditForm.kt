package com.sorrowblue.comicviewer.feature.bookshelf.edit

import kotlinx.serialization.Serializable

internal sealed interface BookshelfEditForm {
    fun <T : BookshelfEditForm> update(displayName: String, isRunning: Boolean? = null): T

    val displayName: String
    val isRunning: Boolean
}

@Serializable
internal data class SmbEditScreenForm(
    override val displayName: String = "",
    val host: String = "",
    val port: Int = 445,
    val path: String = "",
    val auth: Auth = Auth.Guest,
    val domain: String = "",
    val username: String = "",
    val password: String = "",
    override val isRunning: Boolean = false,
) : BookshelfEditForm {

    enum class Auth {
        Guest,
        UserPass,
    }

    override fun <T : BookshelfEditForm> update(displayName: String, isRunning: Boolean?): T {
        @Suppress("UNCHECKED_CAST")
        return copy(displayName = displayName, isRunning = isRunning ?: this.isRunning) as T
    }
}
