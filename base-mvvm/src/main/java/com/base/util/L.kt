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
        if (!mIsDebugging) return

        // Throwable instance must be created before any methods
        getLogInfo(Throwable().stackTrace)
        Log.e(mClassName, createLog(message))
    }

    fun i(message: String) {
        if (!mIsDebugging) return

        getLogInfo(Throwable().stackTrace)
        Log.i(mClassName, createLog(message))
    }

    fun v(message: String) {
        if (!mIsDebugging) return

        getLogInfo(Throwable().stackTrace)
        Log.v(mClassName, createLog(message))
    }

    fun w(message: String) {
        if (!mIsDebugging) return

        getLogInfo(Throwable().stackTrace)
        Log.w(mClassName, createLog(message))
    }

    fun d(obj: Any) {
        if (!mIsDebugging) return

        getLogInfo(Throwable().stackTrace)
        Log.d(mClassName, createLog(obj.toString()))
    }


    private fun createLog(log: String): String {
        return "[$mMethodName:$mLineNumber] $log"
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