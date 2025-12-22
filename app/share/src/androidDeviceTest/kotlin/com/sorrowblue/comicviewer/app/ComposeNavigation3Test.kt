package com.sorrowblue.comicviewer.app

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.test.platform.app.InstrumentationRegistry
import com.sorrowblue.comicviewer.ComicViewerUI
import com.sorrowblue.comicviewer.domain.model.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.SortUtil
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.framework.common.getPlatformGraph
import com.sorrowblue.comicviewer.rememberComicViewerUIContext
import com.sorrowblue.comicviewer.rememberComicViewerUIState
import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ComposeNavigation3Test {

    @get:Rule
    val composeTestRule = createComposeRule()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity

    val graph by lazy {
        createGraphFactory<Navigation3TestGraph.Factory>().create(
            InstrumentationRegistry.getInstrumentation().context,
            object : LicenseeHelper {
                override suspend fun loadLibraries(): ByteArray {
                    TODO("Not yet implemented")
                }
            })
    }

    var bookshelfId: BookshelfId? = null

    @OptIn(InternalDataApi::class)
    @Before
    fun setup() {
        runTest {
            var folder: Folder? = null
            graph.bookshelfLocalDataSource.updateOrCreate(
                InternalStorage("Navigation3Test"),
            ) {
                bookshelfId = it.id
                folder = Folder(it.id, "name", "", "/test/", 0, 0, false)
                graph.fileLocalDataSource.addUpdate(folder)
            }
            val files = SortUtil.sortedIndex(
                List(4) {
                    BookFile(bookshelfId!!, "name$it", "/test/", "/test/name$it", 0, 0, false)
                }
            )
            graph.fileLocalDataSource.updateHistory(folder!!, files)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun myTest() {
        val graph = createGraphFactory<AppGraph.Factory>().createAppGraph(
            InstrumentationRegistry.getInstrumentation().context,
            object : LicenseeHelper {
                override suspend fun loadLibraries(): ByteArray {
                    TODO("Not yet implemented")
                }
            })
        getPlatformGraph = { graph }
        // Start the app
        composeTestRule.setContent {
            with(InstrumentationRegistry.getInstrumentation().context) {
                val state = with(rememberComicViewerUIContext()) {
                    rememberComicViewerUIState(allowNavigationRestored = false)
                }
                with(getPlatformGraph() as AppGraph) {
                    ComicViewerUI(finishApp = {}, state = state)
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("TutorialScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("NextButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("NextButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("NextButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("NextButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("BookshelfScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()

        // Collection
        composeTestRule.onAllNodesWithTag("NavigationSuiteItem")[1].performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()

        // Readlater
        composeTestRule.onAllNodesWithTag("NavigationSuiteItem")[2].performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("ReadLaterScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()

        // History
        composeTestRule.onAllNodesWithTag("NavigationSuiteItem")[3].performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("HistoryScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()

        // Bookshelf
        composeTestRule.onAllNodesWithTag("NavigationSuiteItem")[0].performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("BookshelfScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()


        // Settings
        composeTestRule.onNodeWithTag("SettingsButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("SettingsScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()

        checkSettings("DisplaySettings", "DisplaySettingsRoot")
        checkSettings("FolderSettings", "FolderSettingsRoot")
        checkSettings("ViewerSettings", "ViewerSettingsRoot")
        checkSettings("SecuritySettings", "SecuritySettingsRoot")
        checkSettings("InfoSettings", "InfoSettingsRoot")
        checkSettings("ImageCacheSettings", "ImageCacheSettingsRoot")
        checkSettings("PluginSettings", "PluginSettingsRoot")

        composeTestRule.onNodeWithTag("CloseButton").performClick()
        composeTestRule.waitForIdle()
        navigationCollection()

//        composeTestRule.onNodeWithTag("BookshelfListItem-${bookshelfId!!.value}")
//            .performTouchInput {
//                click(Offset(10f, 10f))
//            }
//        composeTestRule.onNodeWithTag("FolderScreenRoot").assertIsDisplayed()
//        composeTestRule.waitForIdle()
//        composeTestRule.onNodeWithTag("AppBarMenu").performClick()
//        composeTestRule.waitForIdle()
    }

    private fun checkSettings(itemTestTag: String, screenTestTag: String) {
        composeTestRule.onNodeWithTag(itemTestTag).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(screenTestTag).assertIsDisplayed()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("BackButton").performClick()
        composeTestRule.waitForIdle()
    }

    @OptIn(ExperimentalTestApi::class)
    private fun navigationCollection() {
        composeTestRule.onAllNodesWithTag("NavigationSuiteItem")[1].performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()

        // Basic collection create
        composeTestRule.onNodeWithTag("FloatingActionButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("BasicCollectionCreateButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("BasicCollectionCreateScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CollectionNameField").requestFocus()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CollectionNameField").performTextInput("TestCollectionName")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CollectionNameField").performKeyInput {
            pressKey(Key.Enter, 1000)
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CreateButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()

        // Collection
        composeTestRule.onAllNodesWithTag("CollectionListItem").onFirst().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CollectionScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()

        // Basic collection edit
        composeTestRule.onNodeWithTag("EditButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("BasicCollectionEditScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CloseButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CollectionScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()

        // Basic collection delete
        composeTestRule.onNodeWithTag("DeleteButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("DeleteCollectionScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("ConfirmButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()

        // Smart collection create
        composeTestRule.onNodeWithTag("FloatingActionButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("SmartCollectionCreateButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("SmartCollectionCreateScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CollectionNameField").performTextInput("TestCollectionName")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("QueryField").performTextInput("Search keyword")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("QueryField").performKeyInput {
            pressKey(Key.Enter, 1000)
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CreateButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()

        // Collection
        composeTestRule.onAllNodesWithTag("CollectionListItem").onFirst().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CollectionScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()

        // Smart collection edit
        composeTestRule.onNodeWithTag("EditButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("SmartCollectionEditScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CloseButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CollectionScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()

        // Smart collection delete
        composeTestRule.onNodeWithTag("DeleteButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("DeleteCollectionScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("ConfirmButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()
        composeTestRule.waitForIdle()

    }
}
