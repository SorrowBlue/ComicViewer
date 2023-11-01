package com.sorrowblue.comicviewer.feature.favorite.create.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import com.sorrowblue.comicviewer.feature.favorite.create.FavoriteCreateRoute

internal const val favoriteCreateRoute = "favorite/create"

fun NavGraphBuilder.favoriteCreateScreen(onDismissRequest: () -> Unit) {
    dialog(favoriteCreateRoute) {
        FavoriteCreateRoute(onDismissRequest = onDismissRequest)
    }
}

fun NavController.navigateToFavoriteCreate() {
    this.navigate(favoriteCreateRoute)
}