package com.base.mvvm

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.base.util.L
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response


/**
 * Base class for ViewModel. Support lifecycle aware.
 * Guide to app architecture: https://developer.android.com/jetpack/docs/guide
 */
abstract class BaseViewModel(app: Application) : AndroidViewModel(app), LifecycleObserver {
    val viewEvent = LiveEvent<Event>()

    val context: Context? = app.applicationContext     //Use this to get application context from ViewModel

    private val _loading = MutableLiveData<Boolean>()
    val loading :LiveData<Boolean> = _loading

    fun isLoading(isLoading: Boolean = true) {
        _loading.postValue(isLoading)
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

    /**
     * Wrapper method for launch coroutines.
     * Support show/hide loading, catch exceptions.
     * Handle exceptions in onCoroutinesExceptions.
     */
    fun coroutines(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch {
            try {
                isLoading(true)
                block()
            } catch (e: Exception) {
                e.message?.let {
                    L.e(it)
                }
                onCoroutinesExceptions(e)
                isLoading(false)

            }
            isLoading(false)
        }
    }

    /**
     * Handle exception when launch coroutines.
     */
    open fun onCoroutinesExceptions(e: Exception) {

    }
}

/**
 * Extension function, Do something when network call response succeed.
 */
fun <T> Response<T>.onSucceed(doSth: (T) -> Unit): Response<T> {
    L.d("Response code: ${code()} body: ${body()}")
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
