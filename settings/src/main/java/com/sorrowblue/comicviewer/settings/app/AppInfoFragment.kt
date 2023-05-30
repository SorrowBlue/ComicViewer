package com.sorrowblue.comicviewer.settings.app

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceBinding
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceFragment
import com.sorrowblue.comicviewer.framework.settings.preference
import com.sorrowblue.comicviewer.framework.settings.preferenceBinding
import com.sorrowblue.comicviewer.settings.BuildConfig
import com.sorrowblue.comicviewer.settings.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import logcat.asLog
import logcat.logcat


class AppInfoFragment : FrameworkPreferenceFragment(R.xml.settings_app_preference) {

    private val binding: SettingsAppBinding by preferenceBinding()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val pInfo = requireContext().packageManager.getPackageInfo(
                requireContext().packageName, 0
            )
            binding.version.summary = pInfo.versionName
            binding.build.summary =
                Instant.ofEpochMilli(BuildConfig.TIMESTAMP).atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        binding.rate.setOnPreferenceClickListener {
            val manager = ReviewManagerFactory.create(requireContext())
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                    flow.addOnCompleteListener { a ->
                        if (a.isSuccessful) {
                            logcat { "成功" }
                        } else {
                            logcat { a.exception?.asLog().toString() }
                        }
                    }
                } else {
                    // There was some problem, log or handle the error code.
                    @ReviewErrorCode
                    val reviewErrorCode = (task.exception as? ReviewException)?.errorCode
                    logcat { task.exception?.asLog().toString() }
                }
            }
            false
        }
    }
}

internal class SettingsAppBinding(fragment: FrameworkPreferenceFragment) :
    FrameworkPreferenceBinding(fragment) {
    val build by preference<Preference>(R.string.settings_app_preference_key_build)
    val version by preference<Preference>(R.string.settings_app_preference_key_version)
    val rate by preference<Preference>(R.string.settings_app_preference_key_rate)
}
