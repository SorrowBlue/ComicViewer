package com.sorrowblue.comicviewer.settings.feature

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder
import com.sorrowblue.comicviewer.settings.feature.databinding.SettingsFeatureItemBinding

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FeatureItem>() {
    override fun areItemsTheSame(oldItem: FeatureItem, newItem: FeatureItem) =
        oldItem.feature == newItem.feature

    override fun areContentsTheSame(oldItem: FeatureItem, newItem: FeatureItem) = oldItem == newItem
}

internal class FeatureListAdapter(val startDownload: (FeatureItem) -> Unit) :
    ListAdapter<FeatureItem, FeatureListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(parent: ViewGroup) : ViewBindingViewHolder<SettingsFeatureItemBinding>(
        parent, SettingsFeatureItemBinding::inflate
    ) {
        fun bind(item: FeatureItem) {
            binding.icon.setIconResource(item.feature.icon)
            binding.moduleName.setText(item.feature.title)
            binding.description.setText(item.feature.description)
            binding.download.setOnClickListener {
                startDownload.invoke(item)
            }
            binding.uninstall.setOnClickListener {
                startDownload.invoke(item)
            }
            binding.cancel.setOnClickListener {
                startDownload.invoke(item)
            }

            when (item.status) {
                InstallStatus.Cancelled -> {
                    binding.status.text = "CANCELLED"
                    binding.progress.isVisible = false
                    binding.download.isVisible = true
                    binding.cancel.isVisible = false
                    binding.uninstall.isVisible = false
                }

                is InstallStatus.Failed -> {
                    binding.status.text = "FAILED"
                    binding.progress.isVisible = false
                    binding.download.isVisible = true
                    binding.cancel.isVisible = false
                    binding.uninstall.isVisible = false
                }

                InstallStatus.Installed -> {
                    binding.status.text = "INSTALLED"
                    binding.progress.isVisible = false
                    binding.download.isVisible = false
                    binding.cancel.isVisible = false
                    binding.uninstall.isVisible = true
                }

                InstallStatus.NotInstall -> {
                    binding.status.text = "NOT_INSTALL"
                    binding.progress.isVisible = false
                    binding.download.isVisible = true
                    binding.cancel.isVisible = false
                    binding.uninstall.isVisible = false
                }

                is InstallStatus.Progress -> {
                    binding.status.text = when (item.status.status) {
                        SplitInstallSessionStatus.CANCELING -> "CANCELING"
                        SplitInstallSessionStatus.DOWNLOADED -> "DOWNLOADED"
                        SplitInstallSessionStatus.DOWNLOADING -> "DOWNLOADING"
                        SplitInstallSessionStatus.INSTALLING -> "INSTALLING"
                        SplitInstallSessionStatus.PENDING -> "PENDING"
                        else -> ""
                    }
                    binding.progress.isVisible = true
                    binding.progress.isIndeterminate = when (item.status.status) {
                        SplitInstallSessionStatus.CANCELING -> true
                        SplitInstallSessionStatus.DOWNLOADED -> false
                        SplitInstallSessionStatus.DOWNLOADING -> false
                        SplitInstallSessionStatus.INSTALLING -> true
                        SplitInstallSessionStatus.PENDING -> true
                        else -> false
                    }
                    binding.progress.progress =
                        if (item.status.status == SplitInstallSessionStatus.DOWNLOADING) {
                            (item.status.bytesDownloaded.toDouble() / item.status.bytesTotal).toInt()
                        } else {
                            0
                        }
                    binding.download.isVisible = false
                    binding.cancel.isVisible = true
                    binding.uninstall.isVisible = false
                }
            }
        }
    }
}
