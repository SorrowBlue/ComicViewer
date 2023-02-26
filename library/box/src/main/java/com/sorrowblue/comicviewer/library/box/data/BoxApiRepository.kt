package com.sorrowblue.comicviewer.library.box.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.box.sdk.BoxItem
import com.box.sdk.BoxUser
import java.io.OutputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi

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

    @OptIn(ExperimentalSerializationApi::class)
    companion object {

        private var instance: BoxApiRepository? = null

        fun getInstance(context: Context) =
            instance ?: BoxApiRepositoryImpl(context.boxConnectionStateDataStore).also {
                instance = it
            }

        private val Context.boxConnectionStateDataStore: DataStore<BoxConnectionState> by dataStore(
            fileName = "box_connection_state.pb", serializer = BoxConnectionState.Serializer()
        )
    }
}
