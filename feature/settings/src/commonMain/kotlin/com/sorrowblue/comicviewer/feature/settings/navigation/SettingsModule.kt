package com.sorrowblue.comicviewer.feature.settings.navigation

import com.sorrowblue.comicviewer.feature.settings.display.di.DisplaySettingsModule
import com.sorrowblue.comicviewer.feature.settings.folder.di.FolderSettingsModule
import com.sorrowblue.comicviewer.feature.settings.info.di.AppInfoSettingsModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [DisplaySettingsModule::class, FolderSettingsModule::class, AppInfoSettingsModule::class])
@ComponentScan("com.sorrowblue.comicviewer.feature.settings")
class SettingsModule
