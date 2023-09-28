package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController

@Stable
class CommonViewModel : ViewModel() {

    var canScroll by mutableStateOf(false)
}

@Composable
inline fun <reified T : ViewModel> viewModelScopedTo(route: String): T {
    val navController = LocalNavController.current
    val parentEntry =
        remember(navController.currentBackStackEntry) { navController.getBackStackEntry(route) }
    return hiltViewModel(parentEntry)
}

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No LocalNavController provided")
}
