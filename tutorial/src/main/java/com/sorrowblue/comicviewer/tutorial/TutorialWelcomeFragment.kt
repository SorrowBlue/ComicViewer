package com.sorrowblue.comicviewer.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.tutorial.databinding.TutorialFragmentArchiveBinding
import com.sorrowblue.comicviewer.tutorial.databinding.TutorialViewExtensionBinding
import com.sorrowblue.jetpack.binding.viewBinding

class TutorialWelcomeFragment : FrameworkFragment(R.layout.tutorial_fragment_welcome)

class TutorialArchiveFragment : FrameworkFragment(R.layout.tutorial_fragment_archive) {

    private val binding: TutorialFragmentArchiveBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SupportExtension.Archive.values().forEach {
            TutorialViewExtensionBinding.inflate(LayoutInflater.from(requireContext())).apply {
                extension.text = "." + it.extension
                binding.extensionGroup.addView(root)
            }
        }
    }
}

