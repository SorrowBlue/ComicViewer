package com.sorrowblue.comicviewer.book

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.color.MaterialColors
import com.google.android.material.slider.Slider
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.sorrowblue.comicviewer.book.databinding.BookFragmentBinding
import com.sorrowblue.comicviewer.domain.entity.settings.ViewerSettings
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

fun Fragment.applyContainerTransform(transitionName: String?) {
    if (transitionName != null) {
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            scrimColor = MaterialColors.getColor(
                requireContext(),
                android.R.attr.colorBackground,
                Color.TRANSPARENT
            )
            setPathMotion(MaterialArcMotion())
        }
    }
    exitTransition = MaterialElevationScale(false)
    reenterTransition = MaterialElevationScale(true)
}

@AndroidEntryPoint
internal class BookFragment : FrameworkFragment(R.layout.book_fragment) {

    private val binding: BookFragmentBinding by viewBinding()
    private val viewModel: BookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyContainerTransform(viewModel.transitionName)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        setupViewPager2()
        setupHardware()
        setupBottomSheet()

        binding.toolbar.setupWithNavController()

        binding.viewPager2.attachToSlider(binding.slider) {
            viewModel.pageIndex.value = it
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

    override fun onResume() {
        super.onResume()
        binding.appBarLayout.elevation = binding.bottomSheet.elevation
    }

    override fun onPause() {
        super.onPause()
        val bookAdapter = binding.viewPager2.adapter as? BookAdapter ?: return
        bookAdapter.currentList.getOrNull(binding.slider.value.toInt())?.let {
            val index = when (it) {
                is BookPage.Next -> if (it.isNext) bookAdapter.currentList.filterIsInstance<BookPage.Split>().last().index else 0
                is BookPage.Split -> it.index
            }
            viewModel.updateLastReadPage(index)
        }
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
            val book = viewModel.bookFlow.filterNotNull().first()
            val adapter = BookAdapter(book, book.totalPageCount, viewModel.placeholder) {
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.prevComic.filterNotNull().collectLatest {
                (binding.viewPager2.adapter as? BookAdapter)?.prevBook = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.nextComic.filterNotNull().collectLatest {
                (binding.viewPager2.adapter as? BookAdapter)?.nextBook = it
            }
        }

        // 綴じ方向設定
        viewModel.viewerSettings.map { it.bindingDirection }.distinctUntilChanged().onEach {
            val direction = when (it) {
                ViewerSettings.BindingDirection.RIGHT -> ViewPager2.LAYOUT_DIRECTION_RTL
                ViewerSettings.BindingDirection.LEFT -> ViewPager2.LAYOUT_DIRECTION_LTR
            }
            binding.viewPager2.layoutDirection = direction
            binding.slider.layoutDirection = direction
        }.launchInWithLifecycle()

        // 先読みページ数
        viewModel.viewerSettings.map { it.readAheadPageCount }.distinctUntilChanged().onEach {
            binding.viewPager2.offscreenPageLimit = it
        }.launchInWithLifecycle()
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
                    binding.appBarLayout.setExpanded(true)
                } else {
                    standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    binding.appBarLayout.setExpanded(false)
                }
            }
        }
    }

    private fun ViewPager2.attachToSlider(slider: Slider, onChange: (pageIndex: Int) -> Unit) {
        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val bookAdapter = adapter as? BookAdapter ?: return
                val splitList = bookAdapter.currentList.filterIsInstance<BookPage.Split>()
                // 0 12345678 9
                onChange.invoke(
                    when (position) {
                        0 -> splitList.first().index
                        bookAdapter.currentList.lastIndex -> splitList.last().index
                        else -> splitList[position - 1].index
                    }
                )
            }
        })
        slider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                (adapter as? BookAdapter)?.let { adapter ->
                    currentItem = adapter.currentList.filterIsInstance<BookPage.Split>().indexOfFirst { it.index == value.toInt() } + 1
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
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
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
