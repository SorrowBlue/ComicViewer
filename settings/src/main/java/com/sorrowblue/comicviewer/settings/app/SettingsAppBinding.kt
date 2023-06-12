package com.sorrowblue.comicviewer.settings.app

import androidx.preference.Preference
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceBinding
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceFragment
import com.sorrowblue.comicviewer.framework.settings.preference
import com.sorrowblue.comicviewer.settings.R

internal class SettingsAppBinding(fragment: FrameworkPreferenceFragment) :
    FrameworkPreferenceBinding(fragment) {
    val version by preference<Preference>(R.string.settings_app_preference_key_version)
    val build by preference<Preference>(R.string.settings_app_preference_key_build)
    val license by preference<Preference>(R.string.settings_app_preference_key_license)
    val rate by preference<Preference>(R.string.settings_app_preference_key_rate)
}
