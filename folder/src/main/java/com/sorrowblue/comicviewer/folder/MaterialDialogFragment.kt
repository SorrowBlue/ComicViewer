package com.sorrowblue.comicviewer.folder

import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkDialogFragment
import com.sorrowblue.comicviewer.framework.ui.navigation.setDialogFragmentResult

internal class MaterialDialogFragment : FrameworkDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?, builder: MaterialAlertDialogBuilder) {
        with(builder) {
            setTitle("本棚のスキャン")
            setIcon(com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_twotone_book_24)
            setMessage(R.string.folder_message_scan)
            setView(R.layout.folder_view_title)
            setNegativeButton("No") { _, _ ->
                setDialogFragmentResult("result", false)
            }
            setPositiveButton("Continue") { _, _ ->
                setDialogFragmentResult("result", true)
            }
        }
    }
}
