package com.sorrowblue.comicviewer.app

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.test.platform.app.InstrumentationRegistry
import com.sorrowblue.comicviewer.ComicViewerUI
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.framework.common.getPlatformGraph
import com.sorrowblue.comicviewer.rememberComicViewerUIContext
import com.sorrowblue.comicviewer.rememberComicViewerUIState
import org.junit.Rule
import org.junit.Test

class ComposeNavigation3Test {
    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tabTest() {
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

        tutorial()

        tab()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun bookshelfTest() {
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
        tutorial()

        bookshelf()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun collectionTest() {
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

        tutorial()

        navigationCollection()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun settingsTest() {
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

        tutorial()

        settings()
    }

    private fun tutorial() {
        if (composeTestRule.onNodeWithTag("TutorialScreen").isNotDisplayed()) return
        composeTestRule.waitForIdle()
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
    }

    private fun tab() {
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
    }

    private fun settings() {
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
        composeTestRule.onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()

        // Basic collection create
        composeTestRule.onNodeWithTag("FloatingActionButton").performClick()
        composeTestRule.onNodeWithTag("BasicCollectionCreateButton").performClick()
        composeTestRule.onNodeWithTag("BasicCollectionCreateScreenRoot").assertIsDisplayed()
        composeTestRule.onNodeWithTag("CollectionNameField").requestFocus()
        composeTestRule.onNodeWithTag("CollectionNameField").performTextInput("TestCollectionName")
        composeTestRule.onNodeWithTag("CollectionNameField").performKeyInput {
            pressKey(Key.Enter, 1000)
        }
        composeTestRule.onNodeWithTag("CreateButton").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule.onNodeWithTag("BasicCollectionCreateScreenRoot").isNotDisplayed()
        }
        composeTestRule.onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()

        // Collection
        composeTestRule.onAllNodesWithTag("CollectionListItem").onFirst().performClick()
        composeTestRule.onNodeWithTag("CollectionScreenRoot").assertIsDisplayed()

        // Basic collection edit
        composeTestRule.onNodeWithTag("EditButton").performClick()
        composeTestRule.onNodeWithTag("BasicCollectionEditScreenRoot").assertIsDisplayed()
        composeTestRule.onNodeWithTag("CloseButton").performClick()
        composeTestRule.onNodeWithTag("CollectionScreenRoot").assertIsDisplayed()

        // Basic collection delete
        composeTestRule.onNodeWithTag("DeleteButton").performClick()
        composeTestRule.onNodeWithTag("DeleteCollectionScreenRoot").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ConfirmButton").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule.onNodeWithTag("DeleteCollectionScreenRoot").isNotDisplayed()
        }
        composeTestRule.onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()

        // Smart collection create
        composeTestRule.onNodeWithTag("FloatingActionButton").performClick()
        composeTestRule.onNodeWithTag("SmartCollectionCreateButton").performClick()
        composeTestRule.onNodeWithTag("SmartCollectionCreateScreenRoot").assertIsDisplayed()
        composeTestRule.onNodeWithTag("CollectionNameField").requestFocus()
        composeTestRule.onNodeWithTag("CollectionNameField").performTextInput("TestCollectionName")
        composeTestRule.onNodeWithTag("QueryField").performTextInput("Search keyword")
        composeTestRule.onNodeWithTag("QueryField").performKeyInput {
            pressKey(Key.Enter, 1000)
        }
        composeTestRule.onNodeWithTag("CreateButton").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule.onNodeWithTag("SmartCollectionCreateScreenRoot").isNotDisplayed()
        }
        composeTestRule.onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()

        // Collection
        composeTestRule.onAllNodesWithTag("CollectionListItem").onFirst().performClick()
        composeTestRule.onNodeWithTag("CollectionScreenRoot").assertIsDisplayed()

        // Smart collection edit
        composeTestRule.onNodeWithTag("EditButton").performClick()
        composeTestRule.onNodeWithTag("SmartCollectionEditScreenRoot").assertIsDisplayed()
        composeTestRule.onNodeWithTag("CloseButton").performClick()
        composeTestRule.onNodeWithTag("CollectionScreenRoot").assertIsDisplayed()

        // Smart collection delete
        composeTestRule.onNodeWithTag("DeleteButton").performClick()
        composeTestRule.onNodeWithTag("DeleteCollectionScreenRoot").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ConfirmButton").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule.onNodeWithTag("DeleteCollectionScreenRoot").isNotDisplayed()
        }
        composeTestRule.onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()
    }

    private fun bookshelf() {
        composeTestRule.onAllNodesWithTag("NavigationSuiteItem")[0].performClick()
        composeTestRule.onNodeWithTag("BookshelfScreenRoot").assertIsDisplayed()

        composeTestRule.onNodeWithTag("BookshelfFab").performClick()
        composeTestRule.onNodeWithTag("BookshelfSelectionScreenRoot").assertIsDisplayed()

        composeTestRule.onNodeWithTag("BackButton").performClick()
        composeTestRule.onNodeWithTag("BookshelfScreenRoot").assertIsDisplayed()

        composeTestRule.onNodeWithTag("BookshelfFab").performClick()
        composeTestRule.onNodeWithTag("BookshelfSelectionScreenRoot").assertIsDisplayed()

        composeTestRule.onNodeWithTag("BookshelfSelectionItem-${BookshelfType.SMB}").performClick()
        composeTestRule.onNodeWithTag("BookshelfEditScreenRoot").assertIsDisplayed()

        composeTestRule.onNodeWithTag("CloseButton").performClick()
        composeTestRule.onNodeWithTag("BookshelfSelectionScreenRoot").assertIsDisplayed()

        composeTestRule.onNodeWithTag("BookshelfSelectionItem-${BookshelfType.DEVICE}")
            .performClick()
        composeTestRule.onNodeWithTag("BookshelfEditScreenRoot").assertIsDisplayed()

        composeTestRule.onNodeWithTag("CloseButton").performClick()
        composeTestRule.onNodeWithTag("BookshelfSelectionScreenRoot").assertIsDisplayed()

        composeTestRule.onNodeWithTag("BookshelfSelectionItem-${BookshelfType.SMB}").performClick()
        composeTestRule.onNodeWithTag("BookshelfEditScreenRoot").assertIsDisplayed()

        composeTestRule.onNodeWithTag("DisplayNameField").performTextInput("SMBBookshelf")
        composeTestRule.onNodeWithTag("HostField").performTextInput(BuildTestConfig.smbHost)
        composeTestRule.onNodeWithTag("PortField").performTextClearance()
        composeTestRule.onNodeWithTag("PortField")
            .performTextInput(BuildTestConfig.smbPort.toString())
        composeTestRule.onNodeWithTag("PathField").performTextInput(BuildTestConfig.smbPath)
        composeTestRule.onNodeWithTag("AuthUserPass").performClick()
        composeTestRule.onNodeWithTag("DomainField").performTextInput(BuildTestConfig.smbDomain)
        composeTestRule.onNodeWithTag("UsernameField").performTextInput(BuildTestConfig.smbUsername)
        composeTestRule.onNodeWithTag("PasswordField").performTextInput(BuildTestConfig.smbPassword)
        composeTestRule.onNodeWithTag("SaveButton").performClick()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onNodeWithTag("BookshelfEditScreenRoot").isNotDisplayed()
        }
        composeTestRule.onNodeWithTag("BookshelfScreenRoot").assertIsDisplayed()

        composeTestRule.onAllNodesWithTag("BookshelfListItemMenu").onFirst().performClick()
        composeTestRule.onNodeWithTag("BookshelfInfoScreenRoot").assertIsDisplayed()
        composeTestRule.onNodeWithTag("EditButton").performClick()
        composeTestRule.onNodeWithTag("BookshelfEditScreenRoot").assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("CloseButton").onLast().performClick()

        composeTestRule.onNodeWithTag("DeleteButton").performClick()
        composeTestRule.onNodeWithTag("BookshelfDeleteScreenRoot").assertIsDisplayed()
        composeTestRule.onNodeWithTag("DismissButton").performClick()
        composeTestRule.onNodeWithTag("BookshelfInfoScreenRoot").assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("CloseButton").onLast().performClick()
        composeTestRule.onNodeWithTag("BookshelfScreenRoot").assertIsDisplayed()

        composeTestRule.onAllNodesWithTag("BookshelfListItem").onFirst()
            .performTouchInput {
                click(Offset(centerX, 10f))
            }
        composeTestRule.onNodeWithTag("FolderScreenRoot").assertIsDisplayed()

        composeTestRule.onAllNodesWithTag("FileListItemMenu").onFirst().performClick()
        composeTestRule.onNodeWithTag("FileInfoScreenRoot").assertIsDisplayed()

        composeTestRule.onNodeWithTag("AddCollectionButton").performClick()
        composeTestRule.onNodeWithTag("BasicCollectionAddScreenRoot").assertIsDisplayed()

        composeTestRule.onAllNodesWithTag("CloseButton").onLast().performClick()
        composeTestRule.onNodeWithTag("FileInfoScreenRoot").assertIsDisplayed()

        composeTestRule.onAllNodesWithTag("CloseButton").onLast().performClick()
        composeTestRule.onNodeWithTag("FolderScreenRoot").assertIsDisplayed()

        composeTestRule.onAllNodesWithTag("FileListItem").onFirst().performClick()
        composeTestRule.onNodeWithTag("FolderScreenRoot").assertIsDisplayed()

        composeTestRule.onNodeWithTag("SearchButton").performClick()
        composeTestRule.onNodeWithTag("SearchScreenRoot").assertIsDisplayed()

        composeTestRule.onAllNodesWithTag("FileListItemMenu").onFirst().performClick()
        composeTestRule.onNodeWithTag("FileInfoScreenRoot").assertIsDisplayed()

        composeTestRule.onNodeWithTag("OpenFolderButton").performClick()
        composeTestRule.onNodeWithTag("FolderScreenRoot").assertIsDisplayed()

        composeTestRule.onNodeWithTag("BackButton").performClick()
        composeTestRule.onNodeWithTag("FileInfoScreenRoot").assertIsDisplayed()

        composeTestRule.onAllNodesWithTag("CloseButton").onLast().performClick()
        composeTestRule.onNodeWithTag("SearchScreenRoot").assertIsDisplayed()

        composeTestRule.onNodeWithTag("SmartCollectionButton").performClick()
        composeTestRule.onNodeWithTag("SmartCollectionCreateScreenRoot").assertIsDisplayed()

        composeTestRule.onAllNodesWithTag("CloseButton").onLast().performClick()
        composeTestRule.onNodeWithTag("SearchScreenRoot").assertIsDisplayed()
    }
}
