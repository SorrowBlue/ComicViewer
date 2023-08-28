package com.sorrowblue.comicviewer.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigator
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.file.info.observeOpenFolder2
import com.sorrowblue.comicviewer.file.list.FileListFragment
import com.sorrowblue.comicviewer.history.databinding.HistoryFragmentBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class HistoryFragment : FileListFragment(R.layout.history_fragment) {

    private val binding: HistoryFragmentBinding by viewBinding()
    override val viewModel: HistoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeOpenFolder2(R.id.history_fragment) { bookshelfId, parent ->
            TODO()
        }

        binding.viewModel = viewModel
    }

    override fun navigateToFile(
        file: File,
        transitionName: String,
        extras: FragmentNavigator.Extras
    ) {
        when (file) {
            is Book -> TODO()

            is Folder -> Unit
        }
    }
}
