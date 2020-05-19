//package pl.pjt.ubi_bricks
//
//import android.content.ContentValues
//import android.content.Context
//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.io.*
//import java.net.URL
//import java.time.LocalDate
//
//class DatabaseHandler(private val context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
//    companion object {
//        const val DB_NAME = "BrickList.db"
//        const val DB_VERSION = 1
//        const val TABLE_CODES = "Codes"
//        const val TABLE_COLORS = "Colors"
//        const val TABLE_ITEM_TYPES = "ItemTypes"
//        const val TABLE_PARTS = "Parts"
//        const val TABLE_INVENTORIES = "Inventories"
//        const val TABLE_INVENTORIES_PARTS = "InventoriesParts"
//
//        const val COLUMN_NAME = "Name"
//        const val COLUMN_NAME_PL = "NamePL"
//        const val COLUMN_ID = "id"
//        const val COLUMN_ITEM_ID = "ItemID"
//        const val COLUMN_COLOR_ID = "ColorID"
//        const val COLUMN_TYPE_ID = "TypeID"
//        const val COLUMN_CODE = "Code"
//        const val COLUMN_IMAGE = "Image"
//        const val COLUMN_ACTIVE = "Active"
//        const val COLUMN_LAST_ACCESSED = "LastAccessed"
//        const val COLUMN_INVENTORY_ID = "InventoryID"
//        const val COLUMN_QUANTITY_IN_SET = "QuantityInSet"
//        const val COLUMN_QUANTITY_IN_STORE = "QuantityInStore"
//        const val COLUMN_EXTRA = "Extra"
//
//        const val VAL_TRUE = 1
//
//        suspend fun downloadDatabase(context: Context) {
//            withContext(Dispatchers.IO) {
//                val url = "https://github.com/PiotrJTomaszewski/UBI-Bricks/raw/master/database/BrickList.db"
//                val dbFile: File = context.getDatabasePath(DB_NAME).absoluteFile
//                val connection = URL(url).openConnection()
//                connection.doInput = true
//                connection.connect()
//                val netInputStream = connection.getInputStream()
//                val outputStream = FileOutputStream(dbFile)
//                outputStream.use {
//                    output -> netInputStream.copyTo(output)
//                }
//            }
//        }
//
//        private const val DB_INFO_FILE = "dbInfo"
//        private const val WAS_DB_DOWNLOADED_KEY = "wasDBDownloaded"
//
//        fun getWasDBDownloaded(context: Context): Boolean {
//            val sharedPreferences = context.getSharedPreferences(DB_INFO_FILE, Context.MODE_PRIVATE)
//            return sharedPreferences.getBoolean(WAS_DB_DOWNLOADED_KEY, false)
//        }
//
//        fun setWasDBDownloaded(context: Context) {
//            val editor = context.getSharedPreferences(DB_INFO_FILE, Context.MODE_PRIVATE).edit()
//            editor.putBoolean(WAS_DB_DOWNLOADED_KEY, true)
//            editor.apply()
//        }
//    }
//
//    override fun onCreate(db: SQLiteDatabase?) {
//
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//
//    }
//
//    fun getColorName(colorIdCode: Int): String? {
//        val query = "SELECT IFNULL($COLUMN_NAME_PL, $COLUMN_NAME) FROM $TABLE_COLORS WHERE $COLUMN_CODE = ?"
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(query, arrayOf(colorIdCode.toString()))
//        var name: String? = null
//        if (cursor.moveToFirst()) {
//            name = cursor.getString(0)
//            cursor.close()
//        }
////        db.close()
//        return name
//    }
//
//    fun getTypeName(typeId: Int): String? {
//        val query = "SELECT IFNULL($COLUMN_NAME_PL, $COLUMN_NAME) FROM $TABLE_ITEM_TYPES WHERE $COLUMN_ID = ?"
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(query, arrayOf(typeId.toString()))
//        var name: String? = null
//        if (cursor.moveToFirst()) {
//            name = cursor.getString(0)
//            cursor.close()
//        }
////        db.close()
//        return name
//    }
//
//    fun getTypeId(typeIdCode: String): Int? {
//        val query = "SELECT $COLUMN_ID FROM $TABLE_ITEM_TYPES WHERE $COLUMN_CODE = ?"
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(query, arrayOf(typeIdCode))
//        var id: Int? = null
//        if (cursor.moveToFirst()) {
//            id = cursor.getInt(0)
//            cursor.close()
//        }
////        db.close()
//        return id
//    }
//
//    fun getPartName(partIDCode: String): String? {
//        val query = "SELECT IFNULL($COLUMN_NAME_PL, $COLUMN_NAME) FROM $TABLE_PARTS WHERE $COLUMN_CODE = ?"
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(query, arrayOf(partIDCode))
//        var name: String? = null
//        if (cursor.moveToFirst()) {
//            name = cursor.getString(0)
//            cursor.close()
//        }
////        db.close()
//        return name
//    }
//
//    fun getPartItemId(partIDCode: String): Int? {
//        val query = "SELECT $COLUMN_ID FROM $TABLE_PARTS WHERE $COLUMN_CODE = ?"
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(query, arrayOf(partIDCode))
//        var id: Int? = null
//        if (cursor.moveToFirst()) {
//            id = cursor.getInt(0)
//            cursor.close()
//        }
////        db.close()
//        return id
//    }
//
//    fun getColorId(colorIdCode: Int): Int? {
//        val query = "SELECT $COLUMN_ID FROM $TABLE_COLORS WHERE $COLUMN_CODE = ?"
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(query, arrayOf(colorIdCode.toString()))
//        var id: Int? = null
//        if (cursor.moveToFirst()) {
//            id = cursor.getInt(0)
//            cursor.close()
//        }
////        db.close()
//        return id
//    }
//
//    fun getPartCodeId(itemId: Int, colorId: Int): Int? {
//        val query = "SELECT $COLUMN_ID FROM $TABLE_CODES WHERE $COLUMN_ITEM_ID = ? AND $COLUMN_COLOR_ID = ?"
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(query, arrayOf(itemId.toString(), colorId.toString()))
//        var id: Int? = null
//        if (cursor.moveToFirst()) {
//            id = cursor.getInt(0)
//            cursor.close()
//        }
////        db.close()
//        return id
//    }
//
//    fun getPartDesignIdCode(codeId: Int): Int? {
//        val query = "SELECT $COLUMN_CODE FROM $TABLE_CODES WHERE $COLUMN_ID = ?"
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(query, arrayOf(codeId.toString()))
//        var designIdCode: Int? = null
//        if (cursor.moveToFirst()) {
//            designIdCode = cursor.getInt(0)
//            cursor.close()
//        }
////        db.close()
//        return designIdCode
//    }
//
//    suspend fun getPartImage(codesId: Int): Bitmap? {
//        val codesIdString = codesId.toString()
//        val query = "SELECT $COLUMN_IMAGE FROM $TABLE_CODES WHERE $COLUMN_ID = ?"
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(query, arrayOf(codesIdString))
//        var image: Bitmap? = null
//        if (cursor.moveToFirst()) {
//            val blob = cursor.getBlob(0)
//            image = BitmapFactory.decodeByteArray(blob, 0, blob.size)
//            cursor.close()
//        } else {
//            image = downloadPartImage(codesId)
//            if (image != null) {
//                GlobalScope.launch {
//                    setPartImage(codesIdString, image)
//                }
//            }
//        }
////        db.close()
//        return image
//    }
//
//    private suspend fun downloadPartImage(codesId: Int): Bitmap? {
//        val code = getPartDesignIdCode(codesId)
//        val url = "https://www.lego.com/service/bricks/5/2/$code"
//        // TODO: Add other urls
//        var image: Bitmap? = null
//        withContext(Dispatchers.IO) {
//            val connection = URL(url).openConnection()
//            connection.doInput = true
//            connection.connect()
//            val inputStream = connection.getInputStream()
//            image = BitmapFactory.decodeStream(inputStream)
//        }
//        return image
//    }
//
//    private fun setPartImage(codesIdString: String, image: Bitmap) {
//        val db = this.writableDatabase
//        val values = ContentValues()
//        val stream = ByteArrayOutputStream()
//        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
//        val blob = stream.toByteArray()
//        values.put(COLUMN_IMAGE, blob)
//        db.update(TABLE_CODES, values, "$COLUMN_ID = ?", arrayOf(codesIdString))
//        db.close()
//    }
//
//    fun getInventories(): ArrayList<Inventory> {
//        val query = "SELECT $COLUMN_ID, $COLUMN_NAME, $COLUMN_ACTIVE, $COLUMN_LAST_ACCESSED FROM $TABLE_INVENTORIES ORDER BY $COLUMN_LAST_ACCESSED DESC"
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(query, null)
//        val list = ArrayList<Inventory>()
//        if (cursor.moveToFirst()) {
//            while (!cursor.isAfterLast) {
//                val id = cursor.getInt(0)
//                val name = cursor.getString(1)
//                val isActive = (cursor.getInt(2) == VAL_TRUE) // Conversion to Boolean
//                val lastAccessed =  LocalDate.ofEpochDay(cursor.getLong(3))
//                list.add(Inventory(id, name, isActive, lastAccessed))
//            }
//            cursor.moveToNext()
//            cursor.close()
//        }
////        db.close()
//        return list
//    }
//
//    private fun getFreeKey(table: String): Int {
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(query, null)
//        val id = if (cursor.moveToFirst()) {
//            cursor.getInt(0)
//        } else {
//            0
//        }
//        cursor.close()
////        db.close()
//        return id
//    }
//
//    fun getInventoriesPartsFreeKey(): Int {
//        return getFreeKey(TABLE_INVENTORIES_PARTS)
//    }
//
//    fun addInventory(name: String): Int {
//        val db = this.writableDatabase
//        val values = ContentValues()
//        val id = getFreeKey(TABLE_INVENTORIES)
//        values.put(COLUMN_ID, id)
//        values.put(COLUMN_NAME, name)
//        values.put(COLUMN_ACTIVE, VAL_TRUE)
//        values.put(COLUMN_LAST_ACCESSED, LocalDate.now().toEpochDay())
//        db.insert(TABLE_INVENTORIES, null, values)
////        db.close()
//        return id
//    }
//
//    fun addPart(id: Int, inventoryId: Int, part: Part) {
//        val db = this.writableDatabase
//        val values = ContentValues()
//        values.put(COLUMN_ID, id)
//        values.put(COLUMN_INVENTORY_ID, inventoryId)
//        if (part.typeId != null) {
//            values.put(COLUMN_TYPE_ID, part.typeId)
//        }
//        if (part.itemId != null) {
//            values.put(COLUMN_ITEM_ID, part.itemId)
//        }
//        values.put(COLUMN_QUANTITY_IN_SET, part.quantityInSet)
//        values.put(COLUMN_QUANTITY_IN_STORE, part.quantityInStore)
//        if (part.colorId != null) {
//            values.put(COLUMN_COLOR_ID, part.colorId)
//        }
//        values.put(COLUMN_EXTRA, part.extra)
//        db.insert(TABLE_INVENTORIES_PARTS, null, values)
////        db.close()
//    }
//
//    fun getInventoryParts(inventoryId: Int): ArrayList<Part> {
//        val query = "SELECT $COLUMN_ID, $COLUMN_TYPE_ID, $COLUMN_ITEM_ID, $COLUMN_QUANTITY_IN_SET, $COLUMN_QUANTITY_IN_STORE, $COLUMN_COLOR_ID, $COLUMN_EXTRA FROM $TABLE_PARTS WHERE $COLUMN_INVENTORY_ID = ?"
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(query, arrayOf(inventoryId.toString()))
//        val parts = ArrayList<Part>()
//
//
//        if (cursor.moveToFirst()) {
//            while (!cursor.isAfterLast) {
//                val id = cursor.getInt(0)
//                val typeId = cursor.getInt(1)
//                val itemId = cursor.getInt(2)
//                val quantityInSet = cursor.getInt(3)
//                val quantityInStore = cursor.getInt(4)
//                val colorId = cursor.getInt(5)
//                val extra = cursor.getInt(6)
//
//                val colorQuery = "SELECT"
//
//            }
//        }
//        cursor.close()
////        db.close()
//        return id
//    }
//}