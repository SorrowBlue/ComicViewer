package com.sorrowblue.comicviewer.file.list

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import coil.dispose
import coil.load
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.file.databinding.FileItemGridBinding
import com.sorrowblue.comicviewer.file.databinding.FileItemListBinding
import com.sorrowblue.comicviewer.framework.resource.R
import com.sorrowblue.comicviewer.framework.ui.converter.StringConverter.removeExtension
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder

class FileListAdapter(
    private var display: FolderDisplaySettings.Display,
    private var isEnabledThumbnail: Boolean,
    private val onClick: (File, String, FragmentNavigator.Extras) -> Unit,
    private val onLongClick: (File) -> Unit
) : PagingDataAdapter<File, FileListAdapter.FileViewHolder<out ViewBinding>>(
    object : DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File) = oldItem.path == newItem.path
        override fun areContentsTheSame(oldItem: File, newItem: File) = oldItem.areContentsTheSame(newItem)
    }
) {
    var isEditing: Boolean = false

    val selectedItemIds = mutableListOf<String>()

    fun setDisplay(display: FolderDisplaySettings.Display) {
        this.display = display
    }

    override fun getItemViewType(position: Int) = display.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (display) {
        FolderDisplaySettings.Display.GRID -> Grid(parent, isEnabledThumbnail, onClick, onLongClick)
        FolderDisplaySettings.Display.LIST -> List(parent, isEnabledThumbnail, onClick, onLongClick)
    }

    override fun onBindViewHolder(holder: FileViewHolder<out ViewBinding>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: FileViewHolder<out ViewBinding>) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    sealed class FileViewHolder<V : ViewBinding>(
        parent: ViewGroup,
        inflate: (LayoutInflater, ViewGroup, Boolean) -> V,
        val onClick: (File, String, FragmentNavigator.Extras) -> Unit,
        val onLongClick: (File) -> Unit
    ) : ViewBindingViewHolder<V>(parent, inflate) {
        abstract fun bind(file: File?)
        abstract fun clear()
    }

    inner class Grid(
        parent: ViewGroup,
        private val isEnabledThumbnail: Boolean,
        onClick: (File, String, FragmentNavigator.Extras) -> Unit,
        onLongClick: (File) -> Unit,
    ) : FileViewHolder<FileItemGridBinding>(
        parent,
        FileItemGridBinding::inflate,
        onClick,
        onLongClick
    ) {

        override fun clear() {
            binding.bookThumbnail.dispose()
            binding.folderThumbnail.dispose()
        }

        private fun TextView.multilineEllipsize(maxLines: Int, where: TextUtils.TruncateAt) {
            if (maxLines >= lineCount) {
                // ellipsizeする必要無し
                return
            }
            var avail = 0.0f
            for (i in 0 until maxLines) {
                avail += layout.getLineMax(i)
            }
            text = TextUtils.ellipsize(text, paint, avail, where)
        }

        override fun bind(file: File?) {
            binding.root.transitionName = file?.path
            binding.file = file
            binding.cardView.isChecked = selectedItemIds.contains(file?.path)
            binding.name.text = file?.name?.removeExtension()
            binding.name.doOnGlobalLayout {
                binding.name.multilineEllipsize(2, TextUtils.TruncateAt.MIDDLE)
            }
            if (isEnabledThumbnail) {
                if (file is Book) {
                    binding.bookThumbnail.load(file) {
                        error(R.drawable.ic_twotone_broken_image_24)
                    }
                    binding.folderThumbnail.setImageDrawable(null)
                } else if (file is Folder) {
                    binding.bookThumbnail.setImageDrawable(null)
                    binding.folderThumbnail.load(file) {
                        error(R.drawable.ic_twotone_broken_image_24)
                    }
                }
            } else {
                if (file is Book) {
                    binding.bookThumbnail.setImageResource(R.drawable.ic_twotone_book_24)
                    binding.folderThumbnail.setImageDrawable(null)
                } else if (file is Folder) {
                    binding.bookThumbnail.setImageDrawable(null)
                    binding.folderThumbnail.setImageDrawable(null)
                }
            }
            binding.root.setOnLongClickListener {
                if (file != null) {
                    onLongClick(file)
                    true
                } else {
                    false
                }
            }
            binding.root.setOnClickListener {
                if (file != null) {
                    if (isEditing) {
                        binding.cardView.isChecked = !binding.cardView.isChecked
                        if (binding.cardView.isChecked) {
                            selectedItemIds.add(file.path)
                        } else {
                            selectedItemIds.remove(file.path)
                        }
                    } else {
                        onClick.invoke(
                            file,
                            binding.root.transitionName,
                            FragmentNavigatorExtras(binding.root to binding.root.transitionName)
                        )
                    }
                }
            }
        }
    }

    internal class List(
        parent: ViewGroup,
        private val isEnabledThumbnail: Boolean,
        onClick: (File, String, FragmentNavigator.Extras) -> Unit,
        onLongClick: (File) -> Unit
    ) : FileViewHolder<FileItemListBinding>(
        parent,
        FileItemListBinding::inflate,
        onClick,
        onLongClick
    ) {

        override fun clear() {
            binding.folderThumbnail.dispose()
            binding.bookThumbnail.dispose()
        }

        override fun bind(file: File?) {
            binding.file = file
            if (isEnabledThumbnail) {
                if (file is Book) {
                    binding.bookThumbnail.load(file) {
                        error(R.drawable.ic_twotone_broken_image_24)
                    }
                    binding.folderThumbnail.setImageDrawable(null)
                } else if (file is Folder) {
                    binding.bookThumbnail.setImageDrawable(null)
                    binding.folderThumbnail.load(file) {
                        error(R.drawable.ic_twotone_broken_image_24)
                    }
                }
            } else {
                if (file is Book) {
                    binding.bookThumbnail.setImageResource(R.drawable.ic_twotone_book_24)
                    binding.folderThumbnail.setImageDrawable(null)
                } else if (file is Folder) {
                    binding.bookThumbnail.setImageDrawable(null)
                    binding.folderThumbnail.setImageDrawable(null)
                }
            }
            binding.root.setOnLongClickListener {
                if (file != null) {
                    onLongClick(file)
                    true
                } else {
                    false
                }
            }
            binding.root.setOnClickListener {
                if (file != null) {
                    onClick.invoke(
                        file,
                        binding.root.transitionName,
                        FragmentNavigatorExtras(binding.root to binding.root.transitionName)
                    )
                }
            }
        }
    }
}
