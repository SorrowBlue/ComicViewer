package com.sorrowblue.comicviewer.library.box.oauth

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.library.box.data.BoxApiRepository
import com.sorrowblue.comicviewer.library.databinding.GoogledriveFragmentSigninBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.launch

internal class BoxOauth2Fragment :
    FrameworkFragment(com.sorrowblue.comicviewer.library.R.layout.googledrive_fragment_signin) {

    private val binding: GoogledriveFragmentSigninBinding by viewBinding()
    private val viewModel: BoxOauth2ViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appBarConfiguration = AppBarConfiguration(setOf())
        binding.toolbar.setupWithNavController()
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }

        binding.signIn.isVisible = false
        viewModel.authenticate {
            findNavController().navigate(com.sorrowblue.comicviewer.library.R.id.box_navigation, null, navOptions {
                popUpTo(com.sorrowblue.comicviewer.library.R.id.box_oauth2_fragment) {
                    this.inclusive = true
                }
            })
        }
    }
}

internal class BoxOauth2ViewModel(
    application: Application,
    override val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application), SupportSafeArgs {

    private val repository = BoxApiRepository.getInstance(application)
    private val args: BoxOauth2FragmentArgs by navArgs()

    fun authenticate(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.authenticate(args.state, args.code, onSuccess)
        }
    }
}
