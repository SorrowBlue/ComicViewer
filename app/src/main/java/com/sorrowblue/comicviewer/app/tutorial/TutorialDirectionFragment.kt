package com.sorrowblue.comicviewer.app.tutorial

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.app.R
import com.sorrowblue.comicviewer.app.databinding.TutorialFragmentDirectionBinding
import com.sorrowblue.comicviewer.domain.entity.settings.BindingDirection
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerOperationSettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class TutorialDirectionFragment : FrameworkFragment(R.layout.tutorial_fragment_direction) {

    private val binding: TutorialFragmentDirectionBinding by viewBinding()
    private val viewModel: TutorialDirectionViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.bindingDirectionFlow.onEach {
            when (it) {
                BindingDirection.LTR -> binding.readingDirectionGroup.check(R.id.reading_direction_ltr)
                BindingDirection.RTL -> binding.readingDirectionGroup.check(R.id.reading_direction_rtl)
            }
        }.launchInWithLifecycle()
        binding.readingDirectionGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.reading_direction_ltr) {
                viewModel.updateReadingDirection(BindingDirection.LTR)
            } else {
                viewModel.updateReadingDirection(BindingDirection.RTL)
            }
        }
    }
}

@HiltViewModel
internal class TutorialDirectionViewModel @Inject constructor(
    private val viewerOperationSettingsUseCase: ManageViewerOperationSettingsUseCase
) : ViewModel() {

    val bindingDirectionFlow = viewerOperationSettingsUseCase.settings.map { it.bindingDirection }
    fun updateReadingDirection(rtl: BindingDirection) {
        viewModelScope.launch {
            viewerOperationSettingsUseCase.edit { it.copy(bindingDirection = rtl) }
        }
    }
}
