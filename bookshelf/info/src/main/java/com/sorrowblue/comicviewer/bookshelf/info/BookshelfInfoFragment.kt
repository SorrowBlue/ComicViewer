package com.sorrowblue.comicviewer.bookshelf.info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sorrowblue.comicviewer.bookshelf.info.databinding.BookshelfInfoFragmentBinding
import com.sorrowblue.comicviewer.bookshelf.management.device.BookshelfManagementDeviceFragmentArgs
import com.sorrowblue.comicviewer.bookshelf.management.smb.BookshelfManagementSmbFragmentArgs
import com.sorrowblue.comicviewer.domain.entity.server.InternalStorage
import com.sorrowblue.comicviewer.domain.entity.server.SmbServer
import com.sorrowblue.comicviewer.framework.ui.fragment.dialogViewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class BookshelfInfoFragment : BottomSheetDialogFragment(R.layout.bookshelf_info_fragment) {

    private val binding: BookshelfInfoFragmentBinding by dialogViewBinding()
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
                    BookshelfInfoFragmentDirections.actionBookshelfInfoToBookshelfManagementDevice().actionId,
                    BookshelfManagementDeviceFragmentArgs(server.id.value).toBundle()
                )

                is SmbServer -> findNavController().navigate(
                    BookshelfInfoFragmentDirections.actionBookshelfInfoToBookshelfManagementSmb().actionId,
                    BookshelfManagementSmbFragmentArgs(server.id.value).toBundle()
                )

                null -> Unit
            }
        }
    }
}
