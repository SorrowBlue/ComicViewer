package com.sorrowblue.comicviewer.settings.bookshelf

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceBinding
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceFragment
import com.sorrowblue.comicviewer.framework.settings.preference
import com.sorrowblue.comicviewer.framework.settings.preferenceBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SettingsBookshelfBinding(fragment: FrameworkPreferenceFragment) :
    FrameworkPreferenceBinding(fragment) {

    val showPreview: SwitchPreferenceCompat by preference(R.string.settings_bookshelf_preference_key_show_preview)
    val resolveImageFolder: SwitchPreferenceCompat by preference(R.string.settings_bookshelf_preference_key_resolve_image_folder)
    val supportExtension: Preference by preference(R.string.settings_bookshelf_preference_key_support_extension)
}

@AndroidEntryPoint
internal class SettingsBookshelfFragment :
    FrameworkPreferenceFragment(R.xml.settings_bookshelf_preference) {

    private val binding: SettingsBookshelfBinding by preferenceBinding()
    private val viewModel: SettingsBookshelfViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.settings.stateIn(viewLifecycleOwner.lifecycleScope).collectLatest {
                binding.showPreview.isChecked = it.showPreview
                binding.resolveImageFolder.isChecked = it.resolveImageFolder
            }
        }
        binding.supportExtension.setOnPreferenceClickListener {
            findNavController().navigate(SettingsBookshelfFragmentDirections.actionSettingsBookshelfToSettingsBookshelfSupportExtension())
            true
        }
        binding.showPreview.setOnPreferenceChangeListener<Boolean> { _, newValue ->
            viewModel.updateShowPreview(newValue)
            true
        }
        binding.resolveImageFolder.setOnPreferenceChangeListener<Boolean> { _, newValue ->
            viewModel.updateResolveImageFolder(newValue)
            true
        }
    }
}
