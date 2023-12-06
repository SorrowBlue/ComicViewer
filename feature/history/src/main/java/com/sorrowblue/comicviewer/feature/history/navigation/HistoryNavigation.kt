package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.history.HistoryRoute

private const val HistoryRoute = "history"
private const val HistoryGraphRoute = "${HistoryRoute}_graph"

fun NavController.navigateToHistoryGroup() = navigate(HistoryGraphRoute)

internal fun NavGraphBuilder.historyScreen(
    contentPadding: PaddingValues,
    onFileClick: (Book) -> Unit,
    onSettingsClick: () -> Unit,
) {
    composable(route = HistoryRoute) {
        HistoryRoute(
            contentPadding = contentPadding,
            onFileClick = onFileClick,
            onSettingsClick = onSettingsClick
        )
    }
}

fun NavGraphBuilder.historyGroup(
    contentPadding: PaddingValues,
    navigateToBook: (Book) -> Unit,
    onSettingsClick: () -> Unit,
) {
    navigation(route = HistoryGraphRoute, startDestination = HistoryRoute) {
        historyScreen(
            contentPadding = contentPadding,
            onFileClick = navigateToBook,
            onSettingsClick = onSettingsClick
        )
    }
}
