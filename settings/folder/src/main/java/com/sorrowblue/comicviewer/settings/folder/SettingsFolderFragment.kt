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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class SettingsFolderBinding(fragment: FrameworkPreferenceFragment) :
    FrameworkPreferenceBinding(fragment) {

    val showPreview: SwitchPreferenceCompat by preference(R.string.settings_folder_preference_key_show_preview)
    val supportExtension: Preference by preference(R.string.settings_folder_preference_key_support_extension)
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

        viewModel.settings.map { it.showPreview }.distinctUntilChanged()
            .onEach(binding.showPreview::setChecked)
            .launchInWithLifecycle()
        binding.showPreview.setOnPreferenceChangeListener<Boolean> { _, newValue ->
            viewModel.updateShowPreview(newValue)
            false
        }
    }
}
