package com.sorrowblue.comicviewer.book

import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.request.Disposable
import coil.size.Size
import coil.transform.Transformation
import com.sorrowblue.comicviewer.book.databinding.BookItemBinding
import com.sorrowblue.comicviewer.book.databinding.BookItemNextBinding
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.request.BookPageRequest
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder
import kotlin.properties.Delegates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class Position {
    START,
    CENTER,
    END
}

private const val VIEW_TYPE_IMAGE = 0
private const val VIEW_TYPE_NEXT = 1
private const val VIEW_TYPE_PREV = 2

internal class ComicAdapter(
    val book: Book,
    count: Int,
    private val placeholder: String?,
    val onClick: (Position) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var prevComic by Delegates.observable<Book?>(null) { _, _, _ ->
        notifyItemChanged(0)
    }

    var nextComic by Delegates.observable<Book?>(null) { _, _, _ ->
        notifyItemChanged(itemCount - 1)
    }

    var currentList: List<PageInfo> = List(count) { PageInfo(it, PageInfo.Pos.UNKNOWN) }

    override fun getItemCount() = currentList.size + 2

    override fun getItemViewType(position: Int) = when (position) {
        0 -> VIEW_TYPE_PREV
        itemCount - 1 -> VIEW_TYPE_NEXT
        else -> VIEW_TYPE_IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_NEXT -> RelationViewHolder(parent)
        VIEW_TYPE_PREV -> RelationViewHolder(parent)
        else -> ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val item = currentList[position - 1]
            when (item.pos) {
                PageInfo.Pos.UNKNOWN -> holder.initLoad(item)
                PageInfo.Pos.LEFT -> holder.leftLoad(item)
                PageInfo.Pos.RIGHT -> holder.rightLoad(item)
                PageInfo.Pos.NONE -> holder.load(item)
            }
        } else if (holder is RelationViewHolder) {
            val (item, rel) = if (position == 0) prevComic to GetNextComicRel.PREV else nextComic to GetNextComicRel.NEXT
            holder.bind(item, rel)
        }
    }


    val requestList: MutableList<Disposable?> = MutableList(count) { null }

    inner class RelationViewHolder(
        parent: ViewGroup,
    ) : ViewBindingViewHolder<BookItemNextBinding>(parent, BookItemNextBinding::inflate) {

        fun bind(book: Book?, rel: GetNextComicRel) {
            if (book != null) {
                binding.bookName.text = book.name
                binding.bookThumbnail.load(book)
                binding.bookNext.text = when (rel) {
                    GetNextComicRel.NEXT -> "次の本"
                    GetNextComicRel.PREV -> "前の本"
                }
                binding.bookNext.isVisible = true
                binding.bookNext.setOnClickListener {
                    val bundle = BookFragmentArgs(book.serverId.value, book.path).toBundle()
                    it.findNavController().navigate(
                        R.id.book_navigation,
                        bundle,
                        navOptions { popUpTo(R.id.book_navigation) { inclusive = true } })
                }
            } else {
                binding.bookName.text = "本はありません"
                binding.bookThumbnail.load(com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_undraw_not_found_re_44w9)
                binding.bookNext.isVisible = false
                binding.bookNext.setOnClickListener(null)
            }
        }
    }

    inner class ViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<BookItemBinding>(parent, BookItemBinding::inflate) {

        init {
            binding.start.setOnClickListener { onClick.invoke(Position.START) }
            binding.center.setOnClickListener { onClick.invoke(Position.CENTER) }
            binding.end.setOnClickListener { onClick.invoke(Position.END) }
        }

        fun initLoad(item: PageInfo) {
            binding.bookTextview.text = "ページ：${item.index + 1} 見開き：${item.pos.name}"
            if (requestList[item.index]?.isDisposed == false) return
            requestList[item.index] =
                binding.viewerImageview.load(BookPageRequest(book to item.index)) {
                    if (item.index == 0) {
                        memoryCachePolicy(CachePolicy.ENABLED)
                        placeholderMemoryCacheKey(placeholder)
                    }
                    transformations(WhiteTrimTransformation, object : Transformation {
                        override val cacheKey = ""
                        override suspend fun transform(input: Bitmap, size: Size): Bitmap {
                            return if (input.height < input.width) {
                                // 見開き表示する必要あり
                                val index: Int
                                currentList = currentList.toMutableList().apply {
                                    index = indexOf(item)
                                    if (0 <= index) {
                                        set(index, item.copy(pos = PageInfo.Pos.RIGHT))
                                        add(index + 1, item.copy(pos = PageInfo.Pos.LEFT))
                                    }
                                }
                                if (0 <= index) {
                                    withContext(Dispatchers.Main) {
                                        notifyItemInserted(index + 2)
                                        notifyItemChanged(index + 2)
                                    }
                                }
                                Bitmap.createBitmap(
                                    input,
                                    input.width / 2,
                                    0,
                                    input.width / 2,
                                    input.height
                                )
                            } else {
                                // 見開き表示する必要なし
                                val index: Int
                                currentList = currentList.toMutableList().apply {
                                    index = indexOf(item)
                                    if (0 <= index) {
                                        set(index, item.copy(pos = PageInfo.Pos.NONE))
                                    }
                                }
                                input
                            }
                        }
                    })
                    /*
                    listener { request, result ->
                        val drawable = result.drawable
                        if (drawable.intrinsicHeight < drawable.intrinsicWidth) {
                            // 見開き表示
                            val index: Int
                            currentList = currentList.toMutableList().apply {
                                index = indexOf(item)
                                if (0 <= index) {
                                    set(index, item.copy(pos = PageInfo.Pos.RIGHT))
                                    add(index + 1, item.copy(pos = PageInfo.Pos.LEFT))
                                }
                            }
                            if (0 <= index) {
                                notifyItemInserted(index + 2)
                                notifyItemRangeChanged(index + 1, 2)
                            }
                        } else {
                            val index: Int
                            currentList = currentList.toMutableList().apply {
                                index = indexOf(item)
                                if (0 <= index) {
                                    set(index, item.copy(pos = PageInfo.Pos.NONE))
                                }
                                if (0 <= index) {
                                    notifyItemChanged(index + 1)
                                }
                            }
                        }
                    }
*/
                }.apply {
                    job.invokeOnCompletion { requestList[item.index] = null }
                }

        }

        fun leftLoad(item: PageInfo) {
            binding.bookTextview.text = "ページ：${item.index + 1} 見開き：${item.pos.name}"
            binding.viewerImageview.load(BookPageRequest( book to item.index)) {
                transformations(MihirakiSplitTransformation(true), WhiteTrimTransformation)
            }
        }

        fun rightLoad(item: PageInfo) {
            binding.bookTextview.text = "ページ：${item.index + 1} 見開き：${item.pos.name}"
            binding.viewerImageview.load(BookPageRequest( book to item.index)) {
                transformations(MihirakiSplitTransformation(false), WhiteTrimTransformation)
            }
        }

        fun load(item: PageInfo) {
            binding.bookTextview.text = "ページ：${item.index + 1} 見開き：${item.pos.name}"
            binding.viewerImageview.load(BookPageRequest( book to item.index)) { transformations(WhiteTrimTransformation) }
        }
    }
}
