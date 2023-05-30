package com.sorrowblue.comicviewer.library.dropbox.signin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.users.FullAccount
import com.sorrowblue.comicviewer.library.dropbox.data.DropBoxApiRepositoryImpl
import kotlinx.coroutines.flow.Flow

internal class DropBoxSignInViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DropBoxApiRepositoryImpl(application)

    suspend fun storeCredential(dbxCredential: DbxCredential) {
        repository.storeCredential(dbxCredential)
    }

    fun currentAccount(): Flow<FullAccount?> {
        return repository.accountFlow
    }
}
