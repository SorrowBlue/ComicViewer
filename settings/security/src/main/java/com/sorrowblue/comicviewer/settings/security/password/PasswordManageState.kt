package com.sorrowblue.comicviewer.settings.security.password

import com.sorrowblue.comicviewer.settings.security.R

enum class PasswordManageState(val titleRes: Int, val hintRes: Int) {
    NEW(
        R.string.settings_security_password_manage_dialog_title_password_setting,
        R.string.settings_security_password_manage_dialog_hint_new_password
    ),
    CHANGE(
        R.string.settings_security_password_manage_dialog_title_change_password,
        R.string.settings_security_password_manage_dialog_hint_new_password
    ),
    DELETE(
        R.string.settings_security_password_manage_dialog_title_delete_password,
        R.string.settings_security_password_manage_dialog_hint_old_password
    )
}
