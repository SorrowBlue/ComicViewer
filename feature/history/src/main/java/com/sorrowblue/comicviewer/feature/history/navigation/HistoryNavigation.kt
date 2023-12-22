package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.history.HistoryRoute

private const val HistoryRoute = "history"

fun NavController.navigateToHistory() = navigate(HistoryRoute)

fun NavGraphBuilder.historyScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFileClick: (Book) -> Unit,
    onFavoriteClick: (File) -> Unit,
    contentPadding: PaddingValues,
) {
    composable(route = HistoryRoute) { navBackStackEntry ->
        with(navBackStackEntry) {
            HistoryRoute(
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick,
                onFileClick = onFileClick,
                onFavoriteClick = onFavoriteClick,
                contentPadding = contentPadding
            )
        }
    }
}
