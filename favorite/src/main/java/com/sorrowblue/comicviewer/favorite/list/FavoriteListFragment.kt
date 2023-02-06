package com.sorrowblue.comicviewer.favorite.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialFadeThrough
import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.favorite.FavoriteFragmentArgs
import com.sorrowblue.comicviewer.favorite.R
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteFragmentListBinding
import com.sorrowblue.comicviewer.favorite.extension.transitionName
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class FavoriteListFragment : PagingFragment<Favorite>(R.layout.favorite_fragment_list) {

    private val binding: FavoriteFragmentListBinding by viewBinding()

    override val viewModel: FavoriteListViewModel by viewModels()
    override val recyclerView get() = binding.recyclerView
    override val adapter
        get() = FavoriteListAdapter { favorite, extras ->
            findNavController().navigate(
                FavoriteListFragmentDirections.actionFavoriteListToFavorite(favorite),
                extras
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        postponeEnterTransition()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        binding.toolbar.setupWithNavController(findNavController())
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        binding.recyclerView.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true, bottom = true)
            }
        }
        fab.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.favorite_list_fragment) {
                findNavController().navigate(FavoriteListFragmentDirections.actionFavoriteListToFavoriteCreate())
            }
        }
    }

    private fun FavoriteListFragmentDirections.Companion.actionFavoriteListToFavorite(favorite: Favorite) =
        object : NavDirections {
            override val actionId = actionFavoriteListToFavorite().actionId
            override val arguments =
                FavoriteFragmentArgs(favorite.id.value, favorite.transitionName).toBundle()
        }
}
