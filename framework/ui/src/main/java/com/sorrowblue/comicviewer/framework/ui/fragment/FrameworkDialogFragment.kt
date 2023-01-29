package com.sorrowblue.comicviewer.framework.ui.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import logcat.logcat

abstract class FrameworkDialogFragment : DialogFragment() {

    abstract fun onCreateDialog(savedInstanceState: Bundle?, builder: MaterialAlertDialogBuilder)

    abstract fun onShow(dialog: AlertDialog)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logcat { "onCreate" }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        logcat { "onCreateDialog" }
        val builder = MaterialAlertDialogBuilder(requireContext())
        onCreateDialog(savedInstanceState, builder)
        return builder.create().apply {
            setOnShowListener { onShow(it as AlertDialog) }
        }
    }

    override fun onStart() {
        super.onStart()
        logcat { "onStart" }
    }

    override fun onResume() {
        super.onResume()
        logcat { "onResume" }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        logcat { "onDismiss" }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        logcat { "onCancel" }
    }
}
