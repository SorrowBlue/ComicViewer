package com.sorrowblue.comicviewer.book.info

import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sorrowblue.comicviewer.book.R
import com.sorrowblue.comicviewer.book.databinding.BookDialogInfoBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.logcat

@AndroidEntryPoint
internal class BookInfoDialog : BottomSheetDialogFragment(R.layout.book_dialog_info) {

    private val binding: BookDialogInfoBinding by viewBinding()
    private val viewModel: BookInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.openFolder.setOnClickListener {
            findNavController().navigate("http://comicviewer.sorrowblue.com/bookshelf?serverId=${viewModel.serverFileFlow.value!!.server.id.value}&path=${Base64.encodeToString(viewModel.bookshelfFlow.value!!.path.encodeToByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)}".toUri())
        }
    }
}

object DateTimeConverter {

    @JvmStatic
    fun epochMilliToString(epochMilli: Long) = Instant.ofEpochMilli(epochMilli)
        .atZone(ZoneOffset.systemDefault())
        .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
}

object Converter {

    @JvmStatic
    fun byteToString(fileSize: Long): String {
        var a = fileSize / 1024f
        return if (a < 1024) {
            "%.2f".format(a) + " KB"
        } else {
            a /= 1024f
            if (a < 1024) {
                "%.2f".format(a) + " MB"
            } else {
                a /= 1024f
                "%.2f".format(a) + " GB"
            }
        }
    }

}
