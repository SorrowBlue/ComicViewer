package com.sorrowblue.comicviewer.framework.settings

import android.os.Bundle
import android.view.View
import androidx.annotation.XmlRes
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.transition.MaterialFadeThrough
import com.sorrowblue.comicviewer.framework.resource.databinding.FragmentListMaterialBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dev.chrisbanes.insetter.applyInsetter

abstract class FrameworkPreferenceFragment(
    @XmlRes private val preferencesResId: Int
) : PreferenceFragmentCompat() {

    private val binding: FragmentListMaterialBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = IgnorePreferenceDataStore()
        setPreferencesFromResource(preferencesResId, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.preferenceToolbar.setupWithNavController(findNavController())
        binding.preferenceAppBarLayout.applyInsetter {
            type(statusBars = true, captionBar = true, displayCutout = true) { margin(top = true) }
        }
    }

    fun <T> Preference.setOnPreferenceChangeListener(onPreferenceChange: (preference: Preference, newValue: T) -> Boolean) {
        setOnPreferenceChangeListener { preference, newValue ->
            @Suppress("UNCHECKED_CAST")
            onPreferenceChange.invoke(preference, newValue as T)
        }
    }

}

abstract class FrameworkPreferenceBinding(val fragment: FrameworkPreferenceFragment)


context(FrameworkPreferenceFragment)
inline fun <reified V : FrameworkPreferenceBinding> preferenceBinding() =
    object : AndroidLifecycleBindingProperty<FrameworkPreferenceFragment, V>() {

        override fun bind(thisRef: FrameworkPreferenceFragment): V {
            return V::class.java.getConstructor(FrameworkPreferenceFragment::class.java)
                .newInstance(thisRef)
        }

        override fun getLifecycleOwner(thisRef: FrameworkPreferenceFragment): LifecycleOwner {
            return if (thisRef.view != null) thisRef.viewLifecycleOwner else thisRef
        }
    }

context(FrameworkPreferenceBinding)
inline fun <reified V : Preference> preference(id: Int) =
    object : AndroidLifecycleBindingProperty<FrameworkPreferenceBinding, V>() {

        override fun bind(thisRef: FrameworkPreferenceBinding): V {
            return thisRef.fragment.findPreference(thisRef.fragment.getString(id))!!
        }

        override fun getLifecycleOwner(thisRef: FrameworkPreferenceBinding): LifecycleOwner {
            return if (thisRef.fragment.view != null) thisRef.fragment.viewLifecycleOwner else thisRef.fragment
        }
    }

