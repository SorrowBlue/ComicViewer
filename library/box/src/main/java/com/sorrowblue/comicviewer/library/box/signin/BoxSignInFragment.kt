package com.sorrowblue.comicviewer.library.box.signin

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isVisible
import androidx.navigation.ui.AppBarConfiguration
import com.box.sdk.BoxAPIConnection
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.library.databinding.GoogledriveFragmentSigninBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dev.chrisbanes.insetter.applyInsetter
import java.net.URI
import kotlin.random.Random

internal class BoxSignInFragment :
    FrameworkFragment(com.sorrowblue.comicviewer.library.R.layout.googledrive_fragment_signin) {

    private val binding: GoogledriveFragmentSigninBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appBarConfiguration = AppBarConfiguration(setOf())
        binding.toolbar.setupWithNavController()
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }

        binding.signIn.setOnClickListener {
            val state = Random.nextInt(20).toString()
            val url = BoxAPIConnection.getAuthorizationURL(
                "nihdm7dthg9lm7m3b41bpw7jp7b0lb9z",
                URI.create("https://comicviewer.sorrowblue.com/box/oauth2"),
                state,
                null
            )

            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(url.toString()))
        }
        binding.signIn.isVisible = true
    }
}
