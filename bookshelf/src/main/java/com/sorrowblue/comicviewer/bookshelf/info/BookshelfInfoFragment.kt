package com.sorrowblue.comicviewer.bookshelf.info

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.bookshelf.databinding.BookshelfFragmentInfoBinding
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.entity.bookshelf.SmbServer
import com.sorrowblue.comicviewer.framework.ui.navigation.FragmentResult
import com.sorrowblue.comicviewer.framework.ui.navigation.setDialogFragmentResult
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import logcat.logcat

class BookshelfInfoRemoveResult(bundle: Bundle) : FragmentResult(bundle) {

    constructor(bookshelfId: BookshelfId) : this(bundleOf(BOOKSHELF_ID to bookshelfId.value))

    val bookshelfId: BookshelfId = BookshelfId(bundle.getInt(BOOKSHELF_ID))

    companion object {
        private const val BOOKSHELF_ID = "bookshelfId"
    }
}

class BookshelfInfoEditResult(bundle: Bundle) : FragmentResult(bundle) {

    constructor(type: String, bookshelfId: BookshelfId) : this(
        bundleOf(
            TYPE to type,
            BOOKSHELF_ID to bookshelfId.value
        )
    )

    val bookshelfId: BookshelfId get() = BookshelfId(bundle.getInt(BOOKSHELF_ID))
    val type get() = bundle.getString(TYPE)

    companion object {
        private const val TYPE = "type"
        private const val BOOKSHELF_ID = "bookshelfId"
    }
}


@AndroidEntryPoint
internal class BookshelfInfoFragment : BottomSheetDialogFragment(R.layout.bookshelf_fragment_info) {

    private val binding: BookshelfFragmentInfoBinding by viewBinding()
    private val viewModel: BookshelfInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.remove.setOnClickListener {
            setDialogFragmentResult(
                "remove",
                BookshelfInfoRemoveResult(viewModel.bookshelf.value!!.id)
            )
            dismiss()
        }
        binding.edit.setOnClickListener {
            when (viewModel.bookshelf.value) {
                is InternalStorage ->
                    setDialogFragmentResult(
                        "edit",
                        BookshelfInfoEditResult("InternalStorage", viewModel.bookshelf.value!!.id)
                    )

                is SmbServer ->
                    setDialogFragmentResult(
                        "edit",
                        BookshelfInfoEditResult("SmbServer", viewModel.bookshelf.value!!.id)
                    )

                null -> Unit
            }
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logcat { "onDestroy" }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        logcat { "onDismiss" }
    }
}
