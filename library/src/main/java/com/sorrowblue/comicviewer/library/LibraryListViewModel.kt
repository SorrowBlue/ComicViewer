package com.sorrowblue.comicviewer.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LibraryListViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val manager = SplitInstallManagerFactory.create(application)

    val cloudStorageList = listOf(
        CloudStorage.GoogleDrive(manager.installedModules.contains("googledrive")),
        CloudStorage.OneDrive(manager.installedModules.contains("onedrive")),
        CloudStorage.Dropbox(manager.installedModules.contains("dropbox")),
        CloudStorage.Box(manager.installedModules.contains("box"))
    )
}
