package com.sorrowblue.comicviewer.feature.bookshelf.edit

import kotlinx.serialization.Serializable

/**
 * Bookshelf editor form state.
 */
@Serializable
internal sealed interface BookshelfEditorForm {
    /**
     * Bookshelf display name.
     */
    val displayName: String

    /**
     * Update display name.
     *
     * @param displayName New display name.
     * @return New BookshelfEditorForm.
     */
    fun <T : BookshelfEditorForm> update(displayName: String): T
}

/**
 * SMB bookshelf editor form state.
 *
 * @property displayName Bookshelf display name.
 * @property host SMB server host.
 * @property port SMB server port.
 * @property path SMB share path.
 * @property auth SMB authentication type.
 * @property domain SMB domain.
 * @property username SMB username.
 * @property password SMB password.
 */
@Serializable
internal data class SmbEditorForm(
    override val displayName: String = "",
    val host: String = "",
    val port: Int = 445,
    val path: String = "",
    val auth: Auth = Auth.Guest,
    val domain: String = "",
    val username: String = "",
    val password: String = "",
) : BookshelfEditorForm {

    /**
     * SMB authentication type.
     */
    enum class Auth {
        /** Guest authentication. */
        Guest,

        /** Username/Password authentication. */
        UserPass,
    }

    override fun <T : BookshelfEditorForm> update(displayName: String): T {
        @Suppress("UNCHECKED_CAST")
        return copy(displayName = displayName) as T
    }
}

/**
 * Internal storage bookshelf editor form state.
 *
 * @property displayName Bookshelf display name.
 * @property path Path to the directory in internal storage. Null if the root directory is selected.
 */
@Serializable
internal data class InternalStorageEditorForm(
    override val displayName: String = "",
    val path: String? = null,
) : BookshelfEditorForm {

    override fun <T : BookshelfEditorForm> update(displayName: String): T {
        @Suppress("UNCHECKED_CAST")
        return copy(displayName = displayName) as T
    }
}
