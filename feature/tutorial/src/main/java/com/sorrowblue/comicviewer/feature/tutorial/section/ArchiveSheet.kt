package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ArchiveSheet() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = AppMaterialTheme.dimens.margin),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_undraw_file_bundle_re_6q1e),
            contentDescription = null,
            modifier = Modifier.width(200.dp)
        )

        Text(
            text = "Support Archive Compress File",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = "Supports viewing of archived and compressed files. Supported extensions are zip, rar, 7z, etc. Files with passwords are also supported.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.size(16.dp))

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SupportExtension.Archive.entries.forEach {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(text = ".${it.extension}")
                    }
                )
            }
        }
    }
}
