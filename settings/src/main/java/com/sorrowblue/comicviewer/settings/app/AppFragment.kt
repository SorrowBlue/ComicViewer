package com.sorrowblue.comicviewer.settings.app

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.google.android.play.core.review.ReviewManagerFactory
import com.mikepenz.aboutlibraries.LibsBuilder
import com.sorrowblue.comicviewer.framework.settings.FrameworkPreferenceFragment
import com.sorrowblue.comicviewer.framework.settings.preferenceBinding
import com.sorrowblue.comicviewer.settings.BuildConfig
import com.sorrowblue.comicviewer.settings.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import logcat.asLog
import logcat.logcat

val Context.packageInfo: PackageInfo
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName, 0)
    }

class AppFragment : FrameworkPreferenceFragment(R.xml.settings_app_preference) {

    private val binding: SettingsAppBinding by preferenceBinding()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.version.summary = requireContext().packageInfo.versionName
        binding.build.summary =
            Instant.ofEpochMilli(BuildConfig.TIMESTAMP).atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        binding.rate.setOnPreferenceClickListener {
            val manager = ReviewManagerFactory.create(requireContext())
            manager.requestReviewFlow().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    manager.launchReviewFlow(requireActivity(), task.result)
                        .addOnCompleteListener { a ->
                            if (a.isSuccessful) {
                                logcat { "成功" }
                            } else {
                                logcat { a.exception?.asLog().toString() }
                                CustomTabsIntent.Builder().build().launchUrl(
                                    requireContext(),
                                    "http://play.google.com/store/apps/details?id=${requireContext().packageName}".toUri()
                                )
                            }
                        }
                } else {
                    logcat { task.exception?.asLog().toString() }
                    CustomTabsIntent.Builder().build().launchUrl(
                        requireContext(),
                        "http://play.google.com/store/apps/details?id=${requireContext().packageName}".toUri()
                    )
                }
            }
            false
        }
        binding.license.setOnPreferenceClickListener {
            LibsBuilder().withActivityTitle(getString(R.string.settings_app_label_license))
                .withSearchEnabled(true).withEdgeToEdge(true).start(requireContext())
            true
        }
    }
}
