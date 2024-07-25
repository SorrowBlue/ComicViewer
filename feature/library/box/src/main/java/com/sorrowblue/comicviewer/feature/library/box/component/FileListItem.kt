package com.sorrowblue.comicviewer.feature.library.box.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.box.data.BoxApiRepository
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import io.ktor.http.HttpHeaders
import io.ktor.http.auth.AuthScheme
import org.koin.compose.koinInject

@OptIn(ExperimentalCoilApi::class)
@Composable
fun FileListItem(file: File, onClick: () -> Unit, modifier: Modifier = Modifier) {

    val repository: BoxApiRepository = koinInject()
    ListItem(
        headlineContent = { Text(text = file.name) },
        trailingContent = {
            Text(text = file.size.toString())
        },
        leadingContent = {
            var token by remember { mutableStateOf("") }
            LaunchedEffect(Unit) {
                token = repository.accessToken()
            }
            if (token.isNotEmpty()) {
                when (file) {
                    is Book -> {
                        val context = LocalContext.current
                        val data = ImageRequest.Builder(context)
                            .data("https://api.box.com/2.0/files/${file.path}/thumbnail.jpg")
                            .httpHeaders(
                                NetworkHeaders.Builder()
                                    .set(HttpHeaders.Authorization, "${AuthScheme.Bearer} $token")
                                    .build()
                            ).build()
                        AsyncImage(
                            model = data,
                            contentDescription = null,
                            Modifier.size(24.dp)
                        )
                    }

                    is Folder -> {
                        Icon(imageVector = ComicIcons.Folder, contentDescription = null)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            ComicTheme.colorScheme.surfaceContainerHigh,
                            shape = CircleShape
                        )
                )
            }
        },
        modifier = modifier.clickable(onClick = onClick)
    )
}
