package com.sorrowblue.comicviewer.library.googledrive.list

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingViewModel
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

@HiltViewModel
internal class GoogleDriveListViewModel @Inject constructor(
    @ApplicationContext context: Context,
    override val savedStateHandle: SavedStateHandle
) : PagingViewModel<File>(), SupportSafeArgs {

    private val args: GoogleDriveListFragmentArgs by navArgs()

    private val parent = args.parent ?: "root"
    private val credential =
        GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_READONLY))

    val googleSignInAccount = MutableStateFlow(GoogleSignIn.getLastSignedInAccount(context))

    override val transitionName = args.transitionName
    override val pagingDataFlow = googleSignInAccount.filterNotNull().flatMapLatest {
        credential.selectedAccount = it.account
        val driverService =
            Drive.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
                .setApplicationName("ComicViewer")
                .build()
        Pager(PagingConfig(20)) { DrivePagingSource(driverService, parent) }.flow
    }.cachedIn(viewModelScope)
}
