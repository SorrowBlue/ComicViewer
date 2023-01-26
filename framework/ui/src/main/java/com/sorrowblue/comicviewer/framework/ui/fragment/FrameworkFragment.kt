package com.sorrowblue.comicviewer.framework.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import logcat.logcat

open class FrameworkFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    override fun onStart() {
        super.onStart()
        logcat { "onStart" }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logcat { "onViewCreated" }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logcat { "onCreate" }
    }

    override fun onResume() {
        super.onResume()
        logcat { "onResume" }
    }

    protected fun navigate(directions: NavDirections) {
        findNavController().navigate(directions)
    }
}

@HiltViewModel
class CommonViewModel @Inject constructor() : ViewModel() {

    val snackbarMessage = MutableSharedFlow<String>(0, 1, BufferOverflow.SUSPEND)
    var shouldKeepOnScreen = true

    val isRestored = MutableSharedFlow<Boolean>(0)
}

context(Fragment)
fun <T> Flow<T>.launchIn() = launchIn(viewLifecycleOwner.lifecycleScope)

context(Fragment)
fun <T> Flow<T>.launchInWithLifecycle() = flowWithLifecycle(viewLifecycleOwner.lifecycle).launchIn(viewLifecycleOwner.lifecycleScope)

context(AppCompatActivity)
fun <T> Flow<T>.launchInWithLifecycle() = flowWithLifecycle(lifecycle).launchIn(lifecycleScope)


