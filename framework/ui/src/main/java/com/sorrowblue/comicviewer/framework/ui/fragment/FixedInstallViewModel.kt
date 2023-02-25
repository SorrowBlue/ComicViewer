package com.sorrowblue.comicviewer.framework.ui.fragment

import androidx.lifecycle.ViewModel
import androidx.navigation.dynamicfeatures.DynamicInstallMonitor

internal class FixedInstallViewModel : ViewModel() {

    var installMonitor: DynamicInstallMonitor? = null
}
