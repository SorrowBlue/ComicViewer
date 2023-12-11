package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.random.Random
import kotlin.random.nextInt
import kotlinx.coroutines.flow.flowOf

@Preview(name = "phone", device = "id:pixel_7")
@Preview(name = "landscape", device = "spec:parent=pixel_7,orientation=landscape")
@Preview(name = "tablet", device = "id:pixel_tablet")
annotation class PreviewComic

@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480")
@Preview(name = "landscape", device = "spec:shape=Normal,width=640,height=360,unit=dp,dpi=480")
annotation class PreviewMobile

@Preview(name = "tablet", device = "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480")
annotation class PreviewTablet

fun previewBookFile(index: Int = 0): BookFile {
    val path = paths[index]
    val totalPageCount = Random.nextInt(0..999)
    return BookFile(
        bookshelfId = BookshelfId(0),
        name = names[index],
        parent = Path(path).parent.pathString,
        path = path + names[index] + ".zip",
        size = Random.nextLong(),
        lastModifier = LocalDateTime.now(Clock.systemDefaultZone()).toEpochSecond(ZoneOffset.UTC),
        cacheKey = "",
        lastPageRead = Random.nextInt(0..totalPageCount),
        totalPageCount = totalPageCount,
        lastReadTime = LocalDateTime.now(Clock.systemDefaultZone()).toEpochSecond(ZoneOffset.UTC),
    )
}

@Composable
fun fakeEmptyLazyPagingItems(): LazyPagingItems<File> {
    return flowOf(PagingData.empty<File>()).collectAsLazyPagingItems()
}

@Composable
fun fakeLazyPagingItems(): LazyPagingItems<File> {
    val files = paths.mapIndexed { index, path ->
        when (val previewFile = PreviewFile.entries[Random.nextInt(0..2)]) {
            PreviewFile.BookFile -> {
                val totalPageCount = Random.nextInt(Int.MAX_VALUE)
                BookFile(
                    bookshelfId = BookshelfId(previewFile.ordinal),
                    name = names[index],
                    parent = Path(path).parent.pathString,
                    path = path + names[index] + ".zip",
                    size = Random.nextLong(),
                    lastModifier = Random.nextLong(),
                    cacheKey = "",
                    lastPageRead = Random.nextInt(0..totalPageCount),
                    totalPageCount = totalPageCount,
                    lastReadTime = Random.nextLong(),
                )
            }

            PreviewFile.BookFolder -> {
                val totalPageCount = Random.nextInt(Int.MAX_VALUE)
                BookFolder(
                    bookshelfId = BookshelfId(previewFile.ordinal),
                    name = names[index],
                    parent = Path(path).parent.pathString,
                    path = path + names[index] + ".zip",
                    size = Random.nextLong(Long.MAX_VALUE),
                    lastModifier = Random.nextLong(Long.MAX_VALUE),
                    cacheKey = "",
                    lastPageRead = Random.nextInt(0..totalPageCount),
                    totalPageCount = totalPageCount,
                    lastReadTime = Random.nextLong(Long.MAX_VALUE),
                    count = Random.nextInt(Int.MAX_VALUE),
                )
            }

            PreviewFile.Folder -> {
                Folder(
                    bookshelfId = BookshelfId(previewFile.ordinal),
                    name = names[index],
                    parent = Path(path).parent.pathString,
                    path = path + names[index] + ".zip",
                    size = Random.nextLong(Long.MAX_VALUE),
                    lastModifier = Random.nextLong(Long.MAX_VALUE),
                    count = Random.nextInt(Int.MAX_VALUE),
                )
            }
        }
    }
    val pagingData = PagingData.from(files)
    val pagingDataFlow = flowOf(pagingData)
    return pagingDataFlow.collectAsLazyPagingItems()
}

private enum class PreviewFile {
    BookFile,
    BookFolder,
    Folder,
}

private val paths = listOf(
    "/ante/ipsum/primis/",
    "/faucibus/orci/luctus/",
    "/et/ultrices/posuere/",
    "/cubilia/curae/",
    "/In/ultricies/neque/venenatis/",
    "/scelerisque/",
    "/quam/in/molestie/tortor/Praesent/a/rhoncus/libero./",
    "/Proin/tempus/felis/ut/mauris/",
    "/tincidunt/pharetra./",
    "/Aliquam/condimentum/",
    "/nulla/ligula,/",
    "/eget/tincidunt/tellus/",
    "/feugiat/a./Maecenas/",
    "/tempor/efficitur/urna/",
    "/a/mattis/erat/pretium/",
    "Etiam/pellentesque/",
    "/lacus/mi/",
    "/nec/pretium/lacus/cursus/",
    "/eget/Praesent/enim/ligula/",
    "/porta/nec/",
)

private val names = listOf(
    "Curabitur",
    "non",
    "turpis",
    "eu",
    "turpis",
    "tempor",
    "bibendum",
    "nec",
    "in",
    "lorem.",
    "Cras",
    "vitae",
    "est diam",
    "Aenean",
    "a metus",
    "nisl .",
    "Proin",
    "feugiat",
    "Morbi",
    "sodales",
)
