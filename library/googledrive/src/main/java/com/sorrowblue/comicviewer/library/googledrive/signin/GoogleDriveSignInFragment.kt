package com.sorrowblue.comicviewer.library.googledrive.signin

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.android.material.snackbar.Snackbar
import com.google.api.services.drive.DriveScopes
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.library.googledrive.R
import com.sorrowblue.comicviewer.library.googledrive.databinding.GoogledriveFragmentSigninBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class GoogleDriveSignInFragment : FrameworkFragment(R.layout.googledrive_fragment_signin) {

    private val binding: GoogledriveFragmentSigninBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setupWithNavController()
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }

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
    }

    private val googleSignInRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                kotlin.runCatching {
                    GoogleSignIn.getSignedInAccountFromIntent(it.data).result
                    findNavController().navigate(GoogleDriveSignInFragmentDirections.actionGoogledriveSigninToGoogledriveList())
                }.onFailure {
                    it.printStackTrace()
                    if (it is ApiException) {
                        Snackbar.make(
                            binding.root,
                            "認証に失敗しました。(${it.statusCode})",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else if (it is RuntimeExecutionException && it.cause is ApiException) {
                        Snackbar.make(
                            binding.root,
                            "認証に失敗しました。(${(it.cause as ApiException).statusCode})",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        Snackbar.make(binding.root, "エラーが発生しました。", Snackbar.LENGTH_SHORT).show()
                    }
                }
            } else {
                Snackbar.make(binding.root, "キャンセルしました。", Snackbar.LENGTH_SHORT).show()
            }
        }
}
