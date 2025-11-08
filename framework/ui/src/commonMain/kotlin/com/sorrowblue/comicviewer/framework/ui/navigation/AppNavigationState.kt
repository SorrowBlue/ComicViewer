package com.sorrowblue.comicviewer.framework.ui.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

interface AppNavigationState {
    fun addToBackStack(screenKey: ScreenKey)

    fun onBackPressed()

    val currentBackStack: NavBackStack<NavKey>
}
