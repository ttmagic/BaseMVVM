package com.base.mvvm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import java.lang.ref.WeakReference


internal class ConnectionLiveData(context: Context) : LiveData<Boolean>() {

    private val refContext: WeakReference<Context>?

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (isNetworkConnected(context)) {
                postValue(true)
            } else {
                postValue(false)
            }
        }
    }

    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.isConnected
    }

    init {
        refContext = WeakReference(context)
    }

    override fun onActive() {
        refContext?.get()?.let {
            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            it.registerReceiver(networkReceiver, filter)
        }
    }

    override fun onInactive() {
        refContext?.get()?.unregisterReceiver(networkReceiver)
    }

}
