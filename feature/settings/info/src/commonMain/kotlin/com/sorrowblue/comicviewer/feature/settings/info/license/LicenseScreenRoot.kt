package com.sorrowblue.comicviewer.feature.settings.info.license

import androidx.compose.runtime.Composable

@Composable
context(context: LicenseScreenContext)
fun LicenseScreenRoot(onBackClick: () -> Unit) {
    val state = rememberLicenseScreenState()
    LicenseScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onLibraryClick = state::onLibraryClick,
    )

    if (state.uiState.openDialog != null) {
        LicenseDialog(
            library = state.uiState.openDialog!!,
            onDismissRequest = {
                state.closeDialog()
            },
        )
    }
}
