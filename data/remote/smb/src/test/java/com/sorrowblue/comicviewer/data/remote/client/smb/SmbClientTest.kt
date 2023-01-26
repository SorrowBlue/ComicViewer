package com.sorrowblue.comicviewer.data.remote.client.smb

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.common.SmbServerModel
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import logcat.LogcatLogger
import logcat.PrintLogger
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class SmbClientTest {

    @Before
    fun before() {
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(PrintLogger)
        }
    }

    val pc = SmbServerModel(
        ServerModelId(0),
        "Test",
        "SORROWBLUE-DESK",
        445,
        SmbServerModel.UsernamePassword("", "sorrowblue.sb@outlook.jp", "outyuukiasuna2s2")
    )

    @Test
    fun testEncode() {
        val str = "/douzin/成年コミック/[ヤマダユウヤ] 日陰の花 + イラストカード.zip"
        assertEquals(Uri.encode(str), URLEncoder.encode(str, "UTF-8").replace("+", "%20"))
    }

    @Test
    fun testUriDecode() {
        val str = "/douzin/1234567890abcdefghijklmnopqrstuvwxyz     !#\$%&'()=~`{+}_-^@[;],..zip"
        val javaUri = URI("smb", pc.host, str, null)
        val androidUri = Uri.Builder()
            .scheme("smb")
            .authority(pc.host)
            .path(str).build()
        println("result=" + javaUri.decode())
        assertEquals(javaUri.decode(), Uri.decode(androidUri.toString()))
    }

    private fun URI.decode() = URLDecoder.decode(toString().replace("+", "%2B"), "UTF-8")
}
