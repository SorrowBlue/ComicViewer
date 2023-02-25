package com.sorrowblue.comicviewer.library.cloud

import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingFragment
import com.sorrowblue.comicviewer.library.R
import com.sorrowblue.comicviewer.library.databinding.LibraryFragmentCloudBinding
import com.sorrowblue.jetpack.binding.viewBinding

abstract class CloudStorageFragment : PagingFragment<File>(R.layout.library_fragment_cloud) {

    protected val binding: LibraryFragmentCloudBinding by viewBinding()
}