package com.sorrowblue.comicviewer.feature.tutorial

import androidx.lifecycle.ViewModel
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerOperationSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class TutorialViewModel @Inject constructor(
    val splitInstallManager: SplitInstallManager,
    val viewerOperationSettingsUseCase: ManageViewerOperationSettingsUseCase,
) : ViewModel()
