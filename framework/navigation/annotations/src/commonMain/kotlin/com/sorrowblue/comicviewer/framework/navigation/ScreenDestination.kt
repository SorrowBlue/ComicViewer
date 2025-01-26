package com.sorrowblue.comicviewer.framework.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface ScreenDestination {
    val route: KClass<*>
    val typeMap: Map<KType, NavType<*>>
    val style: DestinationStyle

    @Composable
    fun NavBackStackEntry.Content(navController: NavController)
}
