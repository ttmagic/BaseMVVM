package com.base.util

import android.Manifest
import android.os.Build


object PermissionUtil {

    const val MULTI_PERMISSION_REQUEST_CODE = 6969

    /**
     * Get permission request code by permission.
     * @param permission: Manifest.permission.
     */
    fun getRequestCode(permission: String): Int {
        return if (dangerousPermissions.contains(permission)) {
            dangerousPermissions.indexOf(permission)
        } else {
            999
        }
    }

    /**
     * Dangerous Permissions that needs to request at runtime.
     * https://developer.android.com/reference/android/Manifest.permission
     */
    private val dangerousPermissions = arrayListOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ADD_VOICEMAIL,
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.CAMERA,
        Manifest.permission.GET_ACCOUNTS,
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_MMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.RECEIVE_WAP_PUSH,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.SEND_SMS,
        Manifest.permission.USE_SIP,
        Manifest.permission.WRITE_CALENDAR,
        Manifest.permission.WRITE_CALL_LOG,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ).apply {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                add(Manifest.permission.READ_PHONE_NUMBERS)
                add(Manifest.permission.ANSWER_PHONE_CALLS)
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                add(Manifest.permission.ACCEPT_HANDOVER)
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                add(Manifest.permission.ACCESS_MEDIA_LOCATION)
                add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }
}