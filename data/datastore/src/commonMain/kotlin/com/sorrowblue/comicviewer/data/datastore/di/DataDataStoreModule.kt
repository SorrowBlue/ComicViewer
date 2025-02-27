package com.sorrowblue.comicviewer.data.datastore.di

import com.sorrowblue.comicviewer.data.datastore.DataStoreMaker
import com.sorrowblue.comicviewer.data.datastore.qualifier.Book
import com.sorrowblue.comicviewer.data.datastore.qualifier.Display
import com.sorrowblue.comicviewer.data.datastore.qualifier.Folder
import com.sorrowblue.comicviewer.data.datastore.qualifier.FolderDisplay
import com.sorrowblue.comicviewer.data.datastore.qualifier.Security
import com.sorrowblue.comicviewer.data.datastore.qualifier.Settings
import com.sorrowblue.comicviewer.data.datastore.qualifier.Viewer
import com.sorrowblue.comicviewer.data.datastore.qualifier.ViewerOperation
import com.sorrowblue.comicviewer.data.datastore.serializer.BookSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.DisplaySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.FolderDisplaySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.FolderSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.SecuritySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.SettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.ViewerOperationSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.ViewerSettingsSerializer
import org.koin.core.annotation.Singleton

@Singleton
@Book
internal fun bookSettingsDataStore(dataStoreMaker: DataStoreMaker) =
    dataStoreMaker.createDataStore(BookSettingsSerializer)

@Singleton
@Display
internal fun displaySettingsDataStore(dataStoreMaker: DataStoreMaker) =
    dataStoreMaker.createDataStore(DisplaySettingsSerializer)

@Singleton
@FolderDisplay
internal fun folderDisplaySettingsDataStore(dataStoreMaker: DataStoreMaker) =
    dataStoreMaker.createDataStore(FolderDisplaySettingsSerializer)

@Singleton
@Folder
internal fun folderSettingsDataStore(dataStoreMaker: DataStoreMaker) =
    dataStoreMaker.createDataStore(FolderSettingsSerializer)

@Singleton
@Security
internal fun securitySettingsDataStore(dataStoreMaker: DataStoreMaker) =
    dataStoreMaker.createDataStore(SecuritySettingsSerializer)

@Singleton
@Settings
internal fun settingsDataStore(dataStoreMaker: DataStoreMaker) =
    dataStoreMaker.createDataStore(SettingsSerializer)

@Singleton
@ViewerOperation
internal fun viewerOperationSettingsDataStore(dataStoreMaker: DataStoreMaker) =
    dataStoreMaker.createDataStore(ViewerOperationSettingsSerializer)

@Singleton
@Viewer
internal fun viewerSettingsDataStore(dataStoreMaker: DataStoreMaker) =
    dataStoreMaker.createDataStore(ViewerSettingsSerializer)
