package pl.pjt.ubi_bricks

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log

class Settings (private val context: Context) {
    private val settingsFileName = "settings"

    companion object {
        var urlPrefix: String? = null
        var showArchived: Boolean? = null
        var databaseFilePath: String? = null

        private lateinit var databaseFilePathDefault: String
    }

    private val urlPrefixKey: String = "urlPrefix"
    private val urlPrefixDefault: String = "http://http://fcds.cs.put.poznan.pl/MyWeb/BL"

    private val showArchivedKey: String = "showArchived"
    private val showArchivedDefault: Boolean = false

    private val databaseFilePathKey: String = "databaseFile"

    fun readSettings() {
        databaseFilePathDefault = context.applicationInfo.dataDir

        val sharedPreferences = context.getSharedPreferences(settingsFileName, MODE_PRIVATE)
        urlPrefix = sharedPreferences.getString(urlPrefixKey, urlPrefixDefault)
        showArchived = sharedPreferences.getBoolean(showArchivedKey, showArchivedDefault)
        databaseFilePath = sharedPreferences.getString(databaseFilePathKey, databaseFilePathDefault)
    }

    fun saveSettings() {
        val editor = context.getSharedPreferences(settingsFileName, MODE_PRIVATE).edit()
        if (urlPrefix != null && showArchived != null /*&& databaseFilePath != null*/) {
            editor.putString(urlPrefixKey, urlPrefix)
            editor.putBoolean(showArchivedKey, showArchived!!)
            editor.putString(databaseFilePathKey, databaseFilePathDefault)
            editor.apply()
        } else {
            Log.println(Log.ERROR, "Settings", "Cannot save settings, one or more value is null")
        }
    }

}