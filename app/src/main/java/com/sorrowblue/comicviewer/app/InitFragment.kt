package com.sorrowblue.comicviewer.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal class InitFragment : FrameworkFragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val commonViewModel: CommonViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = View(requireContext())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            if (!mainViewModel.isTutorial.first()) {
                navigate(MobileNavigationDirections.actionGlobalTutorial())
                commonViewModel.shouldKeepOnScreen = false
            } else if (mainViewModel.securitySettingsFlow.first().password != null) {
                navigate(MobileNavigationDirections.actionGlobalAuth())
                commonViewModel.shouldKeepOnScreen = false
            } else {
                navigate(MobileNavigationDirections.actionGlobalBookshelf())
                commonViewModel.shouldKeepOnScreen = false
            }
        }
    }
}
