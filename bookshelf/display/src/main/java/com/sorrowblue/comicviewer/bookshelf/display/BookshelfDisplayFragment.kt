package com.sorrowblue.comicviewer.bookshelf.display

import android.app.Dialog
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.sidesheet.SideSheetDialog
import com.sorrowblue.comicviewer.bookshelf.display.databinding.BookshelfDisplayFragmentBinding
import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.framework.ui.fragment.dialogViewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class BookshelfDisplayFragment : DialogFragment() {

    private val binding: BookshelfDisplayFragmentBinding by dialogViewBinding()
    private val viewModel: BookshelfDisplayViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return SideSheetDialog(requireContext()).apply {
            setContentView(binding.root)
            onDialogCreated()
        }
    }

    private fun onDialogCreated() {
        // View
        val navBackStackEntry = findNavController().previousBackStackEntry ?: return
        navBackStackEntry.lifecycleScope.launch {
            viewModel.displayFlow.stateIn(navBackStackEntry.lifecycleScope).onEach {
                binding.spanCountLabel.isVisible = it == BookshelfDisplaySettings.Display.GRID
                binding.spanCountSlider.isVisible = it == BookshelfDisplaySettings.Display.GRID
                binding.viewTypeGroup.check(
                    when (it) {
                        BookshelfDisplaySettings.Display.GRID -> R.id.view_type_grid
                        BookshelfDisplaySettings.Display.LIST -> R.id.view_type_list
                    }
                )
            }.flowWithLifecycle(navBackStackEntry.lifecycle)
                .launchIn(navBackStackEntry.lifecycleScope)
            viewModel.spanCountFlow.stateIn(navBackStackEntry.lifecycleScope).onEach {
                binding.spanCountSlider.value = it.toFloat()
            }.flowWithLifecycle(navBackStackEntry.lifecycle)
                .launchIn(navBackStackEntry.lifecycleScope)
            viewModel.sortFlow.stateIn(navBackStackEntry.lifecycleScope).onEach {
                binding.sortTypeGroup.check(
                    when (it) {
                        BookshelfDisplaySettings.Sort.DATE -> R.id.sort_type_date
                        BookshelfDisplaySettings.Sort.NAME -> R.id.sort_type_name
                        BookshelfDisplaySettings.Sort.SIZE -> R.id.sort_type_size
                    }
                )
            }.flowWithLifecycle(navBackStackEntry.lifecycle)
                .launchIn(navBackStackEntry.lifecycleScope)
            viewModel.orderFlow.stateIn(navBackStackEntry.lifecycleScope).onEach {
                binding.orderTypeGroup.check(
                    when (it) {
                        BookshelfDisplaySettings.Order.ASC -> R.id.order_type_asc
                        BookshelfDisplaySettings.Order.DESC -> R.id.order_type_desc
                    }
                )
            }.flowWithLifecycle(navBackStackEntry.lifecycle)
                .launchIn(navBackStackEntry.lifecycleScope)
        }
        binding.viewTypeGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.view_type_grid -> viewModel.update(BookshelfDisplaySettings.Display.GRID)
                R.id.view_type_list -> viewModel.update(BookshelfDisplaySettings.Display.LIST)
            }
        }

        // SpanCount
        binding.spanCountSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.update(value.toInt())
            }
        }

        // Sort
        binding.sortTypeGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.sort_type_date -> viewModel.update(BookshelfDisplaySettings.Sort.DATE)
                R.id.sort_type_name -> viewModel.update(BookshelfDisplaySettings.Sort.NAME)
                R.id.sort_type_size -> viewModel.update(BookshelfDisplaySettings.Sort.SIZE)
            }
        }

        // Order
        binding.orderTypeGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.order_type_asc -> viewModel.update(BookshelfDisplaySettings.Order.ASC)
                R.id.order_type_desc -> viewModel.update(BookshelfDisplaySettings.Order.DESC)
            }
        }
    }
}
