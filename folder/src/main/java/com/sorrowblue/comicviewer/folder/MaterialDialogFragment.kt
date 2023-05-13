package com.sorrowblue.comicviewer.folder

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sorrowblue.comicviewer.folder.databinding.FolderViewTitleBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkDialogFragment
import com.sorrowblue.comicviewer.framework.ui.navigation.setDialogFragmentResult

internal class MaterialDialogFragment : FrameworkDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?, builder: MaterialAlertDialogBuilder) {
        with(builder) {
            setTitle("本棚のスキャン")
            setIcon(com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_twotone_book_24)
            setMessage(R.string.folder_message_scan)
            setView(R.layout.folder_view_title)
            setNegativeButton("No") {_, _ ->
                setDialogFragmentResult("result", false)
            }
            setPositiveButton("Continue") { _, _ ->
                setDialogFragmentResult("result", true)
            }
        }
    }
}
