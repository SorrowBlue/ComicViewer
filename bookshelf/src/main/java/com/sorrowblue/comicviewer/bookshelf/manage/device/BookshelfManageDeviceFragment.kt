package com.sorrowblue.comicviewer.bookshelf.manage.device

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import dagger.hilt.android.AndroidEntryPoint
import logcat.logcat

@AndroidEntryPoint
internal class BookshelfManageDeviceFragment : FrameworkFragment() {

    private val viewModel: BookshelfDeviceEditViewModel by viewModels()
    private val commonViewModel: CommonViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppMaterialTheme {
                    BookshelfDeviceEditScreen(findNavController(), openDocumentTree = {
                        openDirectory()
                    })
                }
            }
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            logcat { "data=${it.data?.data}" }
            it.data?.data?.let { uri ->
                viewModel.data.value = uri
            } ?: kotlin.run {
                viewModel.data.value = null
                commonViewModel.snackbarMessage.tryEmit("操作がキャンセルされました")
            }
        }

    private fun openDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        }
        activityResultLauncher.launch(intent)
    }
}
