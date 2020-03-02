package com.base.mvvm

import android.app.Application
import com.base.util.Logger
import com.base.util.Pref


/**
 * Base class for Application.
 * Guide to app architecture: https://developer.android.com/jetpack/docs/guide
 * Using: ViewModel, Data binding, Jetpack Navigation, Kotlin coroutines.
 */
abstract class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Pref.init(applicationContext, appId())
        Logger.setDebugging(isDebug())
    }


    /**
     * Specify application id for init shared preferences.
     */
    abstract fun appId(): String

    /**
     * Set debugging for the whole application.
     */
    abstract fun isDebug():Boolean

}