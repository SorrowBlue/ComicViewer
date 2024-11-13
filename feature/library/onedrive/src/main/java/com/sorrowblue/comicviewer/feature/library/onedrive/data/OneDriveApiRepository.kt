package com.sorrowblue.comicviewer.feature.library.onedrive.data

import android.app.Activity
import com.microsoft.graph.models.DriveItemCollectionResponse
import com.microsoft.graph.models.User
import com.microsoft.identity.client.IAccount
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal interface OneDriveApiRepository {

    val accountFlow: StateFlow<IAccount?>

    suspend fun list(
        itemId: String,
        limit: Int,
        skipToken: String?,
    ): DriveItemCollectionResponse

    suspend fun getCurrentUser(): User?
    suspend fun profileImage(): InputStream
    suspend fun download(
        itemId: String,
        outputStream: OutputStream,
        onProgress: (Double) -> Unit,
    )

    fun loadAccount()
    suspend fun login(activity: Activity)
    suspend fun logout()
    suspend fun initialize()
    val authStatus: StateFlow<AuthStatus>
}
