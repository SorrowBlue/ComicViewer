package com.sorrowblue.comicviewer.settings.folder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceBinding
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceFragment
import com.sorrowblue.comicviewer.framework.settings.preference
import com.sorrowblue.comicviewer.framework.settings.preferenceBinding
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

internal class SettingsFolderBinding(fragment: FrameworkPreferenceFragment) :
    FrameworkPreferenceBinding(fragment) {

    val resolveImageFolder: SwitchPreferenceCompat by preference(R.string.settings_folder_prefkey_resolve_image_folder)
    val showPreview: SwitchPreferenceCompat by preference(R.string.settings_folder_preference_key_show_preview)
    val supportExtension: Preference by preference(R.string.settings_folder_preference_key_support_extension)
    val deleteThumbnail: Preference by preference(R.string.settings_folder_preference_key_delete_thumbnail)
}

@AndroidEntryPoint
internal class SettingsFolderFragment :
    FrameworkPreferenceFragment(R.xml.settings_folder_preference) {

    private val binding: SettingsFolderBinding by preferenceBinding()
    private val viewModel: SettingsFolderViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.supportExtension.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFolderFragmentDirections.actionSettingsFolderToSettingsFolderSupportExtension())
            true
        }

        viewModel.showPreview.onEach(binding.showPreview::setChecked).launchInWithLifecycle()
        binding.showPreview.setOnPreferenceChangeListener<Boolean> { _, newValue ->
            viewModel.updateShowPreview(newValue)
            false
        }

        viewModel.resolveImageFolder.onEach(binding.resolveImageFolder::setChecked)
            .launchInWithLifecycle()
        binding.resolveImageFolder.setOnPreferenceChangeListener<Boolean> { _, newValue ->
            viewModel.updateResolveImageFolder(newValue)
            false
        }
        binding.deleteThumbnail.setOnPreferenceClickListener {
            viewModel.deleteThumbnail()
            true
        }
    }
}
