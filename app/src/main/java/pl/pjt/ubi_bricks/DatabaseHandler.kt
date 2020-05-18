package pl.pjt.ubi_bricks

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.core.content.contentValuesOf
import androidx.core.database.getBlobOrNull
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.net.URL
import java.time.LocalDate
import java.util.zip.ZipInputStream

class DatabaseHandler(private val context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        const val DB_NAME = "BrickList.db"
        const val DB_VERSION = 1
        const val TABLE_CODES = "Codes"
        const val TABLE_COLORS = "Colors"
        const val TABLE_ITEM_TYPES = "ItemTypes"
        const val TABLE_PARTS = "Parts"
        const val TABLE_INVENTORIES = "Inventories"
        const val TABLE_INVENTORIES_PARTS = "InventoriesParts"

        const val COLUMN_NAME = "Name"
        const val COLUMN_NAME_PL = "NamePL"
        const val COLUMN_ID = "id"
        const val COLUMN_ITEM_ID = "ItemID"
        const val COLUMN_COLOR_ID = "ColorID"
        const val COLUMN_TYPE_ID = "TypeID"
        const val COLUMN_CODE = "Code"
        const val COLUMN_IMAGE = "Image"
        const val COLUMN_ACTIVE = "Active"
        const val COLUMN_LAST_ACCESSED = "LastAccessed"

        const val VAL_TRUE = 1

        suspend fun downloadDatabase(context: Context) {
            withContext(Dispatchers.IO) {
                val url = "https://github.com/PiotrJTomaszewski/UBI-Bricks/raw/master/BrickList.db"
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

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun getColorName(colorId: Int): String? {
        val query = "SELECT IFNULL($COLUMN_NAME_PL, $COLUMN_NAME) FROM $TABLE_COLORS WHERE $COLUMN_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(colorId.toString()))
        var name: String? = null
        if (cursor.moveToFirst()) {
            name = cursor.getString(0)
            cursor.close()
        }
        db.close()
        return name
    }

    fun getTypeName(typeId: Int): String? {
        val query = "SELECT IFNULL($COLUMN_NAME_PL, $COLUMN_NAME) FROM $TABLE_ITEM_TYPES WHERE $COLUMN_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(typeId.toString()))
        var name: String? = null
        if (cursor.moveToFirst()) {
            name = cursor.getString(0)
            cursor.close()
        }
        db.close()
        return name
    }

    fun getPartName(partId: Int): String? {
        val query = "SELECT IFNULL($COLUMN_NAME_PL, $COLUMN_NAME) FROM $TABLE_PARTS WHERE $COLUMN_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(partId.toString()))
        var name: String? = null
        if (cursor.moveToFirst()) {
            name = cursor.getString(0)
            cursor.close()
        }
        db.close()
        return name
    }

    fun getPartCodeId(partId: Int, colorId: Int): Int? {
        val query = "SELECT $COLUMN_ID FROM $TABLE_CODES WHERE $COLUMN_ITEM_ID = ? AND $COLUMN_COLOR_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(partId.toString(), colorId.toString()))
        var id: Int? = null
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0)
            cursor.close()
        }
        db.close()
        return id
    }

    fun getPartDesignId(codeId: Int): String? {
        val query = "SELECT $COLUMN_CODE FROM $TABLE_CODES WHERE $COLUMN_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(codeId.toString()))
        var designId: String? = null
        if (cursor.moveToFirst()) {
            designId = cursor.getString(0)
            cursor.close()
        }
        db.close()
        return designId
    }

    fun getPartImage(codeId: Int): Bitmap? {
        val query = "SELECT $COLUMN_IMAGE FROM $TABLE_CODES WHERE $COLUMN_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(codeId.toString()))
        var image: Bitmap? = null
        if (cursor.moveToFirst()) {
            val blob = cursor.getBlob(0)
            image = BitmapFactory.decodeByteArray(blob, 0, blob.size)
            cursor.close()
        }
        db.close()
        return image
    }

    fun setPartImage(codeId: Int, image: Bitmap) {
        val db = this.writableDatabase
        val values = ContentValues()
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val blob = stream.toByteArray()
        values.put(COLUMN_IMAGE, blob)
        db.update(TABLE_CODES, values, "$COLUMN_ID = ?", arrayOf(codeId.toString()))
        db.close()
    }

    fun getInventories(): ArrayList<Inventory> {
        val query = "SELECT $COLUMN_ID, $COLUMN_NAME, $COLUMN_ACTIVE, $COLUMN_LAST_ACCESSED FROM $TABLE_INVENTORIES ORDER BY $COLUMN_LAST_ACCESSED DESC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val list = ArrayList<Inventory>()
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val isActive = (cursor.getInt(2) == VAL_TRUE) // Conversion to Boolean
            val lastAccessed =  LocalDate.ofEpochDay(cursor.getLong(3))
            list.add(Inventory(id, name, isActive, lastAccessed))
            cursor.close()
        }
        db.close()
        return list
    }

    fun getFreeKey(table: String): Int {
        val query = "SELECT MAX($COLUMN_ID)+1 FROM $table"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val id = cursor.getInt(0)
        cursor.close()
        db.close()
        return id
    }

    fun addInventory(name: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        val id = getFreeKey(TABLE_INVENTORIES)
        values.put(COLUMN_ID, id)
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_ACTIVE, VAL_TRUE)
        values.put(COLUMN_LAST_ACCESSED, LocalDate.now().toEpochDay())
        db.insert(TABLE_INVENTORIES, null, values)
        db.close()
        return id
    }
}