package com.sorrowblue.comicviewer.framework.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.sorrowblue.comicviewer.framework.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.chrisbanes.insetter.InsetterApplyTypeDsl
import dev.chrisbanes.insetter.InsetterDsl
import javax.inject.Inject
import javax.inject.Qualifier
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import logcat.logcat

open class FrameworkFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    @Inject
    @FrameworkAppBarConfiguration
    lateinit var appBarConfiguration: AppBarConfiguration

    protected val fab get() = requireActivity().requireViewById<ExtendedFloatingActionButton>(R.id.framework_ui_fab)

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
    protected fun navigate(uri: Uri) {
        findNavController().navigate(uri)
    }
    protected fun navigate(directions: NavDirections, extras: FragmentNavigator.Extras) {
        findNavController().navigate(directions, extras)
    }

    protected fun Toolbar.setupWithNavController(navController: NavController) {
        setupWithNavController(navController, appBarConfiguration)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FrameworkAppBarConfiguration

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

fun InsetterDsl.type(
    ime: Boolean = false,
    systemBars: Boolean = false,
    navigationBars: Boolean = false,
    statusBars: Boolean = false,
    systemGestures: Boolean = false,
    mandatorySystemGestures: Boolean = false,
    displayCutout: Boolean = false,
    captionBar: Boolean = false,
    tappableElement: Boolean = false,
    f: InsetterApplyTypeDsl.() -> Unit,
) {
    type(
        ime,
        systemBars || navigationBars,
        systemBars || statusBars,
        systemGestures,
        mandatorySystemGestures,
        displayCutout,
        systemBars || captionBar,
        tappableElement,
        f
    )
}
