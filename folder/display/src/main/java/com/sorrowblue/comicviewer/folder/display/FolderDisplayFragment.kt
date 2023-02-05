package com.sorrowblue.comicviewer.folder.display

import android.app.Dialog
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.sidesheet.SideSheetDialog
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.folder.display.databinding.FolderDisplayFragmentBinding
import com.sorrowblue.comicviewer.framework.ui.fragment.dialogViewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class FolderDisplayFragment : DialogFragment() {

    private val binding: FolderDisplayFragmentBinding by dialogViewBinding()
    private val viewModel: FolderDisplayViewModel by viewModels()

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
                binding.spanCountLabel.isVisible = it == FolderDisplaySettings.Display.GRID
                binding.spanCountSlider.isVisible = it == FolderDisplaySettings.Display.GRID
                binding.viewTypeGroup.check(
                    when (it) {
                        FolderDisplaySettings.Display.GRID -> R.id.view_type_grid
                        FolderDisplaySettings.Display.LIST -> R.id.view_type_list
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
                        FolderDisplaySettings.Sort.DATE -> R.id.sort_type_date
                        FolderDisplaySettings.Sort.NAME -> R.id.sort_type_name
                        FolderDisplaySettings.Sort.SIZE -> R.id.sort_type_size
                    }
                )
            }.flowWithLifecycle(navBackStackEntry.lifecycle)
                .launchIn(navBackStackEntry.lifecycleScope)
            viewModel.orderFlow.stateIn(navBackStackEntry.lifecycleScope).onEach {
                binding.orderTypeGroup.check(
                    when (it) {
                        FolderDisplaySettings.Order.ASC -> R.id.order_type_asc
                        FolderDisplaySettings.Order.DESC -> R.id.order_type_desc
                    }
                )
            }.flowWithLifecycle(navBackStackEntry.lifecycle)
                .launchIn(navBackStackEntry.lifecycleScope)
        }
        binding.viewTypeGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.view_type_grid -> viewModel.update(FolderDisplaySettings.Display.GRID)
                R.id.view_type_list -> viewModel.update(FolderDisplaySettings.Display.LIST)
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
                R.id.sort_type_date -> viewModel.update(FolderDisplaySettings.Sort.DATE)
                R.id.sort_type_name -> viewModel.update(FolderDisplaySettings.Sort.NAME)
                R.id.sort_type_size -> viewModel.update(FolderDisplaySettings.Sort.SIZE)
            }
        }

        // Order
        binding.orderTypeGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.order_type_asc -> viewModel.update(FolderDisplaySettings.Order.ASC)
                R.id.order_type_desc -> viewModel.update(FolderDisplaySettings.Order.DESC)
            }
        }
    }
}
