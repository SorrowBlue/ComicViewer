package com.sorrowblue.comicviewer.settings.display

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.DropDownPreference
import androidx.preference.ListPreference
import com.sorrowblue.comicviewer.domain.model.DarkMode
import com.sorrowblue.comicviewer.domain.model.FolderThumbnailOrder
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceBinding
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceFragment
import com.sorrowblue.comicviewer.framework.settings.preference
import com.sorrowblue.comicviewer.framework.settings.preferenceBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsDisplayBinding(fragment: FrameworkPreferenceFragment) :
    FrameworkPreferenceBinding(fragment) {
    val darkMode by preference<DropDownPreference>(R.string.settings_display_preference_key_dark_mode)
    val folderThumbnails by preference<ListPreference>(R.string.settings_display_preference_key_folder_thumbnails)
}

@AndroidEntryPoint
internal class SettingsDisplayFragment :
    FrameworkPreferenceFragment(R.xml.settings_display_preference) {

    private val binding: SettingsDisplayBinding by preferenceBinding()
    private val viewModel: SettingsDisplayViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.darkMode.entryValues = DarkMode.values().map(DarkMode::name).toTypedArray()
        binding.darkMode.setOnPreferenceChangeListener<String> { _, newValue ->
            viewModel.updateDarkMode(DarkMode.valueOf(newValue))
            false
        }
        binding.folderThumbnails.entryValues =
            FolderThumbnailOrder.values().map(FolderThumbnailOrder::name).toTypedArray()
        binding.folderThumbnails.setOnPreferenceChangeListener<String> { _, newValue ->
            viewModel.updateFolderThumbnailOrder(FolderThumbnailOrder.valueOf(newValue))
            false
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.darkMode.filterNotNull()
                .onEach { binding.darkMode.value = it.name }
                .launchIn(this)
            viewModel.folderThumbnailOrder.filterNotNull()
                .onEach { binding.folderThumbnails.value = it.name }
                .launchIn(this)
        }
    }
}

