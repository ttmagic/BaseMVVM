package com.base.mvvm

/**
 * Represent an Event that a ViewModel can tell a View to do.
 */
data class Event(val type: String, var data: Any?)


/**
 * Types of event.
 */
object Type {
    const val NAVIGATE_SCREEN = "NAVIGATE_SCREEN"
    const val SHOW_DIALOG = "SHOW_DIALOG"
    const val SHOW_TOAST = "SHOW_TOAST"
    const val SHOW_LOADING = "SHOW_LOADING"
    const val EXIT_SCREEN = "EXIT_SCREEN"
}
