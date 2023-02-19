package com.sorrowblue.comicviewer.bookshelf.info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.bookshelf.databinding.BookshelfFragmentInfoBinding
import com.sorrowblue.comicviewer.domain.entity.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.entity.bookshelf.SmbServer
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class BookshelfInfoFragment : BottomSheetDialogFragment(R.layout.bookshelf_fragment_info) {

    private val binding: BookshelfFragmentInfoBinding by viewBinding()
    private val viewModel: BookshelfInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.remove.setOnClickListener {
            findNavController().navigate(
                BookshelfInfoFragmentDirections.actionBookshelfInfoToBookshelfRemoveConfirm(
                    viewModel.bookshelf.value!!.id.value
                )
            )
        }
        binding.edit.setOnClickListener {
            when (val server = viewModel.bookshelf.value) {
                is InternalStorage -> findNavController().navigate(
                    BookshelfInfoFragmentDirections.actionBookshelfInfoToBookshelfManageDevice(
                        server.id.value
                    )
                )

                is SmbServer -> findNavController().navigate(
                    BookshelfInfoFragmentDirections.actionBookshelfInfoToBookshelfManageSmb(
                        server.id.value
                    )
                )

                null -> Unit
            }
        }
    }
}
