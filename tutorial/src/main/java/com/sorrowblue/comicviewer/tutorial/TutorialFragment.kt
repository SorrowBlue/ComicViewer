package com.sorrowblue.comicviewer.tutorial

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.tutorial.databinding.TutorialFragmentBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class TutorialFragment : FrameworkFragment(R.layout.tutorial_fragment) {

    private val binding: TutorialFragmentBinding by viewBinding()
    private val viewModel: TutorialViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true) {
            requireActivity().finish()
        }

        val adapter = TutorialAdapter(this, viewModel.items)
        binding.viewPager2.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { _, _ -> }.attach()

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == viewModel.items.lastIndex) {
                    binding.next.setIconResource(com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_twotone_done_24)
                } else {
                    binding.next.setIconResource(com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_twotone_arrow_right_24)
                }
            }
        })

        binding.tabLayout.doOnPreDraw {
            binding.tabLayout.setSelectedTabIndicatorColor(Color.RED)
        }
        binding.next.setOnClickListener {
            if (viewModel.currentItem.value + 1 <= viewModel.items.lastIndex) {
                viewModel.currentItem.value++
            } else {
                viewModel.done()
                findNavController().popBackStack()
            }
        }
    }
}

@HiltViewModel
internal class TutorialViewModel @Inject constructor(
    private val loadSettingsUseCase: LoadSettingsUseCase
) : ViewModel() {

    val currentItem = MutableStateFlow(0)

    val items = TutorialScreen.values()

    fun done() {
        viewModelScope.launch {
            loadSettingsUseCase.edit { it.copy(doneTutorial = true) }
        }
    }
}

enum class TutorialScreen {
    WELCOME,
    ARCHIVE,
    DOCUMENT,
    READING_DIRECTION
}

internal class TutorialAdapter(fragment: Fragment, private val items: Array<TutorialScreen>) :
    FragmentStateAdapter(fragment.childFragmentManager, fragment.viewLifecycleOwner.lifecycle) {

    override fun getItemCount() = items.size

    override fun createFragment(position: Int): Fragment {
        return when (items[position]) {
            TutorialScreen.WELCOME -> TutorialWelcomeFragment()
            TutorialScreen.DOCUMENT -> TutorialDocumentFragment()
            TutorialScreen.ARCHIVE -> TutorialArchiveFragment()
            TutorialScreen.READING_DIRECTION -> TutorialDirectionFragment()
        }
    }

}
