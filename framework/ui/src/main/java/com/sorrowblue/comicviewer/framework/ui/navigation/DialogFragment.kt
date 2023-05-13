package com.sorrowblue.comicviewer.framework.ui.navigation

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import logcat.logcat

inline fun <reified T> Fragment.setDialogFragmentResultListener(
    destinationId: Int,
    key: String,
    crossinline result: (T) -> Unit
) {
    val navController = findNavController()
    val navBackStackEntry = navController.getBackStackEntry(destinationId)
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME && navBackStackEntry.savedStateHandle.contains(
                key
            )
        ) {
            val value = navBackStackEntry.savedStateHandle.remove<Any?>(key)
                ?: return@LifecycleEventObserver
            if (value is Bundle) {
                result.invoke(T::class.java.getConstructor(Bundle::class.java).newInstance(value))
            } else {
                result.invoke(value as T)
            }
        }
    }
    navBackStackEntry.lifecycle.addObserver(observer)
    viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            navBackStackEntry.lifecycle.removeObserver(observer)
        }
    })
}

fun <T> DialogFragment.setDialogFragmentResult(key: String, value: T) {
    logcat { "value is FragmentResult = ${value is FragmentResult}" }
    if (value is FragmentResult) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(key, value.bundle)
    } else {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(key, value)
    }
}

open class FragmentResult(val bundle: Bundle)
