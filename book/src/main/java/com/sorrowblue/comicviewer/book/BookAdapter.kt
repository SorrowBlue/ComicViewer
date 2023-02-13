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
import com.sorrowblue.comicviewer.framework.ui.fragment.encodeBase64
import com.sorrowblue.comicviewer.framework.ui.recyclerview.ViewBindingViewHolder
import kotlin.properties.Delegates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class BookAdapter(
    val book: Book,
    private val count: Int,
    private val placeholder: String?,
    private val onClick: (Position) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var prevBook by Delegates.observable<Book?>(null) { _, _, _ ->
        notifyItemChanged(0)
    }

    var nextBook by Delegates.observable<Book?>(null) { _, _, _ ->
        notifyItemChanged(itemCount - 1)
    }

    @get:Synchronized
    var currentList: List<BookPage> = buildList {
        add(BookPage.Next(false))
        repeat(count) { add(BookPage.Split(it, BookPage.Split.State.NOT_LOADED)) }
        add(BookPage.Next(true))
    }

    @get:Synchronized
    val disposableList: MutableList<Disposable?> = MutableList(count) { null }

    override fun getItemCount() = currentList.size

    override fun getItemViewType(position: Int) = currentList[position].viewType.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (BookViewType.values()[viewType]) {
            BookViewType.NExT -> NextViewHolder(parent)
            BookViewType.SPLIT -> SplitViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NextViewHolder) {
            if ((currentList[position] as BookPage.Next).isNext) {
                holder.bind(nextBook, true)
            } else {
                holder.bind(prevBook, false)
            }
        } else if (holder is SplitViewHolder) {
            val item = (currentList[position] as BookPage.Split)
            when (item.state) {
                BookPage.Split.State.NOT_LOADED -> holder.initLoad(item)
                BookPage.Split.State.LOADED_SPLIT_NON -> holder.load(item)
                BookPage.Split.State.LOADED_SPLIT_LEFT -> holder.leftLoad(item)
                BookPage.Split.State.LOADED_SPLIT_RIGHT -> holder.rightLoad(item)
            }
        }
    }

    inner class NextViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<BookItemNextBinding>(parent, BookItemNextBinding::inflate) {

        fun bind(book: Book?, isNext: Boolean) {
            if (book != null) {
                binding.bookName.text = book.name
                binding.bookThumbnail.load(book)
                binding.bookNext.text = if (isNext) "次の本" else "前の本"
                binding.bookNext.isVisible = true
                binding.bookNext.setOnClickListener {
                    val bundle = BookFragmentArgs(book.bookshelfId.value, book.path.encodeBase64()).toBundle()
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

    inner class SplitViewHolder(parent: ViewGroup) :
        ViewBindingViewHolder<BookItemBinding>(parent, BookItemBinding::inflate) {

        init {
            binding.start.setOnClickListener { onClick.invoke(Position.START) }
            binding.center.setOnClickListener { onClick.invoke(Position.CENTER) }
            binding.end.setOnClickListener { onClick.invoke(Position.END) }
        }

        fun initLoad(item: BookPage.Split) {
            // 読み込み中はSkip
            if (disposableList[item.index]?.isDisposed == false) return
            disposableList[item.index] =
                binding.image.load(BookPageRequest(book to item.index)) {
                    if (item.index == 0) {
                        memoryCachePolicy(CachePolicy.ENABLED)
                        placeholderMemoryCacheKey(placeholder)
                    }
                    transformations(object : Transformation {
                        override val cacheKey = ""
                        override suspend fun transform(input: Bitmap, size: Size): Bitmap {
                            return if (input.height <= input.width) {
                                // 分割表示する必要あり
                                val index: Int
                                currentList = currentList.toMutableList().apply {
                                    index = indexOf(item)
                                    if (0 < index) {
                                        set(
                                            index,
                                            item.copy(state = BookPage.Split.State.LOADED_SPLIT_RIGHT)
                                        )
                                        add(
                                            index + 1,
                                            item.copy(state = BookPage.Split.State.LOADED_SPLIT_LEFT)
                                        )
                                    }
                                }
                                if (0 < index) {
                                    withContext(Dispatchers.Main) {
                                        notifyItemInserted(index + 1)
                                        notifyItemChanged(index + 1)
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
                                // 分割表示する必要なし
                                val index: Int
                                currentList = currentList.toMutableList().apply {
                                    index = indexOf(item)
                                    if (0 < index) {
                                        set(
                                            index,
                                            item.copy(state = BookPage.Split.State.LOADED_SPLIT_NON)
                                        )
                                    }
                                }
                                input
                            }
                        }
                    })
                }.apply {
                    job.invokeOnCompletion {
                        disposableList[item.index] = null
                    }
                }
        }


        fun leftLoad(item: BookPage.Split) {
            binding.image.load(BookPageRequest(book to item.index)) {
                transformations(MihirakiSplitTransformation(true))
            }
        }

        fun rightLoad(item: BookPage.Split) {
            binding.image.load(BookPageRequest(book to item.index)) {
                transformations(MihirakiSplitTransformation(false))
            }
        }

        fun load(item: BookPage.Split) {
            binding.image.load(BookPageRequest(book to item.index)) {
                transformations()
            }
        }
    }
}
