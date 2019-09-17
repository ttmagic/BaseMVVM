package com.base.util

import android.util.Log

/**
 * Helper class for Logging.
 */
object L {

    private var mClassName: String = ""
    private var mMethodName: String = ""
    private var mLineNumber: Int = 0

    private var mIsDebugging: Boolean = true

    fun setDebugging(debugging: Boolean) {
        mIsDebugging = debugging
    }


    fun e(message: String) {
        if (!mIsDebugging)
            return

        // Throwable instance must be created before any methods
        getMethodNames(Throwable().stackTrace)
        Log.e(mClassName, createLog(message))
    }

    fun i(message: String) {
        if (!mIsDebugging)
            return

        getMethodNames(Throwable().stackTrace)
        Log.i(mClassName, createLog(message))
    }

    fun v(message: String) {
        if (!mIsDebugging)
            return

        getMethodNames(Throwable().stackTrace)
        Log.v(mClassName, createLog(message))
    }

    fun w(message: String) {
        if (!mIsDebugging)
            return

        getMethodNames(Throwable().stackTrace)
        Log.w(mClassName, createLog(message))
    }

    fun d(obj: Any) {
        if (!mIsDebugging)
            return

        getMethodNames(Throwable().stackTrace)
        Log.d(mClassName, createLog(obj.toString()))
    }


    private fun createLog(log: String): String {
        val buffer = StringBuffer()
        buffer.append("[")
        buffer.append(mMethodName)
        buffer.append(":")
        buffer.append(mLineNumber)
        buffer.append("] ")
        buffer.append(log)

        return buffer.toString()
    }

    private fun getMethodNames(sElements: Array<StackTraceElement>) {
        mClassName = sElements[1].fileName
        mMethodName = sElements[1].methodName
        mLineNumber = sElements[1].lineNumber
    }

}