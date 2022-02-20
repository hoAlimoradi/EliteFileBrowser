package com.alimoradi.elitefilebrowser.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.alimoradi.elitefilebrowser.addAccount.PersonSignalProtocolAsJsonData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


object PreferenceHelper {

    fun defaultPrefs(context: Context): SharedPreferences
            = PreferenceManager.getDefaultSharedPreferences(context)

    fun customPrefs(context: Context, name: String): SharedPreferences
            = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    /**
     * puts a value for the given [key].
     */
    operator fun SharedPreferences.set(key: String, value: Any?)
            = when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }

    /**
     * finds a preference based on the given [key].
     * [T] is the type of value
     * @param defaultValue optional defaultValue - will take a default defaultValue if it is not specified
     */
    inline operator fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T
            = when (T::class) {
        String::class -> getString(key, defaultValue as? String ?: "") as T
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}
object AppPreferences {
    private const val NAME = "elitefilebrowser"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences
    // list of app specific preferences
    private val IS_FIRST_RUN_PREF = Pair("is_first_run", false)
    private val MY_SIGNAL_PROTOCOL_DATA_PREF = Pair("mySignalProtocolData", "")
    private val USER_SIGNAL_PROTOCOL_DATA_LIST = Pair("userSignalProtocolDataList", "{}")

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    /**
     * puts a value for the given [key].
     */
    operator fun SharedPreferences.set(key: String, value: Any?)
            = when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }

    /**
     * finds a preference based on the given [key].
     * [T] is the type of value
     * @param defaultValue optional defaultValue - will take a default defaultValue if it is not specified
     */
    inline operator fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T
            = when (T::class) {
        String::class -> getString(key, defaultValue as? String ?: "") as T
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T
        else -> throw UnsupportedOperationException("Not yet implemented")
    }

    fun <T> setList(key: String, list: List<T>?) {
        val gson = Gson()
        val json = gson.toJson(list)
        Log.e("setList : ", "    ${key}"  +  json)
        set(key, json)
    }

    operator fun set(key: String, value: String?) {
        preferences.edit {
            it.putString(key, value)
        }
       /* preferences.edit().putString(key, value)
        preferences.edit().commit()*/
    }

    var firstRun: Boolean
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getBoolean(IS_FIRST_RUN_PREF.first, IS_FIRST_RUN_PREF.second)

        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            it.putBoolean(IS_FIRST_RUN_PREF.first, value)
        }

  /*  var mySignalProtocolData: String
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getString(MY_SIGNAL_PROTOCOL_DATA_PREF.first, MY_SIGNAL_PROTOCOL_DATA_PREF.second)!!

        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            it.putString(MY_SIGNAL_PROTOCOL_DATA_PREF.first, value)
        }

    var userSignalProtocolDataList: String
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getString(MY_SIGNAL_PROTOCOL_DATA_PREF.first, USER_SIGNAL_PROTOCOL_DATA_LIST.second)!!

        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            it.putString(USER_SIGNAL_PROTOCOL_DATA_LIST.first, value)
        }*/


    //mySignalProtocolData
    fun setMySignalProtocolData(list: List<PersonSignalProtocolAsJsonData>) {
        setList(KEY_MY_SIGNAL_PROTOCOL_DATA_PREF, list)
    }

    fun getMySignalProtocolData(): ArrayList<PersonSignalProtocolAsJsonData>? {
        val gson = Gson()
        val json = preferences.getString(KEY_MY_SIGNAL_PROTOCOL_DATA_PREF, null)
        val type: Type = object : TypeToken<ArrayList<PersonSignalProtocolAsJsonData>>() {}.type
        return gson.fromJson(json, type)
    }


    //

    fun setUserSignalProtocolDataList(list: List<PersonSignalProtocolAsJsonData>) {
        setList(KEY_USER_SIGNAL_PROTOCOL_DATA_LIST, list)
    }

    fun getUserSignalProtocolDataList(): ArrayList<PersonSignalProtocolAsJsonData>? {
        val gson = Gson()
        val json = preferences.getString(KEY_USER_SIGNAL_PROTOCOL_DATA_LIST, null)
        val type: Type = object : TypeToken<ArrayList<PersonSignalProtocolAsJsonData>>() {}.type
        return gson.fromJson(json, type)
    }

    private const val KEY_MY_SIGNAL_PROTOCOL_DATA_PREF = "MY_SIGNAL_PROTOCOL_DATA_PREF"
    private const val KEY_USER_SIGNAL_PROTOCOL_DATA_LIST = "USER_SIGNAL_PROTOCOL_DATA_LIST"
}