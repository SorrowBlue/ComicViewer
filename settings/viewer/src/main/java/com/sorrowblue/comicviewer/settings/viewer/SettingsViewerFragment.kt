package com.sorrowblue.comicviewer.settings.viewer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.DropDownPreference
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.sorrowblue.comicviewer.domain.entity.settings.ViewerSettings
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceBinding
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceFragment
import com.sorrowblue.comicviewer.framework.settings.preference
import com.sorrowblue.comicviewer.framework.settings.preferenceBinding
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewerBinding(fragment: FrameworkPreferenceFragment) :
    FrameworkPreferenceBinding(fragment) {
    val statusBar by preference<SwitchPreferenceCompat>(R.string.settings_viewer_preference_key_show_status_bar)
    val navigationBar by preference<SwitchPreferenceCompat>(R.string.settings_viewer_preference_key_show_navigation_bar)
    val notTurnOffScreen by preference<SwitchPreferenceCompat>(R.string.settings_viewer_preference_key_not_turn_off_screen)
    val brightnessControl by preference<SwitchPreferenceCompat>(R.string.settings_viewer_preference_key_brightness_control)
    val brightnessLevel by preference<SeekBarPreference>(R.string.settings_viewer_preference_key_brightness_level)
    val imageQuality by preference<SeekBarPreference>(R.string.settings_viewer_preference_key_image_quality)
    val readingDirection by preference<DropDownPreference>(R.string.settings_viewer_preference_key_binding_direction)
}

@AndroidEntryPoint
internal class SettingsViewerFragment :
    FrameworkPreferenceFragment(R.xml.settings_viewer_preference) {

    private val binding: SettingsViewerBinding by preferenceBinding()
    private val viewModel: SettingsViewerViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.statusBar.setOnPreferenceChangeListener<Boolean> { _, newValue ->
            viewModel.updateStatusBar(newValue)
            false
        }
        binding.navigationBar.setOnPreferenceChangeListener<Boolean> { _, newValue ->
            viewModel.updateNavigationBar(newValue)
            false
        }
        binding.notTurnOffScreen.setOnPreferenceChangeListener<Boolean> { _, newValue ->
            viewModel.updateKeepOnScreen(newValue)
            false
        }
        binding.brightnessControl.setOnPreferenceChangeListener<Boolean> { _, newValue ->
            viewModel.updateBrightnessControl(newValue)
            false
        }
        binding.brightnessLevel.setOnPreferenceChangeListener<Int> { _, newValue ->
            viewModel.updateBrightnessLevel(newValue / 100f)
            false
        }
        binding.imageQuality.setOnPreferenceChangeListener<Int> { _, newValue ->
            viewModel.updateImageQuality(newValue)
            false
        }

        binding.readingDirection.setEntries(R.array.settings_viewer_entries_binding_direction)
        binding.readingDirection.entryValues = arrayOf(ViewerSettings.BindingDirection.RIGHT.name, ViewerSettings.BindingDirection.LEFT.name)
        binding.readingDirection.setOnPreferenceChangeListener<String> { _, newValue ->
            viewModel.updateBindingDirection(ViewerSettings.BindingDirection.valueOf(newValue))
            false
        }
        viewModel.settings.map { it.bindingDirection }.distinctUntilChanged().onEach {
            binding.readingDirection.value = it.name
        }.launchInWithLifecycle()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.settings.collectLatest {
                binding.statusBar.isChecked = it.showStatusBar
                binding.navigationBar.isChecked = it.showNavigationBar
                binding.notTurnOffScreen.isChecked = it.keepOnScreen
                binding.brightnessControl.isChecked = it.enableBrightnessControl
                binding.brightnessLevel.isEnabled = it.enableBrightnessControl
                binding.brightnessLevel.value = (it.screenBrightness * 100).toInt()
                binding.imageQuality.value = it.imageQuality
            }
        }
    }
}
