package pl.pjt.ubi_bricks.database

class Part {
    class PartEntity {
        var id: Int? = null
        var typeId: Int? = null
        var code: String? = null
        var name: String? = null

        var typeEntity: ItemType.ItemTypeEntity? = null
    }

    companion object {
        private const val COLUMN_ID = "id"
        private  const val COLUMN_TYPE_ID = "TypeID"
        private const val COLUMN_CODE = "Code"
        private const val COLUMN_NAME = "Name"
        private const val COLUMN_NAME_PL = "NamePL"

        private const val TABLE_PARTS = "Parts"

        fun getById(id: Int): PartEntity {
            val db = Database.instance!!.readableDatabase
            val query = "SELECT $COLUMN_CODE, $COLUMN_TYPE_ID, IFNULL($COLUMN_NAME_PL, $COLUMN_NAME) FROM $TABLE_PARTS WHERE $COLUMN_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(id.toString()))
            val entity = PartEntity()
            entity.id = id
            if (cursor.moveToFirst()) {
                entity.code = cursor.getString(0)
                entity.typeId = cursor.getInt(1)
                entity.name = cursor.getString(2)
                cursor.close()
            }
            return entity
        }

        fun getByCode(code: String): PartEntity {
            val db = Database.instance!!.readableDatabase
            val query = "SELECT $COLUMN_ID, $COLUMN_TYPE_ID, IFNULL($COLUMN_NAME_PL, $COLUMN_NAME) FROM $TABLE_PARTS WHERE $COLUMN_CODE = ?"
            val cursor = db.rawQuery(query, arrayOf(code))
            val entity = PartEntity()
            entity.code = code
            if (cursor.moveToFirst()) {
                entity.id = cursor.getInt(0)
                entity.typeId = cursor.getInt(1)
                entity.name = cursor.getString(2)
                cursor.close()
            }
            return entity
        }
    }

}