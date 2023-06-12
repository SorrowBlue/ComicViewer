package com.sorrowblue.comicviewer.app.ktx

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sorrowblue.comicviewer.app.DisableableHideBottomViewOnScrollBehavior
import logcat.logcat

@BindingAdapter("isShown")
fun BottomNavigationView.isShown(isShown: Boolean?) {
    if (isShown == null) return
    val lp = layoutParams as CoordinatorLayout.LayoutParams
    val behavior = lp.behavior as DisableableHideBottomViewOnScrollBehavior
    doOnPreDraw {
        if (isShown) {
            logcat { "isShown true" }
            isVisible = true
            behavior.isEnabled = true
            behavior.slideUp(this)
        } else {
            logcat { "isShown false" }
            behavior.isEnabled = false
            behavior.slideDown(this)
            isVisible = false
        }
    }
}

internal fun BottomNavigationView.setupWithNavControllerApp(navController: NavController) {
    setupWithNavController(navController)
    setOnItemReselectedListener {
        if (navController.currentBackStack.value.lastOrNull()?.destination?.parent?.id != it.itemId) {
            navController.popBackStack()
        }
    }
}
