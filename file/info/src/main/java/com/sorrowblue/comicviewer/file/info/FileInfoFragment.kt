package com.sorrowblue.comicviewer.file.info

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.framework.ui.fragment.encodeBase64
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class FileInfoFragment : BottomSheetDialogFragment() {

    private val viewModel: FileInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SampleTheme {
                    BottomSheetDialogScreen(
                        requireContext(),
                        viewModel,
                        { requireParentFragment().findNavController() }
                    ) {
                        Snackbar.make(dialog?.window?.decorView!!, it, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun SampleTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColor && useDarkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !useDarkTheme -> dynamicLightColorScheme(LocalContext.current)
        useDarkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BottomSheetDialogScreen(
    context: Context,
    viewModel: FileInfoViewModel,
    nav: () -> NavController,
    snack: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        val file by viewModel.fileFlow.collectAsState()
        Knob(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
        if (file == null) {
            LinearProgressIndicator(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
        } else {
            Row(Modifier.fillMaxWidth()) {
                when (file!!) {
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
                            .padding(
                                start = 12.dp,
                                top = 16.dp,
                                end = 12.dp,
                                bottom = 12.dp
                            )
                            .size(150.dp)
//                            .paint(painterResource(id = com.sorrowblue.comicviewer.framework.resource.R.drawable.bg_folder))
                    )
                }
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        text = file?.name.orEmpty(),
                        style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.onSurface)
                    )
                    Text(
                        text = file?.path.orEmpty(),
                        style = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.onSurface)
                    )
                }
            }
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                mainAxisAlignment = FlowMainAxisAlignment.Center,
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 0.dp
            ) {
                if (file is Book) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                (file as Book).path.substringAfterLast('.').lowercase()
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.TwoTone.Extension,
                                null,
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        },
                    )
                }
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
            FlowRow(
                mainAxisSpacing = 8.dp, crossAxisSpacing = 0.dp, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
                FilledTonalButtonIcon({
                    viewModel.addReadLater {
                        snack("「後で読む」に追加しました。")
                    }
                }, "Read later", Icons.TwoTone.WatchLater, null)
                FilledTonalButtonIcon({
                    nav().navigate("http://comicviewer.sorrowblue.com/favorite/add?serverId=${viewModel.fileFlow.value!!.bookshelfId.value}&filePath=${file!!.path.encodeBase64()}".toUri())
                }, "Save to favorites", Icons.TwoTone.Favorite, null)
                FilledTonalButtonIcon({
                    nav().navigate("http://comicviewer.sorrowblue.com/folder?serverId=${viewModel.fileFlow.value!!.bookshelfId.value}&path=${file!!.parent.encodeBase64()}".toUri())
                }, "Open folder", Icons.TwoTone.FolderOpen, null)
            }
        }
    }
}

@Composable
fun Knob(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(32.dp)
            .alpha(0.4f)
            .height(4.dp)
            .background(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(size = 8.dp)
            )
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
