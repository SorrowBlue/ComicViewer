package com.sorrowblue.comicviewer.feature.library

import androidx.lifecycle.ViewModel
import com.google.android.play.core.ktx.requestSessionStates
import com.google.android.play.core.ktx.status
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class LibraryViewModel(
    val splitInstallManager: SplitInstallManager,
) : ViewModel()

private val allowInstallStatus = listOf(
    SplitInstallSessionStatus.INSTALLED,
    SplitInstallSessionStatus.FAILED,
    SplitInstallSessionStatus.CANCELED,
)

internal suspend fun SplitInstallManager.isInstallAllowed(): Boolean {
    val states = requestSessionStates()
    return states.isEmpty() || states.all { it.status in allowInstallStatus }
}
