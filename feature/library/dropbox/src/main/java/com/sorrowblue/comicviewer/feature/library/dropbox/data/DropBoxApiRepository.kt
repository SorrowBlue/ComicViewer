package com.sorrowblue.comicviewer.feature.library.dropbox.data

import android.content.Context
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
    suspend fun download(path: String, outputStream: OutputStream, progress: (Double) -> Unit)

    companion object {

        private var instance: DropBoxApiRepository? = null

        fun getInstance(context: Context) = instance ?: DropBoxApiRepositoryImpl(
            context,
        ).also { instance = it }
    }

    val isAuthenticated: Flow<Boolean>
}
