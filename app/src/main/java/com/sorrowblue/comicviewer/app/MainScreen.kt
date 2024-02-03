package com.sorrowblue.comicviewer.app

import android.os.Parcel
import android.os.Parcelable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class MainScreenUiState(
    val isAuthenticating: Boolean = false,
    val currentTab: MainScreenTab? = null,
    val tabs: PersistentList<MainScreenTab> = MainScreenTab.entries.toPersistentList(),
) : Parcelable {

    companion object : Parceler<MainScreenUiState> {
        override fun MainScreenUiState.write(parcel: Parcel, flags: Int) {
            parcel.writeBoolean(isAuthenticating)
            parcel.writeString(currentTab?.name)
            parcel.writeStringList(tabs.map(MainScreenTab::name))
        }

        override fun create(parcel: Parcel) = MainScreenUiState(
            parcel.readBoolean(),
            parcel.readString()?.let(MainScreenTab::valueOf),
            mutableListOf<String>().also(parcel::readStringList)
                .map(MainScreenTab::valueOf).toPersistentList()
        )
    }
}
