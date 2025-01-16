package com.sorrowblue.comicviewer.data.datastore.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named

@Module
@ComponentScan("com.sorrowblue.comicviewer.data.datastore")
class DataDatastoreModule

@Named
annotation class Book

@Named
annotation class Display

@Named
annotation class FolderDisplay

@Named
annotation class Folder

@Named
annotation class Security

@Named
annotation class Settings

@Named
annotation class ViewerOperation

@Named
annotation class Viewer

