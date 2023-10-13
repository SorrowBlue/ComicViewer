package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

sealed interface BookshelfEditContentUiState {
    fun copy2(displayName: String): BookshelfEditContentUiState

    data class SmbServer(
        val displayName: String = "",
        val isDisplayNameError: Boolean = false,
        val host: String = "",
        val isHostError: Boolean = false,
        val port: String = "",
        val isPortError: Boolean = false,
        val path: String = "",
        val authMethod: AuthMethod = AuthMethod.Guest,
        val domain: String = "",
        val username: String = "",
        val isUsernameError: Boolean = false,
        val password: String = "",
        val isPasswordError: Boolean = false,
    ) : BookshelfEditContentUiState {

        override fun copy2(displayName: String): BookshelfEditContentUiState {
            return copy(displayName = displayName)
        }
    }

    data class DeviceStorage(
        val displayName: String = "",
        val isDisplayNameError: Boolean = false,
        val dir: String = "",
        val validate: Boolean = false,
    ) : BookshelfEditContentUiState {
        override fun copy2(displayName: String): BookshelfEditContentUiState {
            return copy(displayName = displayName)
        }
    }
}

enum class AuthMethod {
    Guest,
    UserPassword
}
