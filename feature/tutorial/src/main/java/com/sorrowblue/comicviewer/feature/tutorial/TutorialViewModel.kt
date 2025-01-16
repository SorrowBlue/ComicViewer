package com.sorrowblue.comicviewer.feature.tutorial

import androidx.lifecycle.ViewModel
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerOperationSettingsUseCase
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
internal class TutorialViewModel(
    val splitInstallManager: SplitInstallManager,
    val viewerOperationSettingsUseCase: ManageViewerOperationSettingsUseCase,
) : ViewModel()
