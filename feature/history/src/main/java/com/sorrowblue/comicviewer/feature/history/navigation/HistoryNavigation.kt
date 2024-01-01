package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.history.HistoryScreen
import com.sorrowblue.comicviewer.feature.history.destinations.HistoryScreenDestination
import com.sorrowblue.comicviewer.framework.ui.ComposeValue

context(ComposeValue)
fun NavGraphBuilder.historyScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFileClick: (Book) -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    composable(HistoryScreenDestination) {
        HistoryScreen(
            savedStateHandle = navBackStackEntry.savedStateHandle,
            contentPadding = contentPadding,
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick,
            onFileClick = onFileClick,
            onFavoriteClick = onFavoriteClick,
        )
    }
}
