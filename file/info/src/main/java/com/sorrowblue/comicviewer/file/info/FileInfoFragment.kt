package com.sorrowblue.comicviewer.file.info

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.sorrowblue.comicviewer.file.info.databinding.FileInfoFragmentBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.dialogViewBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.encodeBase64
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class FileInfoFragment : BottomSheetDialogFragment(R.layout.file_info_fragment) {

    private val binding: FileInfoFragmentBinding by dialogViewBinding()
    private val viewModel: FileInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.readLater.setOnClickListener {
            viewModel.addReadLater {
                Snackbar.make(binding.root, "「後で見る」に追加しました。", Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.addFavorite.setOnClickListener {
            findNavController().navigate("http://comicviewer.sorrowblue.com/favorite/add?serverId=${viewModel.fileFlow.value!!.bookshelfId.value}&filePath=${viewModel.fileFlow.value!!.path.encodeBase64()}".toUri())
        }
        binding.openFolder.setOnClickListener {
            requireParentFragment().findNavController().navigate("http://comicviewer.sorrowblue.com/folder?serverId=${viewModel.fileFlow.value!!.bookshelfId.value}&path=${viewModel.fileFlow.value!!.parent.encodeBase64()}".toUri())
        }
    }
}
