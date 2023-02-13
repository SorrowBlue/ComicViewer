package com.sorrowblue.comicviewer.app

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.material.snackbar.Snackbar
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.sorrowblue.comicviewer.app.databinding.FragmentGoogleDriveBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.logcat
import com.google.api.services.drive.model.File as DriveFile

@AndroidEntryPoint
internal class GoogleDriveFragment : FrameworkFragment(R.layout.fragment_google_drive) {

    private val binding: FragmentGoogleDriveBinding by viewBinding()
    private val viewModel: GoogleDriveViewModel by viewModels()

    private val googleSignInRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            GoogleSignIn.getSignedInAccountFromIntent(it.data).addOnSuccessListener {
                logcat { "google sign in success. ${it.displayName}" }
            }.addOnFailureListener {
                it.printStackTrace()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.signIn.setOnClickListener {
            val googleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(Scope(DriveScopes.DRIVE_READONLY))
                    .build()
            googleSignInRequest.launch(
                GoogleSignIn.getClient(
                    requireActivity(),
                    googleSignInOptions
                ).signInIntent
            )
        }

        binding.folder.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.createFolder {
                    Snackbar.make(binding.root, "フォルダを作成。${it.id}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        binding.list.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.listFiles()
            }
        }

        binding.signout.setOnClickListener {
            val googleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                    .build()
            GoogleSignIn.getClient(requireActivity(), googleSignInOptions).signOut().addOnSuccessListener {
                viewModel.googleSignInAccount.value = null
            }
        }
    }
}

@HiltViewModel
internal class GoogleDriveViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    val googleSignInAccount = MutableStateFlow(GoogleSignIn.getLastSignedInAccount(application))

    suspend fun listFiles() {
        val credential =
            GoogleAccountCredential.usingOAuth2(getApplication(), listOf(DriveScopes.DRIVE_READONLY))
        credential.selectedAccount = googleSignInAccount.value?.account
        val driverService =
            Drive.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
                .setApplicationName("ComicViewer")
                .build()
        withContext(Dispatchers.IO) {
            val files = driverService.files().list()
                .setSpaces("drive")
                .setCorpora("user ")
//                .setFields("files(id, name)")
                .execute().files

            files.forEach {
                val file = driverService.files().get(it.id).execute()
                logcat { "id=${file.id}, name=${file.name}" }

            }
        }
    }

    suspend fun createFolder(done: (DriveFile) -> Unit) {
        val credential =
            GoogleAccountCredential.usingOAuth2(getApplication(), listOf(DriveScopes.DRIVE_READONLY))
        credential.selectedAccount = googleSignInAccount.value?.account
        val driverService =
            Drive.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
                .setApplicationName("ComicViewer")
                .build()
        val fileMetadata = DriveFile()
        fileMetadata.name = "Test"
        fileMetadata.mimeType = "application/vnd.google-apps.folder"
        withContext(Dispatchers.IO) {
            driverService.files().create(fileMetadata)
                .setFields("id")
                .execute().let {
                    done(it)
                }
        }
    }
}
