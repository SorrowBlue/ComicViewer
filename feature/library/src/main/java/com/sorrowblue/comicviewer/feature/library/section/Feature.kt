package com.sorrowblue.comicviewer.feature.library.section

import androidx.compose.ui.graphics.vector.ImageVector
import com.sorrowblue.comicviewer.feature.library.R
import com.sorrowblue.comicviewer.feature.library.component.AddOnItemState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.brand.BrandBox
import com.sorrowblue.comicviewer.framework.designsystem.icon.brand.BrandDropbox
import com.sorrowblue.comicviewer.framework.designsystem.icon.brand.BrandGoogleDrive
import com.sorrowblue.comicviewer.framework.designsystem.icon.brand.BrandOnedrive
import com.sorrowblue.comicviewer.domain.model.AddOn as DomainAddOn

sealed interface Feature {

    enum class Basic(val label: Int, val icon: ImageVector) : Feature {
        History(R.string.library_label_history, ComicIcons.History),
    }

    sealed interface AddOn : Feature {

        val label: Int
        val icon: ImageVector
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
            override val icon = ComicIcons.BrandGoogleDrive
            override val addOn = DomainAddOn.GoogleDrive
        }

        data class OneDrive(override val state: AddOnItemState) : AddOn {
            override val label = R.string.library_label_onedrive
            override val icon = ComicIcons.BrandOnedrive
            override val addOn = DomainAddOn.OneDrive
        }

        data class Dropbox(override val state: AddOnItemState) : AddOn {
            override val label = R.string.library_label_dropbox
            override val icon = ComicIcons.BrandDropbox
            override val addOn = DomainAddOn.Dropbox
        }

        data class Box(override val state: AddOnItemState) : AddOn {
            override val label = R.string.library_label_box
            override val icon = ComicIcons.BrandBox
            override val addOn = DomainAddOn.Box
        }
    }
}
