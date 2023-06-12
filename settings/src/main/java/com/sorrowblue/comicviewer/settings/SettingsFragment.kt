package com.sorrowblue.comicviewer.settings

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceFragment
import com.sorrowblue.comicviewer.framework.settings.preferenceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class SettingsFragment : FrameworkPreferenceFragment(R.xml.settings_preference) {

    private val binding: SettingsBinding by preferenceBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.display.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToDisplaySettings())
            true
        }
        binding.folder.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToSettingsFolder())
            true
        }
        binding.viewer.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToSettingsViewer())
            true
        }
        binding.security.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToSettingsSecurity())
            true
        }
        binding.app.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToSettingsApp())
            true
        }
        binding.tutorial.setOnPreferenceClickListener {
            findNavController().navigate(com.sorrowblue.comicviewer.framework.ui.R.id.action_global_tutorial_navigation)
            true
        }
    }
}
