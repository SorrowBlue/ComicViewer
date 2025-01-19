package com.sorrowblue.comicviewer.feature.bookshelf.edit

internal sealed interface BookshelfEditForm {
    fun <T : BookshelfEditForm> update(displayName: String): T

    val displayName: String
}

internal data class SmbEditScreenForm(
    override val displayName: String = "",
    val host: String = "",
    val port: Int = 445,
    val path: String = "",
    val auth: Auth = Auth.Guest,
    val domain: String = "",
    val username: String = "",
    val password: String = "",
) : BookshelfEditForm {

    enum class Auth {
        Guest,
        UserPass,
    }

    override fun <T : BookshelfEditForm> update(displayName: String): T {
        @Suppress("UNCHECKED_CAST")
        return copy(displayName = displayName) as T
    }
}
