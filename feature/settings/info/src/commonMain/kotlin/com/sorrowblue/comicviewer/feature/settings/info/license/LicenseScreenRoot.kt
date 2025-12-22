package com.sorrowblue.comicviewer.feature.settings.info.license

import androidx.compose.runtime.Composable

@Composable
context(context: LicenseScreenContext)
internal fun LicenseScreenRoot(onBackClick: () -> Unit) {
    val state = rememberLicenseScreenState()
    LicenseScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onLibraryClick = state::onLibraryClick,
    )

    state.uiState.openDialog?.let { library ->
        LicenseDialog(
            library = library,
            onDismissRequest = {
                state.closeDialog()
            },
        )
    }
}
