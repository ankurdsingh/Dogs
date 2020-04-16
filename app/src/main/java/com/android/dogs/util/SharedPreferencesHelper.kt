package com.android.dogs.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit

class SharedPreferencesHelper {

    companion object{
        private const val PREF_TIME = "Pref_time"
        private var preferences :SharedPreferences?= null
        @Volatile private var instance : SharedPreferencesHelper? = null
        private val LOCK = Any()
        operator fun invoke(context: Context):SharedPreferencesHelper = instance ?: synchronized(LOCK){
            instance ?: buildHelper(context).also{
                instance = it
            }
        }

        private fun buildHelper(context: Context): SharedPreferencesHelper {
            preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return SharedPreferencesHelper()
        }
    }
    fun saveUpdateTime(time : Long){
        preferences?.edit(commit = true){
            putLong(PREF_TIME,time)
        }
    }

    fun getUpdateTime() = preferences?.getLong(PREF_TIME,0)

    fun getCacheDuration() = preferences?.getString("pref_cache_duration","")
}