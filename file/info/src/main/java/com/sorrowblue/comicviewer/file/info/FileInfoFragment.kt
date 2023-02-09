package com.sorrowblue.comicviewer.file.info

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
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
                    val result by viewModel.fileFlow.collectAsState()
                    FileInfoScreen(
                        result,
                        {
                            viewModel.addReadLater(it) {
                                Snackbar.make(requireView(), "「後で見る」に追加しました。", Snackbar.LENGTH_SHORT).show()
                            }
                        },
                        { this@FileInfoFragment.findNavController().navigate("http://comicviewer.sorrowblue.com/favorite/add?serverId=${it.bookshelfId.value}&filePath=${it.path.encodeBase64()}".toUri()) },
                        { this@FileInfoFragment.findNavController().navigate("http://comicviewer.sorrowblue.com/folder?serverId=${it.bookshelfId.value}&path=${it.parent.encodeBase64()}".toUri()) }
                    )
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
