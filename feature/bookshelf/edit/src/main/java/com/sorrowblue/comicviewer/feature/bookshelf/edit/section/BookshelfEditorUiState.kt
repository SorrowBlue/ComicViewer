package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

sealed interface BookshelfEditorUiState {

    data class SmbServer(
        val displayName: String = "",
        val isDisplayNameError: Boolean = false,
        val host: String = "",
        val isHostError: Boolean = false,
        val port: String = "",
        val isPortError: Boolean = false,
        val path: String = "",
        val authMethod: AuthMethod = AuthMethod.GUEST,
        val domain: String = "",
        val username: String = "",
        val isUsernameError: Boolean = false,
        val password: String = "",
        val isPasswordError: Boolean = false
    ) : BookshelfEditorUiState

    data class DeviceStorage(
        val displayName: String = "",
        val isDisplayNameError: Boolean = false,
        val dir: String = "",
        val validate: Boolean = false,
    ) : BookshelfEditorUiState
}

enum class AuthMethod {
    GUEST,
    USERPASS
}
