package com.deep.notify.data

import android.content.Context
import android.content.SharedPreferences

class ThemePreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("notify_prefs", Context.MODE_PRIVATE)

    fun isStaggeredLayout(): Boolean {
        return sharedPreferences.getBoolean("staggered_layout", true)
    }

    fun setStaggeredLayout(isStaggered: Boolean) {
        sharedPreferences.edit().putBoolean("staggered_layout", isStaggered).apply()
    }

    fun getLastBackupTime(): Long {
        return sharedPreferences.getLong("last_backup_time", 0L)
    }

    fun setLastBackupTime(time: Long) {
        sharedPreferences.edit().putLong("last_backup_time", time).apply()
    }
}
