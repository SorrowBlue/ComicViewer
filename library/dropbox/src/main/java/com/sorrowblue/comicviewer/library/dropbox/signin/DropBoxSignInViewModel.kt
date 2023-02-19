package com.sorrowblue.comicviewer.library.dropbox.signin

import androidx.lifecycle.ViewModel
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.users.FullAccount
import com.sorrowblue.comicviewer.library.dropbox.data.DropBoxApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

@HiltViewModel
internal class DropBoxSignInViewModel @Inject constructor(
    private val repository: DropBoxApiRepository
) : ViewModel() {

    suspend fun storeCredential(dbxCredential: DbxCredential) {
        delay(2000)
        repository.storeCredential(dbxCredential)
    }

    suspend fun currentAccount(): Flow<FullAccount?> {
        return repository.accountFlow
    }
}
