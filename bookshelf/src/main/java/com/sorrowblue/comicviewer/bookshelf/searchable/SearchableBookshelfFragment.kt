package com.sorrowblue.comicviewer.bookshelf.searchable

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.sorrowblue.comicviewer.bookshelf.AbstractBookshelfFragment
import com.sorrowblue.comicviewer.bookshelf.BookshelfAdapter
import com.sorrowblue.comicviewer.bookshelf.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class SearchableBookshelfFragment : AbstractBookshelfFragment() {

    override val viewModel: SearchableBookshelfViewModel by viewModels()
    override val menuResId = R.menu.bookshelf

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            scrimColor = Color.TRANSPARENT
            setPathMotion(MaterialArcMotion())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.menu.requireItem(R.id.bookchelf_search).expandActionView()
        binding.toolbar.menu.requireItem(R.id.bookchelf_search).requireActionView<SearchView>()
            .setQuery(viewModel.query, false)
        val searchView =
            binding.toolbar.menu.requireItem(R.id.bookchelf_search).requireActionView<SearchView>()
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (searchView.query.isEmpty()) {
                    searchView.isIconified = true
                } else {
                    WindowInsetsControllerCompat(requireActivity().window, binding.root).hide(
                        WindowInsetsCompat.Type.ime()
                    )
                }
            }
        }
        searchView.setOnQueryTextChangeListener {
            if (it != null) {
                viewModel.query = it
                (binding.recyclerView.adapter as? BookshelfAdapter)?.refresh()
                true
            } else {
                false
            }
        }
    }

    private fun Menu.requireItem(id: Int) = findItem(id)

    private fun SearchView.setOnQueryTextChangeListener(onQueryTextChange: (newText: String?) -> Boolean) {
        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?) = onQueryTextChange(newText)
        })
    }

    private inline fun <reified T> MenuItem.requireActionView(): T {
        val view = actionView
        checkNotNull(view) {
            ("MenuItem " + this + " did not return a View from"
                    + " onCreateView() or this was called before onCreateView().")
        }
        check(actionView is T)
        return actionView as T
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return false
    }
}
