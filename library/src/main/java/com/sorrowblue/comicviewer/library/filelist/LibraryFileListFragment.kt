package com.sorrowblue.comicviewer.library.filelist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.ui.AppBarConfiguration
import coil.ImageLoader
import coil.imageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.google.android.material.button.MaterialButton
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.library.R
import com.sorrowblue.comicviewer.library.databinding.LibraryFragmentCloudBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.onEach

abstract class LibraryFileListFragment : PagingFragment<File>(R.layout.library_fragment_cloud) {

    protected val binding: LibraryFragmentCloudBinding by viewBinding()
    abstract override val viewModel: LibraryFileListViewModel
    protected lateinit var file: File
    protected val profileImage
        get() =
            binding.toolbar.menu.findItem(R.id.menu_profile_image).actionView
                ?.findViewById<MaterialButton>(R.id.profile_image_btn)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarConfiguration = AppBarConfiguration(setOf())
        binding.viewModel = viewModel
        binding.toolbar.setupWithNavController()
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }

        binding.recyclerView.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true, bottom = true)
            }
        }

        viewModel.isAuthenticated.distinctUntilChanged().filterNot { it }.onEach {
            navigateToSignIn()
        }.launchInWithLifecycle()
        profileImage.setOnClickListener {
            navigateToProfile()
        }
    }


    protected fun createFile(file: File) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_TITLE, file.name)
        intent.type = "*/*"
        this.file = file
        createFileRequest.launch(intent)
    }

    private val createFileRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data?.data != null) {
                enqueueDownload(it.data!!.data!!.toString(), file)
            }
        }

    abstract fun enqueueDownload(outputUri: String, file: File)
    abstract fun navigateToSignIn()
    abstract fun navigateToProfile()


    inline fun MaterialButton.load(
        data: Any?,
        imageLoader: ImageLoader = context.imageLoader,
        builder: ImageRequest.Builder.() -> Unit = {}
    ): Disposable {
        val request = ImageRequest.Builder(context)
            .data(data)
            .target { icon = it }
            .apply(builder)
            .build()
        return imageLoader.enqueue(request)
    }
}
