package pl.pjt.ubi_bricks.database

import android.content.ContentValues
import java.time.LocalDate

class Inventory {
    class InventoryEntity {
        var id: Int? = null
        var name: String? = null
        var active: Boolean? = null
        var lastAccessed: LocalDate? = null
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
            val query = "SELECT $COLUMN_ID, $COLUMN_NAME, $COLUMN_ACTIVE, $COLUMN_LAST_ACCESSED FROM $TABLE_INVENTORIES"
            val cursor = db.rawQuery(query, null)
            val list = ArrayList<InventoryEntity>()
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val entity = InventoryEntity()
                    entity.id = cursor.getInt(0)
                    entity.name = cursor.getString(1)
                    entity.active = (cursor.getInt(2) == IS_ACTIVE_VAL) // Conversion to Boolean
                    entity.lastAccessed = LocalDate.ofEpochDay(cursor.getLong(3)) // TODO: It's not epoch day?
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
            values.put(COLUMN_LAST_ACCESSED, LocalDate.now().toEpochDay())
            db.insert(TABLE_INVENTORIES, null, values)
            return id
        }
    }

}