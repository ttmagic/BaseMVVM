package com.base.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.launch


/**
 * Collections of kotlin extensions.
 */


//-------------------------CONTEXT EXTENSIONS-------------------------
/**
 * Show a toast.
 */
fun Context?.toast(msg: String, lengthLong: Boolean = false) {
    this?.let {
        val length = if (lengthLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        Toast.makeText(it, msg, length).show()
    }
}

/**
 * Show a toast.
 */
fun Context?.toast(@StringRes msgResId: Int, lengthLong: Boolean = false) {
    this?.let {
        val length = if (lengthLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        Toast.makeText(it, msgResId, length).show()
    }
}

/**
 * Get status bar height in pixel
 */
fun Context.getStatusBarHeight(): Int {
    var sttBarHeight = 0
    val resId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resId > 0) {
        sttBarHeight = this.resources.getDimensionPixelSize(resId)
    }
    return sttBarHeight
}

/**
 * Get screen width in pixel.
 */
fun Context.screenWidth(): Int {
    return getDisplaySize().x
}

/**
 * Get screen height in pixel.
 */
fun Context.screenHeight(): Int {
    return getDisplaySize().y
}

private fun Context.getDisplaySize(): Point {
    val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val size = Point()
    display.getSize(size)

    return size
}

/**
 * Check if a permission is granted.
 */
fun Context.ifGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}


//-------------------------VIEW EXTENSIONS-------------------------

/**
 * Show a view (View.VISIBLE).
 */
fun View?.show() {
    this?.let {
        it.post { visibility = View.VISIBLE }
    }
}

/**
 * Hide a view (View.INVISIBLE).
 */
fun View?.hide() {
    this?.let {
        it.post { visibility = View.INVISIBLE }
    }
}

/**
 * Gone a view (View.GONE).
 */
fun View?.gone() {
    this?.let {
        it.post { visibility = View.GONE }
    }
}

/**
 * Hide keyboard from view.
 */
fun View?.hideKeyboard() {
    this?.let {
        (it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            it.windowToken,
            0
        )
    }
}

/**
 * Set click listener for view, prevent double click.
 */
fun View?.onClick(doSth: () -> Unit) {
    if (this == null) return
    this.setOnClickListener {
        it.isEnabled = false
        doSth()
        it.postDelayed({
            it.isEnabled = true
        }, 500)
    }
}

/**
 * Set click listener for view, prevent double click. Support suspend fun coroutines.
 * @param lifecycleScope: LifecycleCoroutineScope for run coroutines.
 * @param doSomething: suspend function.
 */
fun View?.onClickSuspend(lifecycleScope: LifecycleCoroutineScope, doSomething: suspend () -> Unit) {
    if (this == null) return
    this.setOnClickListener {
        lifecycleScope.launch {
            it.isEnabled = false
            doSomething()
            it.isEnabled = true //Enable view after suspend fun finish.
        }
    }
}

/**
 * Listen for event keyboard show.
 * @param isKeyboardShow callback: called when keyboard show/hide. With height = keyboard height.
 */
fun View?.onKeyboardShow(isKeyboardShow: (show: Boolean, height: Int) -> Unit) {
    if (this == null) return
    viewTreeObserver?.addOnGlobalLayoutListener {
        val r = Rect()
        getWindowVisibleDisplayFrame(r)
        val screenHeight = rootView.height
        val keyboardHeight = screenHeight - r.bottom
        if (keyboardHeight > screenHeight * 0.15) {// 0.15 ratio is perhaps enough to determine keypad height.
            isKeyboardShow(true, keyboardHeight)
        } else {
            isKeyboardShow(false, keyboardHeight)
        }
    }
}


/**
 * Show keyboard on edit text.
 */
fun EditText?.showKeyboard(){
    this?.let {
        it.requestFocus()
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
}


/**
 * Listen for text change on edit text.
 */
fun EditText?.onTextChange(doSomething: () -> Unit) {
    if (this == null) return
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            doSomething()
        }
    })
}


/**
 * Move text cursor to end of text view.
 */
fun EditText?.moveCursorEnd() {
    if (this == null) return
    if (text.isNullOrEmpty()) return
    val textLength = text.length
    this.setSelection(textLength)
}

