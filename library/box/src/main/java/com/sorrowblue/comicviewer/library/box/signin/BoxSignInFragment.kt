package com.sorrowblue.comicviewer.library.box.signin

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.box.sdk.BoxAPIConnection
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.library.box.R
import com.sorrowblue.comicviewer.library.box.databinding.BoxFragmentSigninBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import java.net.URI
import kotlin.random.Random


@AndroidEntryPoint
internal class BoxSignInFragment : FrameworkFragment(R.layout.box_fragment_signin) {

    private val binding: BoxFragmentSigninBinding by viewBinding()
    private val viewModel: BoxSignInViewModel by viewModels()
    private val commonViewModel: CommonViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        binding.progress.isVisible = false
        binding.signIn.isVisible = true
    }
}
