package com.base.util

import android.content.Context
import android.content.SharedPreferences
import com.base.util.Pref.init
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


/**
 * Util for Shared Preferences.
 * init in Application before use.
 * @see init
 */
object Pref {
    var preferences: SharedPreferences? = null
        private set

    /**
     * Init before use.
     */
    fun init(context: Context) {
        preferences = context.getSharedPreferences(context.applicationInfo.packageName, Context.MODE_PRIVATE)
    }


    /**
     * Remove a preferences!!.
     */
    fun delete(key: String?) {
        checkInit()
        if (key == null || key.isEmpty()) {
            return
        }
        preferences!!.edit().remove(key)?.apply()
    }

    /**
     * Check if Prefs contains key.
     * @return true if contains.
     */
    fun contains(key: String?): Boolean {
        checkInit()
        return preferences!!.contains(key)
    }


    /**
     * Gets boolean data.
     *
     * @return the boolean data
     */
    fun getBool(key: String, default: Boolean = false): Boolean {
        checkInit()
        return preferences!!.getBoolean(key, default)
    }


    /**
     * Gets int data.
     *
     * @return the int data
     */
    fun getInt(key: String, default: Int = 0): Int {
        checkInit()
        return preferences!!.getInt(key, default)
    }


    /**
     * Gets int data.
     *
     * @param key     the key
     * @return the int data
     */
    fun getLong(key: String, default: Long = 0): Long {
        checkInit()
        return preferences!!.getLong(key, default)
    }

    /**
     * Gets string data.
     *
     * @param key     the key
     * @return the string data
     */
    fun getString(key: String): String? {
        checkInit()
        return preferences!!.getString(key, null)
    }

    /**
     * Gets string data.
     *
     * @param key     the key
     * @return the string data
     */
    // Get Data
    inline fun <reified T> getObj(key: String, myClass: Class<T>): T? {
        if (preferences == null) return null
        val json = preferences!!.getString(key, null)
        return if (json.isNullOrEmpty()) null else Gson().fromJson(json, myClass)
    }

    /**
     * Save data.
     *
     * @param key     the key
     * @param value    the value
     */
    // Save Data
    fun putObj(key: String, value: Any?) {
        checkInit()
        if (value != null) {
            val json = Gson().toJson(value)
            preferences!!.edit().putString(key, json).apply()
        } else {
            preferences!!.edit().putString(key, null).apply()
        }

    }

    fun putList(key: String, value: List<Any?>?) {
        if (value != null) {
            val json = Gson().toJson(value)
            preferences!!.edit().putString(key, json).apply()
        } else {
            preferences!!.edit().putString(key, null).apply()
        }
    }

    inline fun <reified T> getList(key: String, myClass: Class<T>): List<T?>? {
        if (preferences == null) return null

        val type: Type = object : TypeToken<List<T?>?>() {}.type
        val json = preferences!!.getString(key, null)
        return Gson().fromJson(json, type)
    }


    /**
     * Save data.
     *
     * @param key     the key
     * @param value    the value
     */
    fun putString(key: String, value: String) {
        checkInit()
        preferences!!.edit().putString(key, value).apply()
    }

    /**
     * Save data.
     *
     * @param key     the key
     * @param value    the value
     */
    fun putInt(key: String, value: Int) {
        checkInit()
        preferences!!.edit().putInt(key, value).apply()
    }

    /**
     * Save data.
     *
     * @param key     the key
     * @param value    the value
     */
    fun putLong(key: String, value: Long) {
        checkInit()
        preferences!!.edit().putLong(key, value).apply()
    }

    /**
     * Save data.
     *
     * @param key     the key
     * @param value    the value
     */
    fun putBool(key: String, value: Boolean) {
        checkInit()
        preferences!!.edit().putBoolean(key, value).apply()
    }


    private fun checkInit() {
        if (preferences == null) throw Exception("Please call Pref.init before first usage.")
    }
}
