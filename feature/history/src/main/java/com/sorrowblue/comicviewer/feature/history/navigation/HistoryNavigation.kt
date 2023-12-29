package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.history.HistoryRoute
import com.sorrowblue.comicviewer.framework.ui.ComposeValue

const val HistoryScreenRoute = "history"

fun NavController.navigateToHistory() = navigate(HistoryScreenRoute)

context(ComposeValue)
fun NavGraphBuilder.historyScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFileClick: (Book) -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    composable(route = HistoryScreenRoute) { navBackStackEntry ->
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
