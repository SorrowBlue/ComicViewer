package com.sorrowblue.comicviewer.framework.ui.fragment

import androidx.fragment.app.DialogFragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn

context(DialogFragment)
fun <T> Flow<T>.launchInWithDialogLifecycle() =
    flowWithLifecycle(findNavController().currentBackStackEntry!!.lifecycle).launchIn(
        findNavController().currentBackStackEntry!!.lifecycleScope
    )
