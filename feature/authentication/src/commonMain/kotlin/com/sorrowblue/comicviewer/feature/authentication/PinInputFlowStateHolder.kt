package com.sorrowblue.comicviewer.feature.authentication

/**
 * Manages PIN input flow state transitions.
 * Handles the progression through different stages of PIN entry
 * (input, confirmation, verification) and temporary PIN storage.
 */
internal class PinInputFlowStateHolder {
    private var temporaryPin: String = ""

    /**
     * Stores the PIN for later confirmation.
     */
    fun storeTemporaryPin(pin: String) {
        temporaryPin = pin
    }

    /**
     * Verifies if the provided PIN matches the stored temporary PIN.
     */
    fun verifyTemporaryPin(pin: String): Boolean {
        return pin == temporaryPin
    }

    /**
     * Clears the temporarily stored PIN.
     */
    fun clearTemporaryPin() {
        temporaryPin = ""
    }

    /**
     * Gets the stored temporary PIN.
     */
    fun getTemporaryPin(): String = temporaryPin
}
