package com.sorrowblue.comicviewer.library.box.signin

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import com.box.sdk.BoxAPIConnection
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.library.box.R
import com.sorrowblue.comicviewer.library.box.databinding.BoxFragmentSigninBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dev.chrisbanes.insetter.applyInsetter
import java.net.URI
import kotlin.random.Random

internal class BoxSignInFragment : FrameworkFragment(R.layout.box_fragment_signin) {

    private val binding: BoxFragmentSigninBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding()
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
    }
}
