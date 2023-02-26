package com.sorrowblue.comicviewer.library

sealed interface CloudStorage : Library {
    val isInstalled: Boolean
    val iconRes: Int
    val titleRes: Int
    override val viewType get() = LibraryListViewType.CLOUD_STORAGE

    data class GoogleDrive(override val isInstalled: Boolean) : CloudStorage {
        override val iconRes =
            com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_google_drive_icon_2020
        override val titleRes = R.string.library_list_label_google_drive
    }

    data class OneDrive(override val isInstalled: Boolean) : CloudStorage {
        override val iconRes =
            com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_microsoft_office_onedrive
        override val titleRes = R.string.library_list_label_one_drive
    }

    data class Dropbox(override val isInstalled: Boolean) : CloudStorage {
        override val iconRes =
            com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_dropbox_tab_32
        override val titleRes = R.string.library_list_label_dropbox
    }

    data class Box(override val isInstalled: Boolean) : CloudStorage {
        override val iconRes =
            com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_box_blue_cmyk
        override val titleRes = R.string.library_list_label_box
    }
}
