package com.sorrowblue.comicviewer.bookshelf.display

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.bookshelf.databinding.BookshelfFragmentDisplayBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class BookshelfDisplayFragment :
    BottomSheetDialogFragment(R.layout.bookshelf_fragment_display) {

    private val binding: BookshelfFragmentDisplayBinding by viewBinding()
    private val viewModel: BookshelfDisplayViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
    }
}
