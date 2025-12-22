package com.sorrowblue.comicviewer.app

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class ComposeNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity

    @Test
    fun myTest() {
        // Start the app
        composeTestRule.setContent {
//            MaterialTheme {
//                Text("Hello World!", modifier = Modifier.testTag("test"))
//            }
        }
        Thread.sleep(5000)
//        composeTestRule.onNodeWithTag("test").assertIsDisplayed()
    }
}
