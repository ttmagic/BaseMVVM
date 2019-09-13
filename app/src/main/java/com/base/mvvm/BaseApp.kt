package com.base.mvvm

import android.app.Application
import com.base.util.Pref


/**
 * Base class for Application.
 * Guide to app architecture: https://developer.android.com/jetpack/docs/guide
 * Using: ViewModel, Data binding, Jetpack Navigation, Kotlin coroutines.
 */
abstract class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Pref.init(applicationContext, applicationId())
    }


    /**
     * Specify application id for init shared preferences.
     */
    abstract fun applicationId(): String

}