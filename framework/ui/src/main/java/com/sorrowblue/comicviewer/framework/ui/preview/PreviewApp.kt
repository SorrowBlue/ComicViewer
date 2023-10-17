package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sorrowblue.comicviewer.framework.ui.MobilePreviews
import com.sorrowblue.comicviewer.framework.ui.responsive.rememberResponsiveLayoutState

@MobilePreviews
@Composable
fun PreviewApp() {
    val navController = rememberNavController()
    val state = rememberResponsiveLayoutState()
    WireFrameLayout(
        state = state,
        onClickItem = {
            when (it) {
                0 -> navController.navigate("basic")
                1 -> navController.navigate("WithAppbar")
                2 -> navController.navigate("WithAppbarList")
                3 -> navController.navigate("AppbarListSide")
            }
        }
    ) { contentPadding ->
        NavHost(navController = navController, startDestination = "AppbarListSide") {
            composable("default") {
                Default(contentPadding, navController::popBackStack)
                state.navigationState.show()
            }
            composable("WithAppbar") {
                WithAppbar(contentPadding, navController::popBackStack)
                state.navigationState.show()
            }
            composable("WithAppbarList") {
                WithAppbarList(contentPadding, navController::popBackStack)
                state.navigationState.show()
            }
            composable("AppbarListSide") {
                AppbarListSide(contentPadding, navController::popBackStack)
                state.navigationState.show()
            }
            composable("basic") {
                Basic(contentPadding, navController::popBackStack)
                state.navigationState.show()
            }
//            composable("sidesheet") {
//                PreviewResponsiveLayout(contentPadding)
//                state.navigationState.show()
//            }
            composable("fullscreen") {
                FullScreen(contentPadding, navController::popBackStack)
                state.navigationState.hide()
            }
        }
    }
}
