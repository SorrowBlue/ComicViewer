package com.sorrowblue.comicviewer.file.info

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.sorrowblue.comicviewer.file.R
import com.sorrowblue.comicviewer.file.databinding.FileFragmentInfoBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class FileInfoFragment : BottomSheetDialogFragment(R.layout.file_fragment_info) {

    private val binding: FileFragmentInfoBinding by viewBinding()
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
            findNavController().navigate("comicviewer://comicviewer.sorrowblue.com/favorite/add?serverId=${viewModel.fileFlow.value!!.bookshelfId.value}&filePath=${viewModel.fileFlow.value!!.base64Path()}".toUri())
        }
        binding.openFolder.setOnClickListener {
            val file = viewModel.fileFlow.value!!
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                "bookshelfId",
                file.bookshelfId.value
            )
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                "parent",
                file.base64Parent()
            )
            dismiss()
//            requireParentFragment().findNavController().navigate("comicviewer://comicviewer.sorrowblue.com/folder?serverId=${viewModel.fileFlow.value!!.bookshelfId.value}&path=${viewModel.fileFlow.value!!.base64Parent()}".toUri())
        }
    }
}

fun Fragment.observeOpenFolder2(
    fragmentId: Int,
    openFolder: (bookshelfId: Int, parent: String) -> Unit
) {
    val navBackStackEntry = findNavController().getBackStackEntry(fragmentId)
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME && navBackStackEntry.savedStateHandle.contains("bookshelfId") && navBackStackEntry.savedStateHandle.contains(
                "parent"
            )
        ) {
            val bookshelfId = navBackStackEntry.savedStateHandle.get<Int>("bookshelfId")!!
            navBackStackEntry.savedStateHandle.remove<Int>("bookshelfId")
            val parent = navBackStackEntry.savedStateHandle.get<String>("parent")!!
            navBackStackEntry.savedStateHandle.remove<String>("parent")
            openFolder(bookshelfId, parent)
        }
    }
    navBackStackEntry.lifecycle.addObserver(observer)
    viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            navBackStackEntry.lifecycle.removeObserver(observer)
        }
    })
}
