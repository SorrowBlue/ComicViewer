package com.sorrowblue.comicviewer.framework.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.sorrowblue.comicviewer.framework.ui.R
import dev.chrisbanes.insetter.InsetterApplyTypeDsl
import dev.chrisbanes.insetter.InsetterDsl
import javax.inject.Inject
import logcat.logcat

open class FrameworkFragment : Fragment {

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

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
        @Suppress("FrameworkOnly")
        findNavController().navigate(directions)
    }

    protected fun navigate(uri: Uri) {
        @Suppress("FrameworkOnly")
        findNavController().navigate(uri)
    }

    protected fun navigate(directions: NavDirections, extras: FragmentNavigator.Extras) {
        findNavController().navigate(directions, extras)
    }

    protected fun navigate(
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null
    ) {
        findNavController().navigate(resId, args, navOptions, navigatorExtras)
    }

    protected fun Toolbar.setupWithNavController() {
        setupWithNavController(findNavController(), appBarConfiguration)
    }
}

fun FrameworkFragment.checkSelfPermission(permission: String) = ContextCompat.checkSelfPermission(requireContext(), permission)

fun FrameworkFragment.makeSnackbar(
    text: CharSequence,
    @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT
) = Snackbar.make(requireActivity().requireViewById(R.id.activity_root), text, duration)
    .setAnchorView(R.id.framework_ui_fab)
    .apply {
        isAnchorViewLayoutListenerEnabled = true
    }

fun FrameworkFragment.makeSnackbar(
    @StringRes resId: Int,
    @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT
) = Snackbar.make(requireActivity().requireViewById(R.id.activity_root), resId, duration)
    .setAnchorView(R.id.framework_ui_fab)
    .apply {
        isAnchorViewLayoutListenerEnabled = true
    }

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

context(Fragment)
fun <T : Any, VH : RecyclerView.ViewHolder> PagingDataAdapter<T, VH>.submitDataWithLifecycle(data: PagingData<T>) {
    submitData(viewLifecycleOwner.lifecycle, data)
}
