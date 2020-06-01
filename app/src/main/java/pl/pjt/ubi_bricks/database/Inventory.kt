package pl.pjt.ubi_bricks.database

import android.content.ContentValues
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class Inventory {
    class InventoryEntity {
        var id: Int? = null
        var name: String? = null
        var active: Boolean? = null
        var lastAccessed: Instant? = null
    }

    companion object {
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "Name"
        private const val COLUMN_ACTIVE = "Active"
        private const val COLUMN_LAST_ACCESSED = "LastAccessed"
        private const val TABLE_INVENTORIES = "Inventories"

        private const val IS_ACTIVE_VAL = 1

        fun getAll(): ArrayList<InventoryEntity> {
            val db = Database.instance!!.readableDatabase
            val query =
                "SELECT $COLUMN_ID, $COLUMN_NAME, $COLUMN_ACTIVE, $COLUMN_LAST_ACCESSED FROM $TABLE_INVENTORIES ORDER BY $COLUMN_LAST_ACCESSED DESC"
            val cursor = db.rawQuery(query, null)
            val list = ArrayList<InventoryEntity>()
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val entity = InventoryEntity()
                    entity.id = cursor.getInt(0)
                    entity.name = cursor.getString(1)
                    entity.active = (cursor.getInt(2) == IS_ACTIVE_VAL) // Conversion to Boolean
                    entity.lastAccessed = Instant.ofEpochMilli(cursor.getLong(3))
                    list.add(entity)
                    cursor.moveToNext()
                }
                cursor.close()
            }
            return list
        }

        fun getNonArchived(): ArrayList<InventoryEntity> {
            val db = Database.instance!!.readableDatabase
            val query =
                "SELECT $COLUMN_ID, $COLUMN_NAME, $COLUMN_ACTIVE, $COLUMN_LAST_ACCESSED FROM $TABLE_INVENTORIES WHERE $COLUMN_ACTIVE = 1 ORDER BY $COLUMN_LAST_ACCESSED DESC"
            val cursor = db.rawQuery(query, null)
            val list = ArrayList<InventoryEntity>()
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val entity = InventoryEntity()
                    entity.id = cursor.getInt(0)
                    entity.name = cursor.getString(1)
                    entity.active = (cursor.getInt(2) == IS_ACTIVE_VAL) // Conversion to Boolean
                    entity.lastAccessed = Instant.ofEpochMilli(cursor.getLong(3))
                    list.add(entity)
                    cursor.moveToNext()
                }
                cursor.close()
            }
            return list
        }

        private fun getFreeKey(): Int {
            val db = Database.instance!!.readableDatabase
            val query = "SELECT MAX($COLUMN_ID)+1 FROM $TABLE_INVENTORIES"
            val cursor = db.rawQuery(query, null)
            val id = if (cursor.moveToFirst()) {
                cursor.getInt(0)
            } else {
                0
            }
            cursor.close()
            return id
        }

        fun add(name: String): Int {
            val db = Database.instance!!.writableDatabase
            val values = ContentValues()
            val id = getFreeKey()
            values.put(COLUMN_ID, id)
            values.put(COLUMN_NAME, name)
            values.put(COLUMN_ACTIVE, IS_ACTIVE_VAL)
            values.put(COLUMN_LAST_ACCESSED, Instant.now().toEpochMilli())
            db.insert(TABLE_INVENTORIES, null, values)
            return id
        }

        fun setLastAccessed(id: Int) {
            val db = Database.instance!!.writableDatabase
            val values = ContentValues()
            values.put(COLUMN_LAST_ACCESSED, Instant.now().toEpochMilli())
            db.update(TABLE_INVENTORIES, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        }
    }

}