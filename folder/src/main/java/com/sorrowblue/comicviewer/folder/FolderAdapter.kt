package com.sorrowblue.comicviewer.folder

import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigator
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.folder.viewholder.FolderViewHolder

class FolderAdapter(
    display: FolderDisplaySettings.Display,
    private val onClick: (File, String, FragmentNavigator.Extras) -> Unit,
    private val onLongClick: (File) -> Unit
) : PagingDataAdapter<File, FolderViewHolder<out ViewBinding>>(
    object : DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File) = oldItem.path == newItem.path
        override fun areContentsTheSame(oldItem: File, newItem: File) =
            oldItem.path == newItem.path && oldItem.lastModifier == newItem.lastModifier && oldItem.size == newItem.size
    }
) {

    var display = display
        set(value) {
            field = value
            refresh()
        }

    override fun getItemViewType(position: Int) = display.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (display) {
        FolderDisplaySettings.Display.GRID -> FolderViewHolder.Grid(parent, onClick, onLongClick)
        FolderDisplaySettings.Display.LIST -> FolderViewHolder.List(parent, onClick, onLongClick)
    }

    override fun onBindViewHolder(holder: FolderViewHolder<out ViewBinding>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: FolderViewHolder<out ViewBinding>) {
        super.onViewRecycled(holder)
        holder.clear()
    }
}
