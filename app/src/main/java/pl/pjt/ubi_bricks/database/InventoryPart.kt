package pl.pjt.ubi_bricks.database

import android.content.ContentValues
import android.util.Log

class InventoryPart {
    class InventoryPartEntity {
        var id: Int? = null
        var inventoryId: Int? = null // This can be just an id
        var quantityInSet: Int? = null
        var quantityInStore: Int? = null
        var extra: Int? = null

        var typeId: Int? = null
        var partId: Int? = null
        var colorId: Int? = null

        var typeEntity: ItemType.ItemTypeEntity? = null
        var partEntity: Part.PartEntity? = null
        var colorEntity: Color.ColorEntity? = null
        var legoCodeEntity: LegoCode.LegoCodeEntity? = null
    }

    companion object {
        private const val COLUMN_ID = "id"
        private const val COLUMN_INVENTORY_ID = "InventoryID"
        private const val COLUMN_TYPE_ID = "TypeID"
        private const val COLUMN_ITEM_ID = "ItemID"
        private const val COLUMN_QUANTITY_IN_SET = "QuantityInSet"
        private const val COLUMN_QUANTITY_IN_STORE = "QuantityInStore"
        private const val COLUMN_COLOR_ID = "ColorID"
        private const val COLUMN_EXTRA = "Extra"

        private const val TABLE_INVENTORIES_PARTS = "InventoriesParts"

        fun getByInventoryId(inventoryId: Int): ArrayList<InventoryPartEntity> {
            val db = Database.instance!!.readableDatabase
            val query =
                "SELECT $COLUMN_ID, $COLUMN_TYPE_ID, $COLUMN_ITEM_ID, $COLUMN_QUANTITY_IN_SET, $COLUMN_QUANTITY_IN_STORE, $COLUMN_COLOR_ID, $COLUMN_EXTRA FROM $TABLE_INVENTORIES_PARTS WHERE $COLUMN_INVENTORY_ID = ? ORDER BY $COLUMN_QUANTITY_IN_STORE/$COLUMN_QUANTITY_IN_SET ASC"
            val cursor = db.rawQuery(query, arrayOf(inventoryId.toString()))
            val list = ArrayList<InventoryPartEntity>()
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val entity = InventoryPartEntity()
                    entity.inventoryId = inventoryId
                    entity.id = cursor.getInt(0)
                    entity.typeId = cursor.getInt(1)
                    entity.partId = cursor.getInt(2)
                    entity.quantityInSet = cursor.getInt(3)
                    entity.quantityInStore = cursor.getInt(4)
                    entity.colorId = cursor.getInt(5)
                    entity.extra = cursor.getInt(6)
                    list.add(entity)
                    cursor.moveToNext()
                }
                cursor.close()
            }
            return list
        }

        fun getFreeKey(): Int {
            val db = Database.instance!!.readableDatabase
            val query = "SELECT MAX($COLUMN_ID)+1 FROM $TABLE_INVENTORIES_PARTS"
            val cursor = db.rawQuery(query, null)
            val id = if (cursor.moveToFirst()) {
                cursor.getInt(0)
            } else {
                0
            }
            cursor.close()
            return id
        }

        fun add(entity: InventoryPartEntity) {
            val db = Database.instance!!.writableDatabase
            val values = ContentValues()
            values.put(COLUMN_ID, entity.id)
            values.put(COLUMN_INVENTORY_ID, entity.inventoryId)
            values.put(COLUMN_TYPE_ID, entity.typeId)
            values.put(COLUMN_ITEM_ID, entity.partId)
            values.put(COLUMN_QUANTITY_IN_SET, entity.quantityInSet)
            values.put(COLUMN_QUANTITY_IN_STORE, entity.quantityInStore)
            values.put(COLUMN_COLOR_ID, entity.colorId)
//            values.put(COLUMN_EXTRA, entity.extra)
            values.put(COLUMN_EXTRA, 0)
            val result = db.insert(TABLE_INVENTORIES_PARTS, null, values)
            Log.println(Log.DEBUG, "Database", result.toString())
        }

        fun incrementInStore(id: Int): Boolean {
            var result = false
            val db = Database.instance!!.writableDatabase
            val query =
                "SELECT $COLUMN_QUANTITY_IN_STORE, $COLUMN_QUANTITY_IN_SET FROM $TABLE_INVENTORIES_PARTS WHERE $COLUMN_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(id.toString()))
            if (cursor.moveToFirst()) {
                var inStore = cursor.getInt(0)
                val inSet = cursor.getInt(1)
                cursor.close()
                inStore += 1
                if (inStore <= inSet) {
                    val values = ContentValues()
                    values.put(COLUMN_QUANTITY_IN_STORE, inStore)
                    db.update(
                        TABLE_INVENTORIES_PARTS,
                        values,
                        "$COLUMN_ID = ?",
                        arrayOf(id.toString())
                    )
                    result = true
                }
            }
            return result
        }

        fun decrementInStore(id: Int): Boolean {
            var result = false
            val db = Database.instance!!.writableDatabase
            val query =
                "SELECT $COLUMN_QUANTITY_IN_STORE FROM $TABLE_INVENTORIES_PARTS WHERE $COLUMN_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(id.toString()))
            if (cursor.moveToFirst()) {
                var inStore = cursor.getInt(0)
                cursor.close()
                inStore -= 1
                if (inStore >= 0) {
                    val values = ContentValues()
                    values.put(COLUMN_QUANTITY_IN_STORE, inStore)
                    db.update(
                        TABLE_INVENTORIES_PARTS,
                        values,
                        "$COLUMN_ID = ?", arrayOf(id.toString())
                    )
                    result = true
                }
            }
            return result
        }
    }

}