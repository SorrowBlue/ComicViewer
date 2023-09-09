package com.sorrowblue.comicviewer.library

import com.sorrowblue.comicviewer.framework.resource.R as FrameworkResourceR

sealed interface CloudStorage : Library {

    companion object {
        val entries = listOf(
            GoogleDrive(false),
            OneDrive(false),
            Dropbox(false),
            Box(false)
        )
    }

    val isInstalled: Boolean
    val iconRes: Int
    val titleRes: Int

    data class GoogleDrive(override val isInstalled: Boolean) : CloudStorage {
        override val iconRes =
            FrameworkResourceR.drawable.ic_google_drive_icon_2020
        override val titleRes = R.string.library_list_label_google_drive
    }

    data class OneDrive(override val isInstalled: Boolean) : CloudStorage {
        override val iconRes =
            FrameworkResourceR.drawable.ic_microsoft_office_onedrive
        override val titleRes = R.string.library_list_label_one_drive
    }

    data class Dropbox(override val isInstalled: Boolean) : CloudStorage {
        override val iconRes =
            FrameworkResourceR.drawable.ic_dropbox_tab_32
        override val titleRes = R.string.library_list_label_dropbox
    }

    data class Box(override val isInstalled: Boolean) : CloudStorage {
        override val iconRes =
            FrameworkResourceR.drawable.ic_box_blue_cmyk
        override val titleRes = R.string.library_list_label_box
    }
}
