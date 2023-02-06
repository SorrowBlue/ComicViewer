package com.sorrowblue.comicviewer.server.info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sorrowblue.comicviewer.domain.entity.server.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.server.InternalStorage
import com.sorrowblue.comicviewer.domain.entity.server.SmbServer
import com.sorrowblue.comicviewer.framework.ui.fragment.dialogViewBinding
import com.sorrowblue.comicviewer.server.info.databinding.ServerInfoDialogBinding
import com.sorrowblue.comicviewer.server.management.device.ServerManagementDeviceFragmentArgs
import com.sorrowblue.comicviewer.server.management.smb.ServerManagementSmbFragmentArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class ServerInfoFragment : BottomSheetDialogFragment(R.layout.server_info_dialog) {

    private val binding: ServerInfoDialogBinding by dialogViewBinding()
    private val viewModel: ServerInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.remove.setOnClickListener {
            findNavController().navigate(
                ServerInfoFragmentDirections.actionServerInfoToServerRemoveConfirm(viewModel.bookshelf.value!!.id.value)
            )
        }
        binding.edit.setOnClickListener {
            when (val server = viewModel.bookshelf.value) {
                is InternalStorage -> findNavController().navigate(
                    ServerInfoFragmentDirections.actionServerInfoToServerManagementNavigationDevice().actionId,
                    ServerManagementDeviceFragmentArgs(server.id.value).toBundle()
                )
                is SmbServer -> findNavController().navigate(
                    ServerInfoFragmentDirections.actionServerInfoToServerManagementNavigationSmb().actionId,
                    ServerManagementSmbFragmentArgs(server.id.value).toBundle()
                )
                null -> Unit
            }
        }
    }
}

object Converter {

    @JvmStatic
    fun Bookshelf?.toTypeString() = when (this) {
        is InternalStorage -> R.string.server_info_label_device_storage
        is SmbServer -> R.string.server_info_label_smb
        null -> android.R.string.unknownName
    }

}
