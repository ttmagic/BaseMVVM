package com.base.util

import android.util.Log

fun logm(msg: Any?) {
    Logger.setSkipFirstMethod(true)
    Logger.d(msg)
    Logger.setSkipFirstMethod(false)
}

fun logv(msg: Any?) {
    Logger.setSkipFirstMethod(true)
    Logger.v(msg)
    Logger.setSkipFirstMethod(false)
}

fun logd(msg: Any?) {
    Logger.setSkipFirstMethod(true)
    Logger.d(msg)
    Logger.setSkipFirstMethod(false)
}

fun logi(msg: Any?) {
    Logger.setSkipFirstMethod(true)
    Logger.i(msg)
    Logger.setSkipFirstMethod(false)
}

fun loge(msg: Any?) {
    Logger.setSkipFirstMethod(true)
    Logger.e(msg)
    Logger.setSkipFirstMethod(false)
}

fun logw(msg: Any?) {
    Logger.setSkipFirstMethod(true)
    Logger.w(msg)
    Logger.setSkipFirstMethod(false)
}

/**
 * Helper class for Logging.
 */
object Logger {
    private var isSkipFirstMethod = false   //Skip first method call in stack trace or not.

    private var mClassName: String = ""
    private var mMethodName: String = ""
    private var mLineNumber: Int = 0

    private var mIsDebugging: Boolean = true

    fun setDebugging(debugging: Boolean) {
        mIsDebugging = debugging
    }


    fun e(message: Any?) {
        if (!mIsDebugging) return

        // Throwable instance must be created before any methods
        getLogInfo(Throwable().stackTrace)
        Log.e(mClassName, createLog(message))
    }

    fun i(message: Any?) {
        if (!mIsDebugging) return

        getLogInfo(Throwable().stackTrace)
        Log.i(mClassName, createLog(message))
    }

    fun v(message: Any?) {
        if (!mIsDebugging) return

        getLogInfo(Throwable().stackTrace)
        Log.v(mClassName, createLog(message))
    }

    fun w(message: Any?) {
        if (!mIsDebugging) return

        getLogInfo(Throwable().stackTrace)
        Log.w(mClassName, createLog(message))
    }

    fun d(obj: Any?) {
        if (!mIsDebugging) return

        getLogInfo(Throwable().stackTrace)
        Log.d(mClassName, createLog(obj.toString()))
    }


    private fun createLog(log: Any?): String {
        return "[$mMethodName:$mLineNumber] ${log.toString()}"
    }

    private fun getLogInfo(sElements: Array<StackTraceElement>) {
        val i = if (isSkipFirstMethod) 2 else 1

        mClassName = getClassName(sElements[i].className) ?: sElements[i].fileName
        mMethodName = getMethodName(sElements[i].className) ?: sElements[i].methodName
        mLineNumber = sElements[i].lineNumber
    }

    /**
     * get class, method name from StackTraceElement className
     * example input: com.app.MqttClient$subscribeTopic$1
     * return: MqttClient
     */
    private fun getClassName(input: String): String? {
        if (!input.contains(".")) return null
        val classOutput: String
        val className = input.split(".").last()
        classOutput = if (className.contains("$")) {
            className.split("$")[0]
        } else {
            className
        }
        return classOutput
    }

    /**
     * get method name from StackTraceElement className
     * example input: com.app.MqttClient$subscribeTopic$1
     * return: subscribeTopic
     */
    private fun getMethodName(input: String): String? {
        val className = input.split(".").last()
        return if (className.contains("$")) {
            className.split("$")[1]
        } else {
            null
        }
    }

    fun setSkipFirstMethod(skip: Boolean) {
        this.isSkipFirstMethod = skip
    }

}