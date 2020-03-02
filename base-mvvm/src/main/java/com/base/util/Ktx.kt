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
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.base.mvvm.BaseViewModel
import kotlinx.coroutines.Dispatchers
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

fun Context?.toast(@StringRes msgResId: Int, lengthLong: Boolean = false) {
    this?.let {
        val length = if (lengthLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        Toast.makeText(it, msgResId, length).show()
    }
}

fun Fragment.toast(msg: String, lengthLong: Boolean = false) {
    context?.toast(msg, lengthLong)
}

fun Fragment.toast(@StringRes msgResId: Int, lengthLong: Boolean = false) {
    context?.toast(msgResId, lengthLong)
}

fun BaseViewModel.toast(msg: String, lengthLong: Boolean = false) {
    context?.toast(msg, lengthLong)
}

fun BaseViewModel.toast(@StringRes msgResId: Int, lengthLong: Boolean = false) {
    context?.toast(msgResId, lengthLong)
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
fun View?.onClick(doSth: (View) -> Unit) {
    if (this == null) return
    this.setOnClickListener {
        it.isEnabled = false
        doSth(it)
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
fun View?.onClick(lifecycleScope: LifecycleCoroutineScope, doSomething: suspend () -> Unit) {
    if (this == null) return
    this.setOnClickListener {
        lifecycleScope.launch(Dispatchers.IO) {
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
fun EditText?.showKeyboard() {
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


/**
 * Add divider for RecyclerView.
 */
fun RecyclerView.addItemDividers() {
    addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
}

/**
 * Set text color resource.
 */
fun TextView.setTextColorRes(@ColorRes colorRes: Int) {
    this.setTextColor(ContextCompat.getColor(this.context, colorRes))
}


//-----------------------------------------------------------
//--Extensions for LiveData of Int.

operator fun MutableLiveData<Int>.dec(): MutableLiveData<Int> {
    var currValue = this.value ?: 0
    currValue--
    postValue(currValue)
    return this
}

operator fun MutableLiveData<Int>.plusAssign(amount: Int) {
    var currValue = this.value ?: 0
    currValue += amount
    postValue(currValue)
}

operator fun MutableLiveData<Int>.minusAssign(amount: Int) {
    var currValue = this.value ?: 0
    currValue -= amount
    postValue(currValue)
}

operator fun MutableLiveData<Int>.inc(): MutableLiveData<Int> {
    var currValue = this.value ?: 0
    currValue++
    postValue(currValue)
    return this
}


//--Extensions for LiveData of Boolean.
/**
 * Switch true/false state for MutableLiveData of Boolean.
 */
fun MutableLiveData<Boolean>.switchState() {
    var currState = value ?: false
    currState = !currState
    postValue(currState)
}


//--Extensions for LiveData of ArrayList.

/**
 * Get size of list inside liveData.
 */
val <T> MutableLiveData<ArrayList<T>>.size: Int
    get() {
        if (this.value.isNullOrEmpty()) return 0
        return this.value!!.size
    }

/**
 * Post value again to notify observers.
 */
fun <T> MutableLiveData<T>.notifyDataSetChanged() {
    this.notifyObservers()
}

/**
 * Post value again to notify observers.
 */
fun <T> MutableLiveData<T>.notifyObservers() {
    this.postValue(this.value)
}


/**
 * Get item at position.
 */
operator fun <T> MutableLiveData<ArrayList<T>>.get(position: Int): T? {
    if (this.value.isNullOrEmpty()) return null
    if (this.value!!.size <= position) return null
    return this.value!![position]
}


/**
 * Add item at position, notify observers.
 */
fun <T> MutableLiveData<ArrayList<T>>.add(position: Int = 0, item: T) {
    val temp = this.value ?: arrayListOf()
    temp.add(position, item)
    postValue(temp)
}

/**
 * Add item to the end of list, notify observers.
 */
fun <T> MutableLiveData<ArrayList<T>>.add(item: T) {
    val temp = this.value ?: arrayListOf()
    temp.add(item)
    postValue(temp)
}

/**
 * Add all item to the end of list, notify observers.
 */
fun <T> MutableLiveData<ArrayList<T>>.addAll(items: Collection<T>) {
    val temp = this.value ?: arrayListOf()
    temp.addAll(items)
    postValue(temp)
}

/**
 * Add all item at a position, notify observers.
 */
fun <T> MutableLiveData<ArrayList<T>>.addAll(position: Int, items: Collection<T>) {
    val temp = this.value ?: arrayListOf()
    temp.addAll(position, items)
    postValue(temp)
}


/**
 * Observe liveData in a cleaner way.
 */
fun <T> LiveData<T>?.observe(viewLifecycleOwner: LifecycleOwner, callBack: (data: T) -> Unit) {
    this?.observe(viewLifecycleOwner, Observer {
        callBack.invoke(it)
    })
}


