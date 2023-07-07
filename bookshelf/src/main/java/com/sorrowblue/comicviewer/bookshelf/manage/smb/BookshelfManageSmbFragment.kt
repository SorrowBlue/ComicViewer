package com.sorrowblue.comicviewer.bookshelf.manage.smb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.databinding.InverseMethod
import androidx.navigation.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class BookshelfManageSmbFragment : FrameworkFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppMaterialTheme {
                    BookshelfSmbEditScreen(findNavController())
                }
            }
        }
    }
}


internal object AuthConverter {

    @JvmStatic
    @InverseMethod("buttonIdToBoolean")
    fun booleanToButtonId(value: Boolean): Int {
        return if (value) R.id.guest else R.id.username_password
    }

    @JvmStatic
    fun buttonIdToBoolean(value: Int): Boolean {
        return value == R.id.guest
    }
}


internal object PortConverter {

    @JvmStatic
    @InverseMethod("portToString")
    fun stringToPort(value: String?) = value?.toIntOrNull()

    @JvmStatic
    fun portToString(value: Int?) = value?.toString()
}

