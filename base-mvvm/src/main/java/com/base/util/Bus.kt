package com.base.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.base.mvvm.LiveEvent

/**
 * Same as LiveData.observe, but include removeObservers.
 */
fun LiveEvent<Any>?.subscribeWith(lifecycleOwner: LifecycleOwner, callBack: (Any?) -> Unit) {
    this?.removeObservers(lifecycleOwner)
    this?.observe(lifecycleOwner, Observer {
        callBack.invoke(it)
    })
}

object Bus {
    private val events = HashMap<String, LiveEvent<Any>>()

    fun get(key: String): LiveEvent<Any> {
        return if (events.containsKey(key)) {
            events[key]!!
        } else {
            val event = LiveEvent<Any>()
            events[key] = event
            event
        }
    }
}