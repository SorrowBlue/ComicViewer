package com.sorrowblue.comicviewer.app.ktx

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sorrowblue.comicviewer.app.DisableableHideBottomViewOnScrollBehavior

fun BottomNavigationView.isShown(isShown: Boolean?) {
    if (isShown == null) return
    val lp = layoutParams as CoordinatorLayout.LayoutParams
    val behavior = lp.behavior as DisableableHideBottomViewOnScrollBehavior
    doOnPreDraw {
        if (isShown) {
            isVisible = true
            behavior.isEnabled = true
            behavior.slideUp(this)
        } else {
            behavior.isEnabled = false
            behavior.slideDown(this)
            isVisible = false
        }
    }
}

fun <T: Fragment> FragmentContainerView.findNavController() = getFragment<T>().findNavController()
