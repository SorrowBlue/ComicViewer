package com.sorrowblue.comicviewer.multi

@Parcelize
data class RestoableItem(val count: Int, val name: String) : CommonParcelable

// For Android @Parcelize
annotation class Parcelize

// For Android Parcelable
expect interface CommonParcelable
