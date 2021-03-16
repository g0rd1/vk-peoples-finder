package ru.g0rd1.peoplesfinder.repo

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@SuppressLint("ApplySharedPref")
open class SimpleSharedPrefRepo(repoName: String, context: Context): SharedPrefRepo {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(repoName, Context.MODE_PRIVATE)

    override fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).commit()
    }

    override fun getInt(key: String, defValue: Int): Int {
        return sharedPreferences.getInt(key, defValue)
    }

    override fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).commit()
    }

    override fun getStringOrNull(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun getString(key: String, defValue: String): String {
        return sharedPreferences.getString(key, defValue) ?: defValue
    }

    override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).commit()
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    override fun <T> putObject(key: String, value: T) {
        sharedPreferences.edit().putString(key, Gson().toJson(value)).commit()
    }

    override fun <T> getObject(key: String, defValue: T): T {
        val type = object : TypeToken<T>() {}.type
        return Gson().fromJson(sharedPreferences.getString(key, null), type) ?: defValue
    }

    override fun <T> putList(key: String, value: List<T>) {
        sharedPreferences.edit().putString(key, Gson().toJson(value)).commit()
    }

    override fun <T> getList(key: String, defValue: List<T>): List<T> {
        val type = object : TypeToken<List<T>>() {}.type
        return Gson().fromJson(sharedPreferences.getString(key, null), type) ?: defValue
    }

    override fun <T : Enum<T>> putEnum(key: String, value: T) {
        sharedPreferences.edit().putString(key, value.name).apply()
    }

    override fun <T : Enum<T>> getEnum(key: String, defaultValue: T): T {
        val enumName = sharedPreferences.getString(key, "") ?: return defaultValue
        return defaultValue.declaringClass.enumConstants?.firstOrNull { it.name == enumName } ?: defaultValue
    }

    override fun <T : Enum<T>> getEnum(key: String, clazz: Class<T>): T {
        val enumName = sharedPreferences.getString(key, null)!!
        return clazz.enumConstants!!.first { it.name == enumName }
    }

}
