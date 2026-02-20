package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import kotlin.test.assertEquals

actual fun checkSeekableInputStream(seekableInputStream: SeekableInputStream) {
    assertEquals(seekableInputStream.position(), 0, "Position should be 0")
    assertEquals(seekableInputStream.seek(1, SeekableInputStream.SEEK_SET), 1, "Seek should be 10")
    val array = ByteArray(4)
    assertEquals(seekableInputStream.read(array), 4, "Read should be 4")
    assertEquals(seekableInputStream.position(), 5, "Position should be 5")
}
