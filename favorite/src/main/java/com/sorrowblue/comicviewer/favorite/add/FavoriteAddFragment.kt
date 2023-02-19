package com.sorrowblue.comicviewer.favorite.add

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sorrowblue.comicviewer.favorite.R
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteFragmentAddBinding
import com.sorrowblue.comicviewer.favorite.list.FavoriteListAdapter
import com.sorrowblue.comicviewer.framework.ui.flow.attachAdapter
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class FavoriteAddFragment : BottomSheetDialogFragment(R.layout.favorite_fragment_add) {

    private val binding: FavoriteFragmentAddBinding by viewBinding()
    private val viewModel: FavoriteAddViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = FavoriteListAdapter { favorite, _ -> viewModel.add(favorite) }
        binding.recyclerView.adapter = adapter
        viewModel.pagingDataFlow.attachAdapter(adapter)
        binding.close.setOnClickListener { dismiss() }
        binding.fab.setOnClickListener {
            findNavController().navigate(FavoriteAddFragmentDirections.actionFavoriteAddToFavoriteCreate())
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val bottomSheetDialog = it as BottomSheetDialog
                bottomSheetDialog.requireViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    .updateLayoutParams {
                        height = ViewGroup.LayoutParams.MATCH_PARENT
                    }
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
}
