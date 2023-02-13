package com.sorrowblue.comicviewer.favorite.edit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialFadeThrough
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.favorite.R
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteFragmentEditBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class FavoriteEditFragment : PagingFragment<File>(R.layout.favorite_fragment_edit) {

    private val binding: FavoriteFragmentEditBinding by viewBinding()
    override val viewModel: FavoriteEditViewModel by viewModels()

    override val adapter get() = FavoriteEditAdapter(viewModel::removeFile)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        binding.appBarLayout.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(top = true)
            }
        }

        binding.toolbar.setupWithNavController()
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
            }
        }
        binding.favoriteNameLayout.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
            }
        }

        binding.frameworkUiRecyclerView.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true, bottom = true)
            }
        }

        fab.setOnClickListener {
            viewModel.save {
                findNavController().popBackStack()
            }
        }
    }
}

