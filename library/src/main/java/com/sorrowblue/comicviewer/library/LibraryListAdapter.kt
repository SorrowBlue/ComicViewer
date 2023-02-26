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
            binding.root.transitionName = item::class.qualifiedName
            binding.root.setOnClickListener { onClick.invoke(item, FragmentNavigatorExtras(it to it.transitionName)) }
            binding.headline.setText(item.titleRes)
            binding.leadingIcon.setImageResource(item.iconRes)
            binding.trailingIcon.setImageResource(if (item.isInstalled) com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_twotone_arrow_right_24 else com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_twotone_download_24)
        }
    }
}
