package com.sorrowblue.comicviewer.settings

import androidx.preference.Preference
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceBinding
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceFragment
import com.sorrowblue.comicviewer.framework.settings.preference

internal class SettingsBinding(fragment: FrameworkPreferenceFragment) :
    FrameworkPreferenceBinding(fragment) {
    val display by preference<Preference>(R.string.settings_preference_key_display)
    val folder by preference<Preference>(R.string.settings_preference_key_folder)
    val viewer by preference<Preference>(R.string.settings_preference_key_viewer)
    val security by preference<Preference>(R.string.settings_preference_key_security)
    val app by preference<Preference>(R.string.settings_preference_key_app)
    val tutorial by preference<Preference>(R.string.settings_preference_key_tutorial)
}
