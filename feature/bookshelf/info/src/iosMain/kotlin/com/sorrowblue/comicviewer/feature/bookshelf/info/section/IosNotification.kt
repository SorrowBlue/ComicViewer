package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import kotlin.coroutines.suspendCoroutine
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionCarPlay
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatus
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
import platform.UserNotifications.UNAuthorizationStatusEphemeral
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNNotificationSettings
import platform.UserNotifications.UNUserNotificationCenter

enum class PermissionState {
    /**
     * Starting state for each permission.
     */
    NotDetermined,

    Granted,

    /**
     * On Android only applicable to Push Notifications.
     */
    DeniedAlways,
}

internal class NotificationPermissionDelegate {
    suspend fun providePermission(): PermissionState =
        provideNotificationPermission(getPermissionStatus())

    suspend fun getPermissionState(): PermissionState =
        when (val status: UNAuthorizationStatus = getPermissionStatus()) {
            UNAuthorizationStatusAuthorized,
            UNAuthorizationStatusProvisional,
            UNAuthorizationStatusEphemeral,
            -> PermissionState.Granted

            UNAuthorizationStatusNotDetermined -> PermissionState.NotDetermined

            UNAuthorizationStatusDenied -> PermissionState.DeniedAlways

            else -> error("unknown push authorization status $status")
        }

    private suspend fun getPermissionStatus(): UNAuthorizationStatus {
        val currentCenter = UNUserNotificationCenter.currentNotificationCenter()
        return suspendCoroutine { continuation ->
            currentCenter.getNotificationSettingsWithCompletionHandler(
                mainContinuation { settings: UNNotificationSettings? ->
                    continuation.resumeWith(
                        Result.success(
                            settings?.authorizationStatus ?: UNAuthorizationStatusNotDetermined,
                        ),
                    )
                },
            )
        }
    }

    private suspend fun provideNotificationPermission(
        status: UNAuthorizationStatus,
    ): PermissionState {
        when (status) {
            UNAuthorizationStatusAuthorized, // 許可
            UNAuthorizationStatusProvisional, // お試し
            UNAuthorizationStatusEphemeral, // 特定の期間だけ許可
            -> return PermissionState.Granted

            // 未確認
            UNAuthorizationStatusNotDetermined -> {
                // User has not yet chosen permission, request permission
                val newStatus = suspendCoroutine { continuation ->
                    UNUserNotificationCenter.currentNotificationCenter()
                        .requestAuthorizationWithOptions(
                            UNAuthorizationOptionSound
                                .or(UNAuthorizationOptionAlert)
                                .or(UNAuthorizationOptionBadge)
                                .or(UNAuthorizationOptionCarPlay),
                            mainContinuation { granted, error ->
                                if (granted && error == null) {
                                    continuation.resumeWith(
                                        Result.success(
                                            UNAuthorizationStatusAuthorized,
                                        ),
                                    )
                                } else {
                                    continuation.resumeWith(
                                        Result.success(
                                            UNAuthorizationStatusDenied,
                                        ),
                                    )
                                }
                            },
                        )
                }
                return provideNotificationPermission(newStatus)
            }

            UNAuthorizationStatusDenied -> return PermissionState.DeniedAlways

            else -> error("unknown notifications authorization status $status")
        }
    }
}
