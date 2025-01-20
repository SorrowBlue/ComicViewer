package com.sorrowblue.comicviewer.feature.favorite.add.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.favorite.add.generated.resources.Res
import comicviewer.feature.favorite.add.generated.resources.favorite_add_btn_add
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FavoriteAddButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, modifier = modifier) {
        Icon(imageVector = ComicIcons.Add, contentDescription = null)
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = stringResource(Res.string.favorite_add_btn_add))
    }
}
