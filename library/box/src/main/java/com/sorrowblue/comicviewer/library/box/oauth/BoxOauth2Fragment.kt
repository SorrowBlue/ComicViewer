package com.sorrowblue.comicviewer.library.box.oauth

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.library.box.R
import com.sorrowblue.comicviewer.library.box.data.BoxApiRepository
import com.sorrowblue.comicviewer.library.box.databinding.BoxFragmentSigninBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.chrisbanes.insetter.applyInsetter
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class BoxOauth2Fragment : FrameworkFragment(R.layout.box_fragment_signin) {

    private val binding: BoxFragmentSigninBinding by viewBinding()
    private val viewModel: BoxOauth2ViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setupWithNavController()
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }

        binding.progress.isVisible = true
        binding.signIn.isVisible = false
        viewModel.authenticate {
            navigate(BoxOauth2FragmentDirections.actionBoxOauth2ToBoxList())
        }
    }
}

@HiltViewModel
internal class BoxOauth2ViewModel @Inject constructor(
    private val repository: BoxApiRepository,
    override val savedStateHandle: SavedStateHandle
) : ViewModel(), SupportSafeArgs {

    private val args: BoxOauth2FragmentArgs by navArgs()

    fun authenticate(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.authenticate(args.state, args.code, onSuccess)
        }
    }
}
