package com.sorrowblue.comicviewer.domain.service.interactor

import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.usecase.GetPdfPluginStateUseCase
import com.sorrowblue.comicviewer.domain.usecase.PACKAGE_PDF_PLUGIN
import com.sorrowblue.comicviewer.domain.usecase.PdfPluginState
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import logcat.LogPriority
import logcat.logcat

@Inject
internal class GetPdfPluginStateInteractor(private val context: Context) :
    GetPdfPluginStateUseCase() {
    override fun run(request: EmptyRequest): Flow<Resource<PdfPluginState, Resource.SystemError>> {
        return flow<Resource<PdfPluginState, Resource.SystemError>> {
            val versionName = try {
                context.packageManager.getPackageInfo(PACKAGE_PDF_PLUGIN, 0).versionName
            } catch (_: NameNotFoundException) {
                logcat(LogPriority.INFO) { "Plugin app is not installed. $PACKAGE_PDF_PLUGIN" }
                return@flow emit(Resource.Success(PdfPluginState.NotInstalled))
            }
            if (versionName != null) {
                val majorVersion = versionName.split(".").firstOrNull()?.toIntOrNull()
                    ?: return@flow emit(Resource.Success(PdfPluginState.OldVersion))
                if (SupportMajorVersion <= majorVersion) {
                    emit(Resource.Success(PdfPluginState.Enable))
                } else {
                    emit(Resource.Success(PdfPluginState.OldVersion))
                }
            } else {
                emit(Resource.Success(PdfPluginState.OldVersion))
            }
        }.catch {
            emit(Resource.Error(Resource.SystemError(it)))
        }
    }
}

private const val SupportMajorVersion = 0
