package com.sorrowblue.comicviewer.feature.settings.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.LocaleListCompat
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import java.util.Locale
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InAppLanguagePickerTopAppBar(
    onBackClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior?
) {
    TopAppBar(
        title = { Text("アプリの言語") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = ComicIcons.ArrowBack, contentDescription = "Back")
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InAppLanguagePickerScreen(onBackClick: () -> Unit = {}) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val languages = remember { Language.entries.toPersistentList() }
    Scaffold(
        topBar = {
            InAppLanguagePickerTopAppBar(
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        val current = remember {
            AppCompatDelegate.getApplicationLocales().toLanguageTags()
        }
        LazyColumn(
            contentPadding = contentPadding
        ) {
            items(languages) { language ->
                ListItem(
                    trailingContent = {
                        if (current == language.tag) {
                            Icon(imageVector = ComicIcons.Check, contentDescription = null)
                        }
                    },
                    headlineContent = { Text(language.label) },
                    modifier = Modifier.clickable {
                        if (language.tag.isEmpty()) {
                            LocaleListCompat.getEmptyLocaleList()
                        } else {
                            LocaleListCompat.forLanguageTags(language.tag)
                        }.let {
                            AppCompatDelegate.setApplicationLocales(it)
                        }
                    })
            }
        }
    }
}

enum class Language(val label: String, val tag: String) {
    SYSTEM_DEFAUKT("System default", ""),
    JAPANESE("日本語", Locale.JAPANESE.toLanguageTag()),
    ENGLISH_US("English(United States)", Locale.US.toLanguageTag()),
}

@Preview
@Composable
private fun PreviewInAppLanguagePickerScreen() {
    InAppLanguagePickerScreen({})
}
