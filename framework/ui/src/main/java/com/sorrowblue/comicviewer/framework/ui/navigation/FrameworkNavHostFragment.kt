package com.sorrowblue.comicviewer.framework.ui.navigation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.plusAssign
import com.sorrowblue.comicviewer.framework.ui.R

class FrameworkNavHostFragment : NavHostFragment() {

    override fun onCreateNavHostController(navHostController: NavHostController) {
        val containerId =
            if (id != 0 && id != View.NO_ID) id else androidx.navigation.fragment.R.id.nav_host_fragment_container
        navController.navigatorProvider +=
            FrameworkFragmentNavigator(requireContext(), childFragmentManager, containerId)
        super.onCreateNavHostController(navHostController)
    }
}

@Navigator.Name("framework-fragment")
class FrameworkFragmentNavigator(
    context: Context,
    fragmentManager: FragmentManager,
    containerId: Int
) : FragmentNavigator(context, fragmentManager, containerId) {
    override fun createDestination() = Destination(this)

    @NavDestination.ClassType(Fragment::class)
    class Destination(fragmentNavigator: Navigator<out FragmentNavigator.Destination>) :
        FragmentNavigator.Destination(fragmentNavigator) {

        var isVisibleBottomNavigation: Boolean = false
            private set
        var isVisibleFab: Boolean = false
            private set
        var fabIcon: Int = View.NO_ID
            private set
        var fabLabel: Int = View.NO_ID
            private set

        @Suppress("unused")
        constructor(navigatorProvider: NavigatorProvider) :
                this(navigatorProvider.getNavigator(FrameworkFragmentNavigator::class.java))

        override fun onInflate(context: Context, attrs: AttributeSet) {
            super.onInflate(context, attrs)
            context.resources.obtainAttributes(attrs, R.styleable.FrameworkFragmentNavigator).use {
                isVisibleBottomNavigation =
                    it.getBoolean(R.styleable.FrameworkFragmentNavigator_bottom_nav_visible, false)
                isVisibleFab =
                    it.getBoolean(R.styleable.FrameworkFragmentNavigator_fab_visible, false)
                fabIcon = it.getResourceId(R.styleable.FrameworkFragmentNavigator_fab_icon, View.NO_ID)
                fabLabel = it.getResourceId(R.styleable.FrameworkFragmentNavigator_fab_label, View.NO_ID)
            }
        }
    }
}
