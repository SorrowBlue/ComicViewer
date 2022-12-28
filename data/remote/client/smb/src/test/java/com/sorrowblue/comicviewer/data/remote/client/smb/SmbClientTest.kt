package com.sorrowblue.comicviewer.data.remote.client.smb

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.common.SmbServerModel
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class SmbClientTest {

    @Test
    fun testEncode() {
        val str = "/douzin/成年コミック/[ヤマダユウヤ] 日陰の花 + イラストカード.zip"
        assertEquals(Uri.encode(str), URLEncoder.encode(str, "UTF-8").replace("+", "%20"))
    }

    @Test
    fun testUriDecode() {
        val str = "/douzin/1234567890abcdefghijklmnopqrstuvwxyz     !#\$%&'()=~`{+}_-^@[;],..zip"
        val javaUri = URI("smb", serverModel.host, str, null)
        val androidUri = Uri.Builder()
            .scheme("smb")
            .authority(serverModel.host)
            .path(str).build()
        println("result=" + javaUri.decode())
        assertEquals(javaUri.decode(), Uri.decode(androidUri.toString()))
    }

    private fun URI.decode() = URLDecoder.decode(toString().replace("+", "%2B"), "UTF-8")

    val serverModel = SmbServerModel(
        ServerModelId(0),
        "Test",
        "SORROWBLUE-DESK",
        "445",
        SmbServerModel.UsernamePassword("sorrowblue.sb@outlook.jp", "outyuukiasuna2s2")
    )

    val nas = SmbServerModel(
        ServerModelId(0),
        "Test",
        "192.168.0.101",
        "445",
        SmbServerModel.UsernamePassword("SorrowBlue", "nasyuukiasuna2s2")
    )

    val client = SmbFileClient(serverModel)
    val nasClient = SmbFileClient(nas)

    @Test
    fun jcifsTest() = runTest {
        val fileModel =
            FileModel.Folder("/Users/sorro/Downloads/", serverModel.id, "", "", 0, 0, 0)
        measureTimeMillis {
            client.listFiles(fileModel)
        }.also {
            println("win client.listFiles. ${it}ms")
        }
    }

    @Test
    fun jcifsNasTest() = runTest {
        val fileModel = FileModel.Folder("/douzin/", serverModel.id, "", "", 0, 0, 0)
        measureTimeMillis {
            nasClient.listFiles(fileModel)
        }.also {
            println("nas client.listFiles. ${it}ms")
        }
    }
}
