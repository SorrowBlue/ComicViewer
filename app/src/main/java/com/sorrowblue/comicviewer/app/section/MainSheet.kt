package com.sorrowblue.comicviewer.app.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.sorrowblue.comicviewer.app.component.NavHostWithSharedAxisX
import com.sorrowblue.comicviewer.app.mainGraphRoute

@Composable
@OptIn(ExperimentalMaterialNavigationApi::class)
internal fun MainSheet(
    bottomSheetNavigator: BottomSheetNavigator,
    navController: NavHostController,
    startDestination: String,
    navGraph: NavGraphBuilder.(NavHostController, PaddingValues) -> Unit,
    contentPadding: PaddingValues,
) {
    ModalBottomSheetLayout(bottomSheetNavigator) {
        NavHostWithSharedAxisX(
            navController = navController,
            route = mainGraphRoute,
            startDestination = startDestination,
            modifier = Modifier
        ) {
            navGraph(navController, contentPadding)
        }
    }
}
