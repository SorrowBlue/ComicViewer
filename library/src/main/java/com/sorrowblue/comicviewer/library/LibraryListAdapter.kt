package com.sorrowblue.comicviewer.library

import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder
import com.sorrowblue.comicviewer.library.databinding.LibraryItemLocalFeatureBinding
import com.sorrowblue.comicviewer.library.databinding.LibraryItemServiceBinding
import com.sorrowblue.comicviewer.framework.resource.R as FrameworkResourceR

internal class LibraryListAdapter(private val onClick: (Library, FragmentNavigator.Extras) -> Unit) :
    ListAdapter<Library, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<Library>() {
            override fun areItemsTheSame(oldItem: Library, newItem: Library) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Library, newItem: Library) =
                oldItem == newItem
        }
    ) {

    override fun getItemViewType(position: Int) = getItem(position).viewType.value

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (LibraryListViewType.values()[viewType]) {
            LibraryListViewType.LOCAL_FEATURE -> LocalFeatureViewHolder(parent)
            LibraryListViewType.CLOUD_STORAGE -> CloudStorageViewHolder(parent)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LocalFeatureViewHolder) {
            holder.bind(getItem(position) as LocalFeature)
        } else if (holder is CloudStorageViewHolder) {
            holder.bind(getItem(position) as CloudStorage)
        }
    }

    inner class LocalFeatureViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<LibraryItemLocalFeatureBinding>(
            parent, LibraryItemLocalFeatureBinding::inflate
        ) {

        fun bind(item: LocalFeature) {
            binding.root.transitionName = item.name
            binding.root.setOnClickListener { onClick.invoke(item, FragmentNavigatorExtras(it to it.transitionName)) }
            when (item) {
                LocalFeature.DOWNLOADED -> {
                    binding.headline.text = "ダウンロード済みの本"
                    binding.leadingIcon.setImageResource(com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_twotone_download_24)
                }
            }
        }
    }

    inner class CloudStorageViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<LibraryItemServiceBinding>(
            parent, LibraryItemServiceBinding::inflate
        ) {

        fun bind(item: CloudStorage) {
            binding.root.transitionName = item.name
            binding.root.setOnClickListener { onClick.invoke(item, FragmentNavigatorExtras(it to it.transitionName)) }
            when (item) {
                CloudStorage.GOOGLE_DRIVE -> {
                    binding.headline.text = "Google ドライブ"
                    binding.leadingIcon.setImageResource(FrameworkResourceR.drawable.ic_google_drive_icon_2020)
                }

                CloudStorage.BOX -> {
                    binding.headline.text = "BOX"
                    binding.leadingIcon.setImageResource(FrameworkResourceR.drawable.ic_box_blue_cmyk)
                }

                CloudStorage.MEGA -> {
                    binding.headline.text = "MEGA"
                    binding.leadingIcon.setImageResource(FrameworkResourceR.drawable.ic_mega_letter_logo)
                }

                CloudStorage.DROP_BOX -> {
                    binding.headline.text = "DropBox"
                    binding.leadingIcon.setImageResource(FrameworkResourceR.drawable.ic_dropbox_tab_32)
                }

                CloudStorage.ONE_DRIVE -> {
                    binding.headline.text = "OneDrive"
                    binding.leadingIcon.setImageResource(FrameworkResourceR.drawable.ic_microsoft_office_onedrive)
                }
            }
        }
    }
}
