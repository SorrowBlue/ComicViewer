package com.sorrowblue.comicviewer.settings.folder.supportextension

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder
import androidx.preference.children
import com.google.android.material.snackbar.Snackbar
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceBinding
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceFragment
import com.sorrowblue.comicviewer.framework.settings.preference
import com.sorrowblue.comicviewer.framework.settings.preferenceBinding
import com.sorrowblue.comicviewer.settings.folder.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class SettingsFolderSupportExtensionBinding(fragment: FrameworkPreferenceFragment) :
    FrameworkPreferenceBinding(fragment) {

    val archive: PreferenceCategory by preference(R.string.settings_folder_prefkey_archive)
    val document: PreferenceCategoryButton by preference(R.string.settings_folder_prefkey_document)
}

fun getAttr(context: Context, attr: Int, fallbackAttr: Int): Int {
    val value = TypedValue()
    context.theme.resolveAttribute(attr, value, true)
    return if (value.resourceId != 0) {
        attr
    } else fallbackAttr
}

class PreferenceCategoryButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = getAttr(
        context,
        androidx.preference.R.attr.preferenceCategoryStyle,
        android.R.attr.preferenceCategoryStyle
    ),
    defStyleRes: Int = 0
) : PreferenceCategory(context, attrs, defStyleAttr, defStyleRes) {

    var onClick: () -> Unit = {}

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.findViewById(R.id.preference_category_button)?.setOnClickListener {
            onClick.invoke()
        }
    }
}

@AndroidEntryPoint
internal class SettingsFolderSupportExtensionFragment :
    FrameworkPreferenceFragment(R.xml.settings_folder_preference_support_extension) {

    private val binding: SettingsFolderSupportExtensionBinding by preferenceBinding()
    private val viewModel: SettingsFolderSupportExtensionViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        SupportExtension.Archive.values().forEach {
            val pref = CheckBoxPreference(requireContext())
            pref.key = it.name
            pref.title = it.extension
            pref.isChecked = true
            pref.setOnPreferenceChangeListener<Boolean> { preference, t ->
                if (t) {
                    viewModel.addExtension(SupportExtension.valueOf(preference.key))
                } else {
                    viewModel.removeExtension(SupportExtension.valueOf(preference.key))
                }
                true
            }
            binding.archive.addPreference(pref)
        }
        if (!viewModel.isSupportDocument.value) {
            binding.document.summary = "有効化するには追加のダウンロードが必要です。"
            binding.document.onClick = {
                Snackbar.make(requireView(), "ダウンロードします。", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            binding.document.summary = null
            binding.document.onClick = {}
        }
        SupportExtension.Document.values().forEach {
            val pref = CheckBoxPreference(requireContext())
            pref.key = it.name
            pref.isEnabled = viewModel.isSupportDocument.value
            pref.title = it.extension
            pref.isChecked = true
            pref.setOnPreferenceChangeListener<Boolean> { preference, t ->
                if (t) {
                    viewModel.addExtension(SupportExtension.valueOf(preference.key))
                } else {
                    viewModel.removeExtension(SupportExtension.valueOf(preference.key))
                }
                true
            }
            binding.document.addPreference(pref)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.supportExtension.collectLatest { extension ->
                preferenceScreen.children.filterIsInstance<PreferenceCategory>()
                    .flatMap { it.children.filterIsInstance<CheckBoxPreference>() }.forEach {
                        it.isChecked = SupportExtension.valueOf(it.key) in extension
                    }
            }
        }
    }
}
