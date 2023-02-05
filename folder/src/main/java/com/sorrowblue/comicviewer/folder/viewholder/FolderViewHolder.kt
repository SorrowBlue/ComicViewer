package com.sorrowblue.comicviewer.folder.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.viewbinding.ViewBinding
import coil.dispose
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.folder.databinding.FolderItemBinding
import com.sorrowblue.comicviewer.folder.databinding.FolderItemListBinding
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder

sealed class FolderViewHolder<V : ViewBinding>(
    parent: ViewGroup,
    inflate: (LayoutInflater, ViewGroup, Boolean) -> V,
    val onClick: (File, String, FragmentNavigator.Extras) -> Unit,
    val onLongClick: (File) -> Unit
) : ViewBindingViewHolder<V>(parent, inflate) {

    abstract fun bind(file: File?)
    abstract fun clear()

    internal class Grid(
        parent: ViewGroup,
        onClick: (File, String, FragmentNavigator.Extras) -> Unit,
        onLongClick: (File) -> Unit
    ) : FolderViewHolder<FolderItemBinding>(
        parent,
        FolderItemBinding::inflate,
        onClick,
        onLongClick
    ) {

        override fun clear() {
            binding.bookThumbnail.dispose()
            binding.folderThumbnail.dispose()
        }

        override fun bind(file: File?) {
            binding.root.transitionName = file?.path
            binding.file = file
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

    internal class List(
        parent: ViewGroup,
        onClick: (File, String, FragmentNavigator.Extras) -> Unit,
        onLongClick: (File) -> Unit
    ) : FolderViewHolder<FolderItemListBinding>(
        parent,
        FolderItemListBinding::inflate,
        onClick,
        onLongClick
    ) {

        override fun clear() {
            binding.folderThumbnail.dispose()
            binding.bookThumbnail.dispose()
        }

        override fun bind(file: File?) {
            binding.file = file
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
