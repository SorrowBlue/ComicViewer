package com.sorrowblue.comicviewer.library.box.data

import androidx.datastore.core.DataStore
import com.box.sdk.BoxAPIConnection
import com.box.sdk.BoxFile
import com.box.sdk.BoxFolder
import com.box.sdk.BoxItem
import com.box.sdk.BoxUser
import com.box.sdk.PagingParameters
import com.box.sdk.SortParameters
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import logcat.logcat

private val CLIENT_ID = "nihdm7dthg9lm7m3b41bpw7jp7b0lb9z"
private val CLIENT_SECRET = "znx5P0kuwJ5LNqF3UG8Yw8Xs05dw4zNq"

internal class BoxApiRepositoryImpl(
    private val dropboxCredentialDataStore: DataStore<BoxConnectionState>,
) : BoxApiRepository {

    private val apiFlow = dropboxCredentialDataStore.data.distinctUntilChangedBy { it.state }.map {
        if (it.state != null) {
            withContext(Dispatchers.IO) {
                val restoreApi = BoxAPIConnection.restore(CLIENT_ID, CLIENT_SECRET, it.state)
                kotlin.runCatching {
                    logcat { "ユーザ取得。" }
                    BoxUser.getCurrentUser(restoreApi)
                }.fold({
                    logcat { "ユーザ取得。成功、id=${it.id}" }
                    restoreApi
                }, {
                    it.printStackTrace()
                    logcat { "ユーザ取得。失敗" }
                    dropboxCredentialDataStore.updateData { it.copy(state = null) }
                    null
                })
            }
        } else {
            null
        }
    }

    override suspend fun authenticate(state: String, code: String, onSuccess: () -> Unit) {
        val api = BoxAPIConnection(CLIENT_ID, CLIENT_SECRET)
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                api.authenticate(code)
            }
        }.onSuccess {
            logcat { "認証成功。${api.accessToken},${api.save()}" }
            withContext(Dispatchers.IO) {
                dropboxCredentialDataStore.updateData { it.copy(state = api.save()) }
            }
            onSuccess()
        }.onFailure {
            logcat { "認証失敗" }
            dropboxCredentialDataStore.updateData { it.copy(state = null) }
        }
    }

    override val userInfoFlow = apiFlow.map {
        if (it != null) {
            kotlin.runCatching {
                withContext(Dispatchers.IO) {
                    BoxUser.getCurrentUser(it).getInfo("id", "avatar_url", "name")
                }
            }.getOrNull()
        } else {
            null
        }
    }

    override suspend fun currentUser(): BoxUser? {
        val api = apiFlow.first() ?: return null
        return kotlin.runCatching { BoxUser.getCurrentUser(api) }.getOrNull()
    }

    override fun isAuthenticated(): Flow<Boolean> {
        return apiFlow.map { it != null }
    }

    override suspend fun signOut() {
        withContext(Dispatchers.IO) {
            apiFlow.first()?.let { api ->
                api.revokeToken()
                dropboxCredentialDataStore.updateData { it.copy(state = null) }
            }
        }
    }

    override suspend fun list(path: String?, limit: Long, offset: Long): List<BoxItem.Info>? {
        val api = apiFlow.first() ?: return null
        val folder = if (path == null) {
            BoxFolder.getRootFolder(api)
        } else {
            BoxFolder(api, path)
        }
        logcat { "フォルダ, id=${folder.id}, ${folder.getInfo("name").name}" }
        val sorting = SortParameters.none()
        val paging = PagingParameters.offset(offset, limit)
        val iterator = folder.getChildren(sorting, paging, "id", "name", "type","size", "modified_at")
        return iterator.apply {
            forEach {
                logcat { "フォルダ, id=${it.id}, ${it.name}" }
            }
        }.toList().apply {
            logcat { "フォルダリスト size=${size}" }
        }
    }

    override suspend fun fileThumbnail(id: String): String? {
        val api = apiFlow.first() ?: return null
        return withContext(Dispatchers.IO) {
            BoxFile(
                api,
                id
            ).getInfoWithRepresentations("[jpg?dimensions=32x32]").representations.firstOrNull()?.content?.urlTemplate?.replace("{+asset_path}","")
        }
    }

    override suspend fun accessToken(): String {
        return apiFlow.first()?.accessToken.orEmpty()
    }

    override suspend fun download(
        path: String,
        outputStream: OutputStream,
        progress: (Double) -> Unit
    ) {
        val api = apiFlow.first() ?: return
        BoxFile(api, path).download(outputStream) { numBytes, totalBytes ->
            progress.invoke(numBytes.toDouble() / totalBytes)
        }
    }
}
