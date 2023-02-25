package com.sorrowblue.comicviewer.framework.ui.navigation

import androidx.navigation.NavHostController
import androidx.navigation.dynamicfeatures.DynamicInstallManager
import androidx.navigation.dynamicfeatures.fragment.DynamicNavHostFragment
import androidx.navigation.plusAssign

class FrameworkDynamicNavHostFragment : DynamicNavHostFragment() {

    override fun onCreateNavHostController(navHostController: NavHostController) {
        super.onCreateNavHostController(navHostController)
        val installManager = DynamicInstallManager(requireContext(), createSplitInstallManager())
        val navigatorProvider = navHostController.navigatorProvider
        navigatorProvider +=
            FrameworkDynamicFragmentNavigator(
                requireContext(),
                childFragmentManager,
                id,
                installManager
            )
    }
}
