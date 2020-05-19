package pl.pjt.ubi_bricks.database

class ItemType {
    class ItemTypeEntity {
        var id: Int? = null
        var code: String? = null
        var name: String? = null
    }

    companion object {
        private const val COLUMN_ID = "id"
        private const val COLUMN_CODE = "Code"
        private const val COLUMN_NAME = "Name"
        private const val COLUMN_NAME_PL = "NamePL"
        private const val TABLE_ITEM_TYPES = "ItemTypes"

        fun getById(id: Int): ItemTypeEntity {
            val db = Database.instance!!.readableDatabase
            val query = "SELECT $COLUMN_CODE, IFNULL($COLUMN_NAME_PL, $COLUMN_NAME) FROM $TABLE_ITEM_TYPES WHERE $COLUMN_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(id.toString()))
            val entity = ItemTypeEntity()
            entity.id = id
            if (cursor.moveToFirst()) {
                entity.code = cursor.getString(0)
                entity.name = cursor.getString(1)
                cursor.close()
            }
            return entity
        }

        fun getByCode(code: String): ItemTypeEntity {
            val db = Database.instance!!.readableDatabase
            val query = "SELECT $COLUMN_ID, IFNULL($COLUMN_NAME_PL, $COLUMN_NAME) FROM $TABLE_ITEM_TYPES WHERE $COLUMN_CODE = ?"
            val cursor = db.rawQuery(query, arrayOf(code))
            val entity = ItemTypeEntity()
            entity.code = code
            if (cursor.moveToFirst()) {
                entity.id = cursor.getInt(0)
                entity.name = cursor.getString(1)
                cursor.close()
            }
            return entity
        }
    }
}