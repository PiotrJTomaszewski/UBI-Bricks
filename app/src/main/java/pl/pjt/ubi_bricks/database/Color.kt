package pl.pjt.ubi_bricks.database

class Color {
    class ColorEntity {
        var id: Int? = null
        var legoId: Int? = null // Code column
        var name: String? = null
    }

    companion object {
        private const val COLUMN_ID = "id"
        private const val COLUMN_LEGO_ID = "Code"
        private const val COLUMN_NAME = "Name"
        private const val COLUMN_NAME_PL = "NamePL"
        private const val TABLE_COLORS = "Colors"

        fun getById(colorId: Int): ColorEntity {
            val db = Database.instance!!.readableDatabase
            val query = "SELECT $COLUMN_LEGO_ID, IFNULL($COLUMN_NAME_PL, $COLUMN_NAME) FROM $TABLE_COLORS WHERE $COLUMN_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(colorId.toString()))
            val colorEntity = ColorEntity()
            colorEntity.id = colorId
            if (cursor.moveToFirst()) {
                colorEntity.legoId = cursor.getInt(0)
                colorEntity.name = cursor.getString(1)
                cursor.close()
            }
            return colorEntity
        }

        fun getByLegoId(colorLegoId: Int): ColorEntity {
            val db = Database.instance!!.readableDatabase
            val query = "SELECT $COLUMN_ID, IFNULL($COLUMN_NAME_PL, $COLUMN_NAME) FROM $TABLE_COLORS WHERE $COLUMN_LEGO_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(colorLegoId.toString()))
            val colorEntity = ColorEntity()
            colorEntity.legoId = colorLegoId
            if (cursor.moveToFirst()) {
                colorEntity.id = cursor.getInt(0)
                colorEntity.name = cursor.getString(1)
                cursor.close()
            }
            return colorEntity
        }
    }
}