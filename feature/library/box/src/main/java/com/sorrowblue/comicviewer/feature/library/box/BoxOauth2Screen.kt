package com.sorrowblue.comicviewer.feature.library.box

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sorrowblue.comicviewer.feature.library.box.data.BoxApiRepository
import com.sorrowblue.comicviewer.feature.library.box.navigation.BoxOauth2Args
import kotlinx.coroutines.launch

@Composable
internal fun BoxOauth2Route(
    onComplete: () -> Unit,
    viewModel: BoxOauth2ViewModel = viewModel(
        factory = BoxOauth2ViewModel.factory(
            BoxApiRepository.getInstance(LocalContext.current)
        )
    ),
) {
    BoxOauth2Screen()
    LaunchedEffect(Unit) {
        viewModel.authenticate(onComplete)
    }
}

@Composable
private fun BoxOauth2Screen() {
    Scaffold {
        Box(modifier = Modifier.padding(it), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

internal class BoxOauth2ViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: BoxApiRepository,
) : ViewModel() {

    private val args = BoxOauth2Args(savedStateHandle)

    fun authenticate(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.authenticate(args.state, args.code, onSuccess)
        }
    }

    companion object {
        fun factory(repository: BoxApiRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val savedStateHandle = extras.createSavedStateHandle()
                return BoxOauth2ViewModel(savedStateHandle, repository) as T
            }
        }
    }
}
