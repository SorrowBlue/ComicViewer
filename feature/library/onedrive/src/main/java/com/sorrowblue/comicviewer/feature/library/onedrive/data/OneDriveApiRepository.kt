package com.sorrowblue.comicviewer.feature.library.onedrive.data

import android.app.Activity
import com.microsoft.graph.models.User
import com.microsoft.graph.requests.DriveItemCollectionPage
import com.microsoft.identity.client.IAccount
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.flow.StateFlow

interface OneDriveApiRepository {

    val accountFlow: StateFlow<IAccount?>

    suspend fun list(
        driveId: String?,
        itemId: String,
        limit: Int,
        skipToken: String?,
    ): DriveItemCollectionPage

    suspend fun getCurrentUser(): User?
    suspend fun driveId(): String
    suspend fun profileImage(): InputStream
    suspend fun download(
        driveId: String,
        itemId: String,
        outputStream: OutputStream,
        onProgress: (Double) -> Unit,
    )

    fun loadAccount()
    suspend fun login(activity: Activity)
    suspend fun logout()
    suspend fun initialize()
}
