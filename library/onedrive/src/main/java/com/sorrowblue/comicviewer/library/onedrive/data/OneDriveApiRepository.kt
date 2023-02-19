package com.sorrowblue.comicviewer.library.onedrive.data

import com.microsoft.graph.models.User
import com.microsoft.graph.requests.DriveItemCollectionPage
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.flow.Flow

interface OneDriveApiRepository {
    suspend fun list(
        driveId: String?,
        itemId: String,
        limit: Int,
        skipToken: String?
    ): DriveItemCollectionPage

    suspend fun getCurrentUser(): User?
    val currentUserFlow: Flow<User?>
    suspend fun driveId(): String
    suspend fun profileImage(): InputStream
    val isAuthenticated: Flow<Boolean?>
    suspend fun download(
        driveId: String,
        itemId: String,
        outputStream: OutputStream,
        onProgress: (Double) -> Unit
    )
}
