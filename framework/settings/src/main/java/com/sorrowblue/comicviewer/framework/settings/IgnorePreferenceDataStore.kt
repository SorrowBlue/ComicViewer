package com.sorrowblue.comicviewer.framework.settings

import androidx.preference.PreferenceDataStore

class IgnorePreferenceDataStore : PreferenceDataStore() {
    override fun putString(key: String?, value: String?) = Unit
    override fun putStringSet(key: String?, values: MutableSet<String>?) = Unit
    override fun putInt(key: String?, value: Int) = Unit
    override fun putLong(key: String?, value: Long) = Unit
    override fun putFloat(key: String?, value: Float) = Unit
    override fun putBoolean(key: String?, value: Boolean) = Unit
}
