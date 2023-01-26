package com.sorrowblue.comicviewer.book.info

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Base64
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Target
import androidx.palette.graphics.get
import coil.load
import coil.size.Size
import coil.transform.Transformation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.MaterialColors
import com.sorrowblue.comicviewer.book.R
import com.sorrowblue.comicviewer.book.databinding.BookDialogInfoBinding
import com.sorrowblue.comicviewer.domain.request.FileThumbnailRequest
import com.sorrowblue.comicviewer.framework.ui.fragment.launchIn
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
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
//        binding.favorite.setOnClickListener {
//            val file = viewModel.serverFileFlow.value?.file ?: return@setOnClickListener
//            findNavController().navigate("https://comicviewer.sorrowblue.com/favorite/add?serverId=${file.serverId.value}&filePath=${Uri.encode(file.path)}".toUri())
//        }
        viewModel.fileFlow.filterNotNull().onEach {
            val imageView: ImageView = binding.bookImageview
            imageView.load(FileThumbnailRequest(it.serverId to it)) {
//                transformations(BlurTransformation(requireContext()))
                listener { request, result ->
                    Palette.from(result.drawable.toBitmap()).generate {
                        if (it == null) {
                            logcat { "palette is null" }
                        } else {
                            val swatch = it[Target.VIBRANT]?.rgb ?: it[Target.MUTED]?.rgb
                            if (swatch != null) {
                                val colorRoles = MaterialColors.getColorRoles(requireContext(), swatch)
                                binding.root.setBackgroundColor(colorRoles.accentContainer)
                                binding.bookName.setTextColor(colorRoles.onAccentContainer)
                                binding.favorite.setTextColor(colorRoles.onAccentContainer)
                                binding.favorite.iconTint = ColorStateList.valueOf(colorRoles.onAccentContainer)
                                binding.openFolder.setTextColor(colorRoles.onAccentContainer)
                                binding.openFolder.iconTint = ColorStateList.valueOf(colorRoles.onAccentContainer)
                            }
                        }
                    }
                }
            }
        }.launchIn()
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
