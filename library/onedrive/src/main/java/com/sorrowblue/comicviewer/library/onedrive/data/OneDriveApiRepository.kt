package com.sorrowblue.comicviewer.library.onedrive.data

import android.content.Context
import com.microsoft.graph.models.User
import com.microsoft.graph.requests.DriveItemCollectionPage
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.flow.Flow

interface OneDriveApiRepository {

    companion object {
        private var instance: OneDriveApiRepository? = null

        @Synchronized
        fun getInstance(context: Context): OneDriveApiRepository = instance
            ?: OneDriveApiRepositoryImpl(AuthenticationProvider.getInstance(context)).also {
                instance = it
            }
    }

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
