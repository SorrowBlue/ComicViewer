package com.sorrowblue.comicviewer.server.info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sorrowblue.comicviewer.domain.entity.server.DeviceStorage
import com.sorrowblue.comicviewer.domain.entity.server.Server
import com.sorrowblue.comicviewer.domain.entity.server.Smb
import com.sorrowblue.comicviewer.server.info.databinding.ServerInfoDialogBinding
import com.sorrowblue.comicviewer.server.management.device.ServerManagementDeviceFragmentArgs
import com.sorrowblue.comicviewer.server.management.smb.ServerManagementSmbFragmentArgs
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class ServerInfoFragment : BottomSheetDialogFragment(R.layout.server_info_dialog) {

    private val binding: ServerInfoDialogBinding by viewBinding()
    private val viewModel: ServerInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.remove.setOnClickListener {
            findNavController().navigate(
                ServerInfoFragmentDirections.actionServerInfoToServerRemoveConfirm(viewModel.server.value!!)
            )
        }
        binding.edit.setOnClickListener {
            when (val server = viewModel.server.value) {
                is DeviceStorage -> findNavController().navigate(
                    ServerInfoFragmentDirections.actionServerInfoToServerManagementNavigationDevice().actionId,
                    ServerManagementDeviceFragmentArgs(server, viewModel.bookshelf.value).toBundle()
                )
                is Smb -> findNavController().navigate(
                    ServerInfoFragmentDirections.actionServerInfoToServerManagementNavigationSmb().actionId,
                    ServerManagementSmbFragmentArgs(server, viewModel.bookshelf.value!!).toBundle()
                )
                null -> Unit
            }
        }
    }
}

object Converter {

    @JvmStatic
    fun Server?.toTypeString() = when (this) {
        is DeviceStorage -> R.string.server_info_label_device_storage
        is Smb -> R.string.server_info_label_smb
        null -> android.R.string.unknownName
    }

}
