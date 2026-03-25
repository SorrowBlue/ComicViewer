package com.sorrowblue.comicviewer.framework.ui.preview.fake

import com.composables.core.androidx.annotation.IntRange
import com.sorrowblue.comicviewer.domain.model.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.Folder
import okio.Path.Companion.toPath

@OptIn(InternalDataApi::class)
fun fakeDeviceStorage(bookshelfId: Int = 0, name: String = nextLoremIpsum()) = DeviceStorage(
    id = BookshelfId(bookshelfId),
    displayName = name,
    fileCount = 999,
    isDeleted = false,
)

@OptIn(InternalDataApi::class)
fun fakeSmbServer(bookshelfId: Int = 0, name: String = nextLoremIpsum()) = SmbServer(
    id = BookshelfId(bookshelfId),
    displayName = name,
    host = "198.51.100.254",
    port = 455,
    auth = SmbServer.Auth.UsernamePassword("example.com", nextLoremIpsum(), nextLoremIpsum()),
)

@OptIn(InternalDataApi::class)
fun fakeBookFile(bookshelfId: Int = 0, @IntRange(0, 19) index: Int = 0) = BookFile(
    bookshelfId = BookshelfId(bookshelfId),
    name = FileNames[index],
    parent = FilePaths[index].removeSuffix("/${FileNames[index]}"),
    path = FilePaths[index],
    size = FileNames[index].hashCode().toLong(),
    lastModifier = FileNames[index].hashCode().toLong(),
    isHidden = false,
    totalPageCount = FileNames[index].hashCode(),
    lastPageRead = FileNames[index].hashCode() / 2,
)

@OptIn(InternalDataApi::class)
fun fakeFolder(bookshelfId: Int = 0, @IntRange(0, 19) index: Int = 0) = Folder(
    bookshelfId = BookshelfId(bookshelfId),
    name = FilePaths[index].toPath().parent?.name.orEmpty(),
    parent = FilePaths[index].toPath().parent?.parent.toString(),
    path = FilePaths[index].toPath().parent.toString(),
    size = FileNames[index].hashCode().toLong(),
    lastModifier = FileNames[index].hashCode().toLong(),
    isHidden = false,
)

private val FileNames = listOf(
    "[作者] マンガタイトル 第01巻.cbz",
    "[作者] マンガタイトル 第02巻.cbz",
    "Adventure_Quest_Ch_123.zip",
    "週刊少年ダミー 2024年12号.pdf",
    "【新連載】異世界から来たエンジニアの日常_01.cbr",
    "My_Sketchbook_2023_Archive.zip",
    "テスト用コミックデータ_カラー版.pdf",
    "[Circle-Name] 同人誌タイトル (C103).cbz",
    "Sample_Manga_Volume_10_END.zip",
    "01_プロトタイプ版_ネーム資料.pdf",
    "[作者] マンガタイトル 第01巻.cbz",
    "[作者] マンガタイトル 第02巻.cbz",
    "Adventure_Quest_Ch_123.zip",
    "週刊少年ダミー 2024年12号.pdf",
    "【新連載】異世界から来たエンジニアの日常_01.cbr",
    "My_Sketchbook_2023_Archive.zip",
    "テスト用コミックデータ_カラー版.pdf",
    "[Circle-Name] 同人誌タイトル (C103).cbz",
    "Sample_Manga_Volume_10_END.zip",
    "01_プロトタイプ版_ネーム資料.pdf",
)
val FilePaths = listOf(
    "Manga/Ongoing/Action/Shonen/Weekly/SeriesTitle/Volumes/[作者] マンガタイトル 第01巻.cbz",
    "Manga/Ongoing/Action/Shonen/Weekly/SeriesTitle/Volumes/[作者] マンガタイトル 第02巻.cbz",
    "Library/Graphic_Novels/Fantasy/Adventure_Quest/Season_03/Chapter_123/Adventure_Quest_Ch_123.zip",
    "Magazines/Weekly_Shonen_Dummy/Back_Issues/2024/December/Volumes/週刊少年ダミー 2024年12号.pdf",
    "Shared_Folder/New_Arrivals/Isekai_Genre/Engineer_Daily_Life/Serialized/【新連載】異世界から来たエンジニアの日常_01.cbr",
    "Archive/Personal_Projects/Digital_Scans/2023_Archive/Sketchbooks/High_Res/My_Sketchbook_2023_Archive.zip",
    "Books/Reference/Art_Books/Color_Collections/Limited_Edition/Drafts/テスト用コミックデータ_カラー版.pdf",
    "Doujin/Events/Comiket/C103/Circle_Name/Titles/New_Releases/[Circle-Name] 同人誌タイトル (C103).cbz",
    "Downloads/Completed_Series/Completed_Manga_A_to_Z/Sample_Series_Name/Final_Volumes/Sample_Manga_Volume_10_END.zip",
    "Temp/Draft_Folders/Project_X/Initial_Prototypes/Name_Drawings/Final_Review/01_プロトタイプ版_ネーム資料.pdf",
    "Manga/Ongoing/Action/Shonen/Weekly/SeriesTitle/Volumes/[作者] マンガタイトル 第01巻.cbz",
    "Manga/Ongoing/Action/Shonen/Weekly/SeriesTitle/Volumes/[作者] マンガタイトル 第02巻.cbz",
    "Library/Graphic_Novels/Fantasy/Adventure_Quest/Season_03/Chapter_123/Adventure_Quest_Ch_123.zip",
    "Magazines/Weekly_Shonen_Dummy/Back_Issues/2024/December/Volumes/週刊少年ダミー 2024年12号.pdf",
    "Shared_Folder/New_Arrivals/Isekai_Genre/Engineer_Daily_Life/Serialized/【新連載】異世界から来たエンジニアの日常_01.cbr",
    "Archive/Personal_Projects/Digital_Scans/2023_Archive/Sketchbooks/High_Res/My_Sketchbook_2023_Archive.zip",
    "Books/Reference/Art_Books/Color_Collections/Limited_Edition/Drafts/テスト用コミックデータ_カラー版.pdf",
    "Doujin/Events/Comiket/C103/Circle_Name/Titles/New_Releases/[Circle-Name] 同人誌タイトル (C103).cbz",
    "Downloads/Completed_Series/Completed_Manga_A_to_Z/Sample_Series_Name/Final_Volumes/Sample_Manga_Volume_10_END.zip",
    "Temp/Draft_Folders/Project_X/Initial_Prototypes/Name_Drawings/Final_Review/01_プロトタイプ版_ネーム資料.pdf",
)
