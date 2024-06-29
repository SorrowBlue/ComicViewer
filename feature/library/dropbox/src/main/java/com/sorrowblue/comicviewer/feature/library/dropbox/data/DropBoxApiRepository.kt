package com.sorrowblue.comicviewer.feature.library.dropbox.data

import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.files.ListFolderResult
import com.dropbox.core.v2.users.FullAccount
import java.io.OutputStream
import kotlinx.coroutines.flow.Flow

internal interface DropBoxApiRepository {

    suspend fun storeCredential(dbxCredential: DbxCredential)

    val accountFlow: Flow<FullAccount?>

    suspend fun currentAccount(): FullAccount?
    suspend fun signOut()
    suspend fun list(path: String, limit: Long, cursor: String?): ListFolderResult?
    suspend fun download(
        path: String,
        outputStream: OutputStream,
        progress: suspend (Double) -> Unit,
    )
    fun startSignIn()
    suspend fun dbxCredential(): Boolean

    val isAuthenticated: Flow<Boolean>
    suspend fun refresh()
    suspend fun downloadLink(path: String): String
}
