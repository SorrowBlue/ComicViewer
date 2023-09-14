package com.sorrowblue.comicviewer.feature.main

import androidx.navigation.NavController

interface MainNestedGraphStateHolder {
    val startDestination: String
    fun routeToTab(route: String): MainScreenTab?
    fun onTabSelected(navController: NavController, tab: MainScreenTab)
}
