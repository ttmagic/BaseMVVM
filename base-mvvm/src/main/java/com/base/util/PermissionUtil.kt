package com.base.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment


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

/**
 * Open app setting screen. (Intent.ACTION_APPLICATION_DETAILS_SETTINGS).
 * @return true if handled.
 */
fun Context?.openAppSettingScreen(): Boolean {
    if (this == null) return false
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", applicationInfo.packageName, null)
    intent.data = uri
    startActivity(intent)
    return true
}

/**
 * Open app setting screen, from fragment.
 */
fun Fragment?.openAppSettingScreen(): Boolean {
    if (this == null || context == null) return false
    return context.openAppSettingScreen()
}

/**
 * Check if a permission is "Never ask again".
 */
fun Activity?.ifNeverAskedAgain(permission: String, doSth: () -> Unit) {
    if (this == null) return
    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
        doSth.invoke()
    }
}

/**
 * Check if a permission is "Never ask again".
 */
fun Fragment?.ifNeverAskedAgain(permission: String, doSth: () -> Unit) {
    if (this == null || this.activity == null) return
    if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
        doSth.invoke()
    }
}