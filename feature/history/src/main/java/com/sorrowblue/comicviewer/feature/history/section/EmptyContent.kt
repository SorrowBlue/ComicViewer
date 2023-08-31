package com.sorrowblue.comicviewer.feature.history.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.feature.history.R
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme

@Composable
internal fun EmptyContent(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_undraw_resume_folder_re_e0bi),
            contentDescription = null
        )
        Text(
            text = stringResource(id = R.string.history_label_no_history),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview
@Composable
private fun PreviewEmptyContent() {

    AppMaterialTheme {
        Surface {
            EmptyContent()
        }
    }
}
