package com.base.mvvm

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.util.SparseArray
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.util.contains
import androidx.core.util.set
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.base.util.L
import com.base.util.PermissionUtil
import com.base.util.ifGranted

/**
 * Base class for Activity.
 * Guide to app architecture: https://developer.android.com/jetpack/docs/guide
 */
abstract class BaseActivity : AppCompatActivity() {

    private val connectionLiveData by lazy { ConnectionLiveData(getAppContext()) }

    abstract fun getAppContext(): Context

    /**
     * Specify nav controller.
     * https://developer.android.com/guide/navigation/navigation-getting-started
     */
    abstract fun navController(): NavController?

    fun setTransparentStatusBar() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        connectionLiveData.observe(this, Observer {
            onNetworkChanged(it)
        })
    }

    /**
     * Listen for network change event.
     * @param isConnected: true if connected, false otherwise.
     */
    open fun onNetworkChanged(isConnected: Boolean) {
    }

    private val permissionMap = SparseArray<(Boolean) -> Unit>()
    /**
     * Use this method to request a permission.
     * @param permission: Manifest.permission.
     * @param onPermissionResult : callback when request permission is done.
     */
    fun requestPermission(permission: String, onPermissionResult: (granted: Boolean) -> Unit) {
        if (this.ifGranted(permission)) {
            onPermissionResult(true)
            return
        } else {
            val requestCode = PermissionUtil.getRequestCode(permission)
            permissionMap[requestCode] = onPermissionResult

            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    /**
     * Use this method to request a permission.
     * @param permissions: Multiple Manifest.permission.
     * @param onPermissionsResult : callback when request permission is done.
     */
    fun requestMultiPermissions(
        vararg permissions: String,
        onPermissionsResult: (allGranted: Boolean) -> Unit
    ) {
        val requestCode = PermissionUtil.MULTI_PERMISSION_REQUEST_CODE
        ActivityCompat.requestPermissions(this, permissions, requestCode)
        permissionMap[requestCode] = onPermissionsResult
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        L.d("requestCode: $requestCode permissions: $permissions grantResults: $grantResults")
        if (!permissionMap.contains(requestCode)) return

        //Multiple permissions
        if (requestCode == PermissionUtil.MULTI_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                var allAreGranted = true
                grantResults.forEach {
                    if (it != PackageManager.PERMISSION_GRANTED) {
                        allAreGranted = false
                        return@forEach
                    }
                }
                permissionMap[requestCode]!!.invoke(allAreGranted)
            } else {
                // all permissions denied
                permissionMap[requestCode]!!.invoke(false)
            }
            permissionMap.remove(requestCode)
            return
        }


        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            // permission was granted
            permissionMap[requestCode]!!.invoke(true)
        } else {
            // permission denied
            permissionMap[requestCode]!!.invoke(false)
        }
        permissionMap.remove(requestCode)

    }


    override fun onBackPressed() {
        if (navController() != null) {
            val canPop = navController()!!.popBackStack()
            if (!canPop) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    fun isPortrait(): Boolean =
        resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    fun isLandscape(): Boolean =
        resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

}