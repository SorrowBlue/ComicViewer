package com.sorrowblue.comicviewer.library.box.data

import com.box.sdk.BoxItem
import com.box.sdk.BoxUser
import java.io.OutputStream
import kotlinx.coroutines.flow.Flow

interface BoxApiRepository {

    fun isAuthenticated(): Flow<Boolean>

    val userInfoFlow: Flow<BoxUser.Info?>

    suspend fun currentUser(): BoxUser?
    suspend fun signOut()
    suspend fun download(path: String, outputStream: OutputStream, progress: (Double) -> Unit)
    suspend fun list(path: String?, limit: Long, offset: Long = 0): List<BoxItem.Info>?
    suspend fun authenticate(state: String, code: String, onSuccess: () -> Unit)
    suspend fun fileThumbnail(id: String): String?
    suspend fun accessToken(): String
}
