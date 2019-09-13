package com.base.mvvm

import android.app.Application
import android.content.Context
import androidx.lifecycle.*


/**
 * Base class for ViewModel. Support lifecycle aware.
 * Guide to app architecture: https://developer.android.com/jetpack/docs/guide
 */
abstract class BaseViewModel(private val app: Application) : AndroidViewModel(app),    LifecycleObserver {
    val viewEvent = LiveEvent<Event>()

    val context: Context = app.applicationContext     //Use this to get application context from ViewModel

    val loading = MutableLiveData<Boolean>()

    fun isLoading(isLoading: Boolean = true) {
        loading.value = isLoading
    }


    /**
     * Use this method to fire event to View (Activity/Fragment)
     */
    fun sendEvent(event: Event) {
        viewEvent.postValue(event)
    }




//-----------------------
// Lifecycle aware methods. Override if needed.
// https://developer.android.com/topic/libraries/architecture/lifecycle
//-----------------------


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onFragmentCreated() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onFragmentStarted() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onFragmentResumed() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onFragmentPaused() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onFragmentStopped() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onFragmentDestroyed() {

    }


}