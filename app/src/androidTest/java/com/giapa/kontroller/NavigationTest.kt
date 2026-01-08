package com.giapa.kontroller

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun connection_to_controls_navigation() {
        composeRule.onNodeWithText("KOntroller").assertIsDisplayed()
        composeRule.onNodeWithText("Address").performTextInput("192.168.1.2:1234")
        composeRule.onNodeWithText("Connect").performClick()

        composeRule.onNodeWithText("Controls").assertIsDisplayed()
        composeRule.onNodeWithText("Mic").assertIsDisplayed()
    }
}

