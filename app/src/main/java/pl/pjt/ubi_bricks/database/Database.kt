package pl.pjt.ubi_bricks.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class Database {
    companion object {
        const val DB_NAME = "BrickList.db"
        const val DB_VERSION = 1

        const val VAL_TRUE = 1
        var instance: Singleton? = null

        fun initDatabase(context: Context) {
            if (instance == null) {
                instance = Singleton(context)
            }
        }

        suspend fun downloadDatabase(context: Context) {
            withContext(Dispatchers.IO) {
                val url = "https://github.com/PiotrJTomaszewski/UBI-Bricks/raw/master/database/BrickList.db"
                val dbFile: File = context.getDatabasePath(DB_NAME).absoluteFile
                val connection = URL(url).openConnection()
                connection.doInput = true
                connection.connect()
                val netInputStream = connection.getInputStream()
                val outputStream = FileOutputStream(dbFile)
                outputStream.use {
                        output -> netInputStream.copyTo(output)
                }
            }
        }

        private const val DB_INFO_FILE = "dbInfo"
        private const val WAS_DB_DOWNLOADED_KEY = "wasDBDownloaded"

        fun getWasDBDownloaded(context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences(DB_INFO_FILE, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(WAS_DB_DOWNLOADED_KEY, false)
        }

        fun setWasDBDownloaded(context: Context) {
            val editor = context.getSharedPreferences(DB_INFO_FILE, Context.MODE_PRIVATE).edit()
            editor.putBoolean(WAS_DB_DOWNLOADED_KEY, true)
            editor.apply()
        }

    }

    class Singleton(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
        override fun onCreate(db: SQLiteDatabase?) {

        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        }

    }

}