package com.base.util

import android.Manifest


object PermissionUtil {

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
        Manifest.permission.ACCEPT_HANDOVER,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_MEDIA_LOCATION,
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.ADD_VOICEMAIL,
        Manifest.permission.ANSWER_PHONE_CALLS,
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.CAMERA,
        Manifest.permission.GET_ACCOUNTS,
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_NUMBERS,
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
    )
}