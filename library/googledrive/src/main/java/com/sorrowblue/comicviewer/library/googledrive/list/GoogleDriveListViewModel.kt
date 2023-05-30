package com.sorrowblue.comicviewer.library.googledrive.list

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.library.filelist.LibraryFileListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

interface GoogleDriveApiRepository {
    val googleSignInAccount: MutableStateFlow<GoogleSignInAccount?>
    fun updateAccount()
    val driverServiceFlow: Flow<Drive>
}

internal class GoogleDriveApiRepositoryImpl(private val context: Context) :
    GoogleDriveApiRepository {

    private val credential =
        GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_READONLY))

    override val googleSignInAccount =
        MutableStateFlow(GoogleSignIn.getLastSignedInAccount(context))

    override val driverServiceFlow = googleSignInAccount.filterNotNull().map {
        credential.selectedAccount = it.account
        Drive.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
            .setApplicationName("ComicViewer").build()
    }

    override fun updateAccount() {
        googleSignInAccount.value = GoogleSignIn.getLastSignedInAccount(context)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
internal class GoogleDriveListViewModel(
    private val repository: GoogleDriveApiRepository,
    override val savedStateHandle: SavedStateHandle
) : LibraryFileListViewModel(), SupportSafeArgs, DefaultLifecycleObserver {

    companion object {

        fun Factory(repository: GoogleDriveApiRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Create a SavedStateHandle for this ViewModel from extras
                val savedStateHandle = extras.createSavedStateHandle()
                return GoogleDriveListViewModel(repository, savedStateHandle) as T
            }
        }
    }

    private val args: GoogleDriveListFragmentArgs by navArgs()

    private val parent = args.parent ?: "root"

    override val isAuthenticated = repository.googleSignInAccount.map { it != null }

    val googleSignInAccount = repository.googleSignInAccount

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        repository.updateAccount()
    }

    override val transitionName = args.transitionName
    override val pagingDataFlow = repository.driverServiceFlow.flatMapLatest {
        Pager(PagingConfig(20)) { DrivePagingSource(it, parent) }.flow
    }.cachedIn(viewModelScope)
}
