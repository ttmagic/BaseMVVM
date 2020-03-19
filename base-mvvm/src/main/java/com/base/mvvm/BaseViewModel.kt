package com.base.mvvm

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import com.base.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response


/**
 * Base class for ViewModel. Support lifecycle aware.
 * Guide to app architecture: https://developer.android.com/jetpack/docs/guide
 */
abstract class BaseViewModel(app: Application) : AndroidViewModel(app), LifecycleObserver {
    //Hold arguments From fragment.
    lateinit var args: Bundle

    //Fire single event to corresponding Fragment.
    val viewEvent = LiveEvent<Event>()

    //Use this to get app context from ViewModel
    val context: Context = app.applicationContext

    private val _loading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _loading

    fun setLoading(loading: Boolean = true) {
        _loading.postValue(loading)
    }


    /**
     * Use this method to fire event to View (Activity/Fragment). Listen in onEvent()
     */
    fun sendEvent(event: Event) {
        viewEvent.postValue(event)
    }

    /**
     * Use this method to fire event to View (Activity/Fragment). Listen in onEvent()
     */
    fun sendEvent(event: String, data: Any?) {
        viewEvent.postValue(Event(event, data))
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

    /**
     * Wrapper method for launch coroutines.
     * Support show/hide loading, catch exceptions.
     * Handle exceptions in onCoroutinesExceptions.
     */
    fun coroutines(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch {
            try {
                setLoading(true)
                block()
            } catch (e: Exception) {
                onCoroutinesExceptions(e)
                setLoading(false)
            }
            setLoading(false)
        }
    }

    /**
     * Handle exception when launch coroutines.
     */
    open fun onCoroutinesExceptions(error: Exception) {
        Logger.e(error.message)
    }
}

/**
 * Extension function, Do something when network call response succeed.
 */
fun <T> Response<T>.onSucceed(doSth: (T) -> Unit): Response<T> {
    Logger.d("Response code: ${code()} body: ${body()}")
    if (isSuccessful && body() != null) {
        doSth(body()!!)
    }
    return this
}


/**
 * Extension function, Do something when network call response failed.
 */
fun <T> Response<T>.onFailed(errCode: (Int) -> Unit): Response<T> {
    if (!isSuccessful || body() == null) {
        errCode(code())
    }
    return this
}

/**
 * Navigate screen using Jetpack Navigation
 */
fun BaseViewModel.navigate(@IdRes resId: Int) {
    sendEvent(Type.NAVIGATE_SCREEN, Pair<@IdRes Int, Bundle?>(resId, null))
}

/**
 * Navigate screen using Jetpack Navigation
 */
fun BaseViewModel.navigate(@IdRes resId: Int, args: Bundle?) {
    sendEvent(Type.NAVIGATE_SCREEN, Pair(resId, args))
}

/**
 * Navigate screen using Jetpack Navigation
 */
fun BaseViewModel.navigate(navDirections: NavDirections) {
    sendEvent(Type.NAVIGATE_SCREEN, navDirections)
}

//Get data from bundle
inline fun <reified T> Bundle?.get(key: String): T? {
    return if (this != null && containsKey(key) && (get(key) is T)) {
        get(key) as T?
    } else {
        null
    }
}

//Get live data from bundle
inline fun <reified T> Bundle?.getLiveData(key: String): MutableLiveData<T?> {
    return MutableLiveData(this.get(key))
}