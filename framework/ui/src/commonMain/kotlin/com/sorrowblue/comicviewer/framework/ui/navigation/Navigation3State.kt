package com.sorrowblue.comicviewer.framework.ui.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

interface Navigation3State {

    fun addToBackStack(screenKey: NavKey)

    fun onBackPressed()

    val currentBackStack: NavBackStack<NavKey>
}
