package com.sorrowblue.comicviewer.server.info

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.server.info.databinding.ServerInfoDialogBinding
import com.sorrowblue.comicviewer.server.info.databinding.ServerInfoItemBinding
import com.sorrowblue.comicviewer.server.management.device.ServerManagementDeviceFragmentArgs
import com.sorrowblue.comicviewer.server.management.smb.ServerManagementSmbFragmentArgs
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
                is Server.DeviceStorage -> findNavController().navigate(
                    ServerInfoFragmentDirections.actionServerInfoToServerManagementNavigationDevice().actionId,
                    ServerManagementDeviceFragmentArgs(server, viewModel.bookshelf.value).toBundle()
                )
                is Server.Smb -> findNavController().navigate(
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
    fun Server?.toTypeString(context: Context) = when (this) {
        is Server.DeviceStorage -> "デバイスストレージ"
        is Server.Smb -> "LAN/SMB"
        null -> ""
    }

    @JvmStatic
    fun Server?.toHost() = when (this) {
        is Server.DeviceStorage -> ""
        is Server.Smb -> host
        null -> ""
    }
}
