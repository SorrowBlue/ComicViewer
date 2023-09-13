package com.sorrowblue.comicviewer.feature.library.section

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Download
import androidx.compose.material.icons.twotone.History
import androidx.compose.ui.graphics.vector.ImageVector
import com.sorrowblue.comicviewer.feature.library.R
import com.sorrowblue.comicviewer.feature.library.component.AddOnItemState
import com.sorrowblue.comicviewer.domain.AddOn as DomainAddOn
import com.sorrowblue.comicviewer.framework.resource.R as FrameworkResourceR

sealed interface Feature {

    enum class Basic(val label: Int, val icon: ImageVector) : Feature {
        History(R.string.library_label_history, Icons.TwoTone.History),
        Download(R.string.library_label_download, Icons.TwoTone.Download)
    }

    sealed interface AddOn : Feature {

        val label: Int
        val icon: Int
        val state: AddOnItemState
        val addOn: DomainAddOn

        fun copy2(state: AddOnItemState) = when (this) {
            is Box -> copy(state = state)
            is Dropbox -> copy(state = state)
            is GoogleDrive -> copy(state = state)
            is OneDrive -> copy(state = state)
        }

        data class GoogleDrive(override val state: AddOnItemState) : AddOn {
            override val label = R.string.library_label_googledrive
            override val icon = FrameworkResourceR.drawable.ic_google_drive_icon_2020
            override val addOn = DomainAddOn.GoogleDrive
        }

        data class OneDrive(override val state: AddOnItemState) : AddOn {
            override val label = R.string.library_label_onedrive
            override val icon = FrameworkResourceR.drawable.ic_microsoft_office_onedrive
            override val addOn = DomainAddOn.OneDrive
        }

        data class Dropbox(override val state: AddOnItemState) : AddOn {
            override val label = R.string.library_label_dropbox
            override val icon = FrameworkResourceR.drawable.ic_dropbox_tab_32
            override val addOn = DomainAddOn.Dropbox
        }

        data class Box(override val state: AddOnItemState) : AddOn {
            override val label = R.string.library_label_box
            override val icon = FrameworkResourceR.drawable.ic_box_blue_cmyk
            override val addOn = DomainAddOn.Box
        }
    }
}
