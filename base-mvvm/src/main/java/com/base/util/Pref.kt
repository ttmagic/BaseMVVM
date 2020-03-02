package com.base.util

import android.content.Context
import android.content.SharedPreferences
import com.base.util.Pref.init
import com.google.gson.Gson

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
     * @param sharedPrefName: Name for sharedPref storage. Usually application package name.
     */
    fun init(context: Context, sharedPrefName: String) {
        preferences = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
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
