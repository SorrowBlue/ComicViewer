package com.sorrowblue.comicviewer.settings.security

import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceBinding
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceFragment
import com.sorrowblue.comicviewer.framework.settings.preference

internal class SettingsSecurityBinding(fragment: FrameworkPreferenceFragment) :
    FrameworkPreferenceBinding(fragment) {
    val auth by preference<SwitchPreferenceCompat>(R.string.settings_security_preference_key_auth_on_launch)
    val useBiometric by preference<SwitchPreferenceCompat>(R.string.settings_security_preference_key_use_biometric)
    val password by preference<Preference>(R.string.settings_security_preference_key_change_password)
}
