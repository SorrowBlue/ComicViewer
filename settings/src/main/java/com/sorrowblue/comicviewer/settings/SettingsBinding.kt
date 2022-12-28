package com.sorrowblue.comicviewer.settings

import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceBinding
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceFragment
import com.sorrowblue.comicviewer.framework.settings.preference

internal class SettingsBinding(fragment: FrameworkPreferenceFragment) :
    FrameworkPreferenceBinding(fragment) {
    val viewerSettings by preference<Preference>(R.string.settings_preference_key_viewer)
    val displaySettings by preference<Preference>(R.string.settings_preference_key_display)
    val bookshelf by preference<Preference>(R.string.settings_preference_key_bookshelf)
    val useBiometric by preference<SwitchPreferenceCompat>(R.string.settings_preference_key_use_biometric)
    val restoreOnLaunch by preference<SwitchPreferenceCompat>(R.string.settings_preference_key_restore_on_launch)
}
