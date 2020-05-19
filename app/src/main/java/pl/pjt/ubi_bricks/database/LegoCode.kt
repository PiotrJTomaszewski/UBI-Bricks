package pl.pjt.ubi_bricks.database

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URL

class LegoCode {
    class LegoCodeEntity {
        var id: Int? = null
        var legoCode: Int? = null
        var image: Bitmap? = null

    }

    companion object {
        private const val COLUMN_ID = "id"
        private const val COLUMN_ITEM_ID = "ItemID"
        private const val COLUMN_COLOR_ID = "ColorID"
        private const val COLUMN_LEGO_CODE = "Code"
        private const val COLUMN_IMAGE = "Image"

        private const val TABLE_CODES = "Codes"

        suspend fun getByItemAndColorIdWithUpdateImage(itemId: Int, colorId: Int): LegoCodeEntity {
            val db = Database.instance!!.readableDatabase
            val query = "SELECT $COLUMN_ID, $COLUMN_LEGO_CODE, $COLUMN_IMAGE FROM $TABLE_CODES WHERE $COLUMN_ITEM_ID = ? AND $COLUMN_COLOR_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(itemId.toString(), colorId.toString()))
            val entity = LegoCodeEntity()
            if (cursor.moveToFirst()) {
                entity.id = cursor.getInt(0)
                entity.legoCode = cursor.getInt(1)
                val blob = cursor.getBlob(2)
                if (blob == null) {
                    val image = downloadPartImage(entity.legoCode!!)
                    val id = entity.id!!
                    if (image != null) {
                        entity.image = image
                        // Save image in the background
                        GlobalScope.launch {
                            setPartImage(id, image)
                        }
                    }
                } else {
                    entity.image = BitmapFactory.decodeByteArray(blob, 0, blob.size)
                }
                cursor.close()
            }
            return entity
        }

        private suspend fun downloadPartImage(legoCode: Int): Bitmap? {
            val url = "https://www.lego.com/service/bricks/5/2/$legoCode"
            // TODO: Add other urls
            var image: Bitmap? = null
            withContext(Dispatchers.IO) {
                val connection = URL(url).openConnection()
                connection.doInput = true
                connection.connect()
                val inputStream = connection.getInputStream()
                image = BitmapFactory.decodeStream(inputStream)
            }
            return image
        }

        private fun setPartImage(id: Int, image: Bitmap) {
            val db = Database.instance!!.writableDatabase
            val values = ContentValues()
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val blob = stream.toByteArray()
            values.put(COLUMN_IMAGE, blob)
            db.update(TABLE_CODES, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        }
    }
}