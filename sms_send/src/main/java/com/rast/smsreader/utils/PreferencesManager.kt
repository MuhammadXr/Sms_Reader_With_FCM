package com.rast.smsreader.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("SmsPrefs", Context.MODE_PRIVATE)

    fun saveData(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getData(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveSimSlotData(value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("last_link", value)
        editor.apply()
    }
    fun getSimSlotData(defaultValue: Int): Int {
        return sharedPreferences.getInt("last_link", defaultValue)
    }
    fun saveIntervalData(value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("interval", value)
        editor.apply()
    }
    fun getIntervalData(defaultValue: Int): Int {
        return sharedPreferences.getInt("interval", defaultValue) ?: defaultValue
    }
}