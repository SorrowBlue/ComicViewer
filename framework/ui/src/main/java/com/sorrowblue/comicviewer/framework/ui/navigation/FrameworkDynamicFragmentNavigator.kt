package com.sorrowblue.comicviewer.framework.ui.navigation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.dynamicfeatures.DynamicExtras
import androidx.navigation.dynamicfeatures.DynamicInstallManager
import androidx.navigation.fragment.FragmentNavigator
import com.sorrowblue.comicviewer.framework.ui.R

@Navigator.Name("fragment")
class FrameworkDynamicFragmentNavigator(
    context: Context,
    manager: FragmentManager,
    containerId: Int,
    private val installManager: DynamicInstallManager
) : FragmentNavigator(context, manager, containerId) {

    override fun createDestination(): Destination = Destination(this)

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ) {
        for (entry in entries) {
            navigate(entry, navOptions, navigatorExtras)
        }
    }

    private fun navigate(
        entry: NavBackStackEntry,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ) {
        val destination = entry.destination
        val extras = navigatorExtras as? DynamicExtras
        if (destination is Destination) {
            val moduleName = destination.moduleName
            if (moduleName != null && installManager.needsInstall(moduleName)) {
                installManager.performInstall(entry, extras, moduleName)
                return
            }
        }
        super.navigate(
            listOf(entry),
            navOptions,
            if (extras != null) extras.destinationExtras else navigatorExtras
        )
    }

    /**
     * Destination for dynamic feature navigator.
     */
    class Destination : FragmentNavigator.Destination {
        var moduleName: String? = null

        var isVisibleBottomNavigation: Boolean = false
            private set
        var isVisibleFab: Boolean = false
            private set
        var fabIcon: Int = View.NO_ID
            private set
        var fabLabel: Int = View.NO_ID
            private set
        @Suppress("unused")
        constructor(navigatorProvider: NavigatorProvider) : super(navigatorProvider)

        constructor(
            fragmentNavigator: Navigator<out FragmentNavigator.Destination>
        ) : super(fragmentNavigator)

        override fun onInflate(context: Context, attrs: AttributeSet) {
            super.onInflate(context, attrs)
            context.withStyledAttributes(attrs, R.styleable.FrameworkDynamicFragmentNavigator) {
                moduleName = getString(R.styleable.FrameworkDynamicFragmentNavigator_moduleName)
                isVisibleBottomNavigation =
                    getBoolean(R.styleable.FrameworkDynamicFragmentNavigator_bottom_nav_visible, false)
                isVisibleFab =
                    getBoolean(R.styleable.FrameworkDynamicFragmentNavigator_fab_visible, false)
                fabIcon = getResourceId(R.styleable.FrameworkDynamicFragmentNavigator_fab_icon, View.NO_ID)
                fabLabel = getResourceId(R.styleable.FrameworkDynamicFragmentNavigator_fab_label, View.NO_ID)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (other == null || other !is Destination) return false
            return super.equals(other) && moduleName == other.moduleName
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + moduleName.hashCode()
            return result
        }
    }
}
