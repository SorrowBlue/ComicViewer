package com.sorrowblue.comicviewer.library.dropbox.data

import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.files.ListFolderResult
import com.dropbox.core.v2.users.FullAccount
import java.io.OutputStream
import kotlinx.coroutines.flow.Flow

interface DropBoxApiRepository {

    fun isAuthenticated(): Flow<Boolean>
    suspend fun storeCredential(dbxCredential: DbxCredential)

    val accountFlow: Flow<FullAccount?>

    suspend fun currentAccount(): FullAccount?
    suspend fun signOut()
    suspend fun list(path: String, limit: Long, cursor: String?): ListFolderResult?
    suspend fun download(path: String, outputStream: OutputStream, progress: (Double) -> Unit)
}
