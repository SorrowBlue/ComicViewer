package com.sorrowblue.comicviewer.settings.feature

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.errorCode
import com.google.android.play.core.ktx.status
import com.google.android.play.core.ktx.totalBytesToDownload
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

enum class Feature(
    val moduleName: String,
    @get:DrawableRes val icon: Int,
    @get:StringRes val title: Int,
    @get:StringRes val description: Int,
) {

    ARCHIVE(
        "zip",
        com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_twotone_folder_zip_24,
        R.string.settings_feature_title_archive,
        R.string.settings_feature_desc_archive
    ),
    DOCUMENT(
        "document",
        com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_twotone_picture_as_pdf_24,
        R.string.settings_feature_title_document,
        R.string.settings_feature_desc_document
    )
}

data class FeatureItem(val feature: Feature, val status: InstallStatus, val sessionId: Int = -1)

sealed class InstallStatus {

    companion object {
        fun from(state: SplitInstallSessionState): InstallStatus {
            return when (state.status) {
                SplitInstallSessionStatus.INSTALLED -> Installed
                SplitInstallSessionStatus.CANCELED -> Cancelled
                SplitInstallSessionStatus.FAILED -> Failed(state.errorCode)
                SplitInstallSessionStatus.CANCELING, SplitInstallSessionStatus.DOWNLOADED, SplitInstallSessionStatus.DOWNLOADING, SplitInstallSessionStatus.INSTALLING, SplitInstallSessionStatus.PENDING -> Progress(
                    state.status, state.bytesDownloaded, state.totalBytesToDownload
                )

                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    TODO()
                }

                else -> Failed(SplitInstallErrorCode.INTERNAL_ERROR)
            }
        }
    }

    data class Progress(
        @SplitInstallSessionStatus val status: Int, val bytesDownloaded: Long, val bytesTotal: Long
    ) : InstallStatus()

    object Cancelled : InstallStatus()
    data class Failed(@SplitInstallErrorCode val errorCode: Int) : InstallStatus()
    object Installed : InstallStatus()
    object NotInstall : InstallStatus()
}

