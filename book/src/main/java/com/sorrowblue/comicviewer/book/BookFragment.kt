package com.sorrowblue.comicviewer.book

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.color.MaterialColors
import com.google.android.material.slider.Slider
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.sorrowblue.comicviewer.book.databinding.BookFragmentBinding
import com.sorrowblue.comicviewer.domain.entity.settings.ViewerSettings
import com.sorrowblue.comicviewer.framework.resource.FrameworkDrawable
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class BookFragment : FrameworkFragment(R.layout.book_fragment) {

    private val binding: BookFragmentBinding by viewBinding()
    private val viewModel: BookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            scrimColor = MaterialColors.getColor(
                requireContext(),
                android.R.attr.colorBackground,
                Color.TRANSPARENT
            )
            setPathMotion(MaterialArcMotion())
        }
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
    }

    var isFavorite = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        setupViewPager2()
        setupHardware()
        setupBottomSheet()

        binding.viewerToolbar.setupWithNavController(findNavController())
        binding.viewerToolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.favorite) {
                isFavorite = !isFavorite
                it.setIcon(if (isFavorite) FrameworkDrawable.ic_twotone_favorite_24 else FrameworkDrawable.ic_twotone_favorite_border_24)
            }
            true
        }

        binding.viewPager2.attachToSlider(binding.slider) {
            viewModel.pageIndex.value = it
            viewModel.updateLastReadPage(it)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pageCount.filter { 0 < it - 1 }.collectLatest {
                binding.slider.valueTo = it - 1f
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.viewerSettings.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest(::setupFullScreen)
        }
    }

    override fun onPause() {
        super.onPause()
        (binding.viewPager2.adapter as? ComicAdapter)?.currentList?.getOrNull(binding.slider.value.toInt())
            ?.let { viewModel.updateLastReadPage(it.index) }
    }

    override fun onStop() {
        super.onStop()
        val windowInsetsController =
            WindowInsetsControllerCompat(requireActivity().window, binding.root)
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH

        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.attributes = window.attributes.apply {
            screenBrightness = -1f
        }
    }

    private fun setupViewPager2() {
        // Adapterをセットするまで非表示
        binding.viewPager2.isVisible = false
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.libraryComic.filterNotNull().collectLatest {
                val (server, book) = it.value
                val adapter =
                    ComicAdapter(server, book, book.totalPageCount, viewModel.placeholder) {
                        when (it) {
                            Position.START -> prevPage()
                            Position.CENTER -> viewModel.isVisibleUI.value =
                                !viewModel.isVisibleUI.value
                            Position.END -> nextPage()
                        }
                    }
                binding.viewPager2.adapter = adapter
                binding.viewPager2.setCurrentItem(book.lastPageRead + 1, false)
                binding.viewPager2.isVisible = true
                binding.preview.isVisible = false
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.prevComic.filterNotNull().collectLatest {
                (binding.viewPager2.adapter as? ComicAdapter)?.prevComic = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.nextComic.filterNotNull().collectLatest {
                (binding.viewPager2.adapter as? ComicAdapter)?.nextComic = it
            }
        }
        // 閉じ方向設定
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.layoutDirectionFlow.collectLatest {
                binding.viewPager2.layoutDirection = it
                binding.slider.layoutDirection = it
            }
        }
        // 先読みページ数
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.readAheadPageCountFlow.collectLatest(binding.viewPager2::setOffscreenPageLimit)
        }
    }

    private fun setupHardware() {
        binding.root.setOnKeyListener { _, i, keyEvent ->
            if (i == KeyEvent.KEYCODE_VOLUME_DOWN && keyEvent.action == KeyEvent.ACTION_DOWN) {
                nextPage()
                true
            } else if (i == KeyEvent.KEYCODE_VOLUME_UP && keyEvent.action == KeyEvent.ACTION_DOWN) {
                prevPage()
                true
            } else {
                false
            }
        }
        binding.root.isFocusableInTouchMode = true
        binding.root.requestFocus()
    }

    private fun setupBottomSheet() {
        val standardBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        standardBottomSheetBehavior.isHideable = true
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isVisibleUI.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                if (it) {
                    standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    binding.viewerAppBarLayout.setExpanded(true)
                } else {
                    standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    binding.viewerAppBarLayout.setExpanded(false)
                }
            }
        }
    }

    private fun ViewPager2.attachToSlider(slider: Slider, onChange: (pageIndex: Int) -> Unit) {
        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                (adapter as? ComicAdapter)?.let { adapter ->
                    onChange.invoke(if (position == 0) 0 else adapter.currentList.getOrElse(position - 1) { adapter.currentList.lastOrNull() }?.index ?: 0)
                }
            }
        })
        slider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                (adapter as? ComicAdapter)?.let { adapter ->
                    currentItem = adapter.currentList.indexOfFirst { it.index == value.toInt() } + 1
                }
            }
        }
    }

    private fun nextPage() {
        binding.viewPager2.currentItem++
    }

    private fun prevPage() {
        binding.viewPager2.currentItem--
    }

    private fun setupFullScreen(displaySettings: ViewerSettings) {
        val windowInsetsController =
            WindowInsetsControllerCompat(requireActivity().window, binding.root)
        binding.viewerToolbar.applyInsetter {
            type(displayCutout = true, statusBars = displaySettings.showStatusBar) { margin(true) }
        }
        binding.viewPager2.applyInsetter {
            type(
                displayCutout = true,
                statusBars = displaySettings.showStatusBar,
                navigationBars = displaySettings.showNavigationBar
            ) {
                padding(true)
            }
        }

        if (displaySettings.showStatusBar) {
            windowInsetsController.show(WindowInsetsCompat.Type.statusBars())
        } else {
            windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
        }
        if (displaySettings.showNavigationBar) {
            windowInsetsController.show(WindowInsetsCompat.Type.navigationBars())
        } else {
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
        }
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        val window = requireActivity().window
        if (displaySettings.keepOnScreen) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        if (displaySettings.enableBrightnessControl) {
            window.attributes = window.attributes.apply {
                screenBrightness = displaySettings.screenBrightness
            }
        } else {
            window.attributes = window.attributes.apply {
                screenBrightness = -1f
            }
        }
    }
}
