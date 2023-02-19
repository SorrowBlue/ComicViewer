package com.sorrowblue.comicviewer.settings

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mikepenz.aboutlibraries.LibsBuilder
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceFragment
import com.sorrowblue.comicviewer.framework.settings.preferenceBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class SettingsFragment : FrameworkPreferenceFragment(R.xml.settings_preference) {

    private val viewModel: SettingsViewModel by hiltNavGraphViewModels(R.id.settings_navigation)
    private val binding: SettingsBinding by preferenceBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewerSettings.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToSettingsViewer())
            true
        }
        binding.displaySettings.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToDisplaySettings())
            true
        }

        binding.folder.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToSettingsFolder())
            true
        }
        binding.license.setOnPreferenceClickListener {
            LibsBuilder().start(requireActivity())
            true
        }

        binding.security.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToSettingsSecurity())
            true
        }

        binding.restoreOnLaunch.setOnPreferenceChangeListener<Boolean> { _, newValue ->
            viewModel.updateRestoreOnLaunch(newValue)
            false
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.settings.collectLatest {
                binding.restoreOnLaunch.isChecked = it.restoreOnLaunch
            }
        }
    }
}
