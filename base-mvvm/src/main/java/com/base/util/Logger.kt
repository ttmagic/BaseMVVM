package com.base.util

import android.util.Log

fun logm(msg: Any?) {
    Logger.d(msg)
}

fun logv(msg: Any?) {
    Logger.v(msg)
}

fun logd(msg: Any?) {
    Logger.d(msg)
}

fun logi(msg: Any?) {
    Logger.i(msg)
}

fun loge(msg: Any?) {
    Logger.e(msg)
}

fun logw(msg: Any?) {
    Logger.w(msg)
}

/**
 * Helper class for Logging.
 */
object Logger {

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
        mClassName = getClassName(sElements[1].className) ?: sElements[1].fileName
        mMethodName = getMethodName(sElements[1].className) ?: sElements[1].methodName
        mLineNumber = sElements[1].lineNumber
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

}