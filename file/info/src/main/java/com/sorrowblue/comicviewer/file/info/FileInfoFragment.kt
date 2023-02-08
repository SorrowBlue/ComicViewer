package com.sorrowblue.comicviewer.file.info

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Book
import androidx.compose.material.icons.twotone.Extension
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.FolderOpen
import androidx.compose.material.icons.twotone.History
import androidx.compose.material.icons.twotone.Update
import androidx.compose.material.icons.twotone.WatchLater
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.file.info.databinding.FileInfoFragmentBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.dialogViewBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.encodeBase64
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class FileInfoFragment : BottomSheetDialogFragment(R.layout.file_info_fragment) {

    private val binding: FileInfoFragmentBinding by dialogViewBinding()
    private val viewModel: FileInfoViewModel by viewModels()

    //    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return ComposeView(requireContext()).apply {
//            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//            setContent {
//                SampleTheme(requireContext()) {
//                    BottomSheetDialogScreen(requireContext(), viewModel)
//                }
//            }
//        }
//    }
//
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
            requireParentFragment().findNavController()
                .navigate("http://comicviewer.sorrowblue.com/folder?serverId=${viewModel.fileFlow.value!!.bookshelfId.value}&path=${viewModel.fileFlow.value!!.parent.encodeBase64()}".toUri())
        }
    }
}

@Composable
fun SampleTheme(
    context: Context,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = dynamicLightColorScheme(context),
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BottomSheetDialogScreen(context: Context, viewModel: FileInfoViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Knob(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))

        Row(Modifier.fillMaxWidth()) {
            val file by viewModel.fileFlow.collectAsState()
            when (file) {
                is Book -> AsyncImage(
                    model = file,
                    contentDescription = "本のサムネイル",
                    modifier = Modifier
                        .size(150.dp)
                )

                is Folder -> AsyncImage(
                    model = file,
                    contentDescription = "フォルダのサムネイル",
                    modifier = Modifier
                        .padding(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 12.dp)
                        .size(150.dp)
//                            .paint(painterResource(id = com.sorrowblue.comicviewer.framework.resource.R.drawable.bg_folder))

                )

                null -> {
                    Unit
                }
            }
            Column(Modifier.fillMaxWidth()) {
                Text(text = file?.name.orEmpty(), style = Typography().titleMedium)
                Text(text = file?.path.orEmpty(), style = Typography().labelSmall)
            }
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            mainAxisAlignment = FlowMainAxisAlignment.Center,
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 0.dp
        ) {
            val file by viewModel.fileFlow.collectAsState()
            if (file is Book) {
                AssistChip(
                    onClick = {},
                    label = { Text((file as Book).path.substringAfterLast('.').lowercase()) },
                    leadingIcon = {
                        Icon(
                            Icons.TwoTone.Extension,
                            null,
                            Modifier.size(AssistChipDefaults.IconSize)
                        )
                    },
                )
            }
            if (file != null) {
                AssistChip(
                    onClick = {},
                    label = { Text(Converter.fileSize(file!!.size)) },
                    leadingIcon = {
                        Icon(
                            Icons.TwoTone.Book,
                            null,
                            Modifier.size(AssistChipDefaults.IconSize)
                        )
                    },
                )
                AssistChip(
                    onClick = {},
                    label = { Text(Converter.dateTime(file!!.lastModifier)) },
                    leadingIcon = {
                        Icon(
                            Icons.TwoTone.Update,
                            null,
                            Modifier.size(AssistChipDefaults.IconSize)
                        )
                    },
                )
            }
            if (file is Book) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            Converter.lastReadPage(
                                context,
                                (file as Book).lastPageRead,
                                (file as Book).totalPageCount
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.TwoTone.History,
                            null,
                            Modifier.size(AssistChipDefaults.IconSize)
                        )
                    },
                )
                AssistChip(
                    onClick = {},
                    label = { Text(Converter.dateTime((file as Book).lastReadTime) + "に読んだ") },
                    leadingIcon = {
                        Icon(
                            Icons.TwoTone.History,
                            null,
                            Modifier.size(AssistChipDefaults.IconSize)
                        )
                    },
                )
            }
        }
        FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 0.dp) {
            FilledTonalButtonIcon({}, "Read later", Icons.TwoTone.WatchLater, null)
            FilledTonalButtonIcon({}, "Save to favorites", Icons.TwoTone.Favorite, null)
            FilledTonalButtonIcon({}, "Open folder", Icons.TwoTone.FolderOpen, null)
        }
    }
}

@Composable
fun Knob(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(30.dp)
            .height(3.dp)
            .background(color = Color(0xFFC4C4C4), shape = RoundedCornerShape(size = 12.dp))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilledTonalButtonIcon(
    onClick: () -> Unit,
    text: String,
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FilledTonalButton(onClick, modifier, enabled) {
        Icon(imageVector, contentDescription, Modifier.size(AssistChipDefaults.IconSize))
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(text)
    }
}
