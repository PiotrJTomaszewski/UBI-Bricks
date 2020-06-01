package pl.pjt.ubi_bricks.database

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.ContactsContract
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.lang.Exception
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


        fun getByItemAndColorId(itemId: Int, colorId: Int?): LegoCodeEntity {
            val db = Database.instance!!.readableDatabase
            val query =
                "SELECT $COLUMN_ID, $COLUMN_LEGO_CODE, $COLUMN_IMAGE FROM $TABLE_CODES WHERE $COLUMN_ITEM_ID = ? AND $COLUMN_COLOR_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(itemId.toString(), colorId.toString()))
            val entity = LegoCodeEntity()
            if (cursor.moveToFirst()) {
                entity.id = cursor.getInt(0)
                entity.legoCode = cursor.getInt(1)
                entity.image = null
                val blob = cursor.getBlob(2)
                if (blob != null) {
                    entity.image = BitmapFactory.decodeByteArray(blob, 0, blob.size)
                }
                cursor.close()
            } else {
                return getByItemId(itemId)
            }
            return entity
        }

        private fun getByItemId(itemId: Int): LegoCodeEntity {
            val db = Database.instance!!.readableDatabase
            val query =
                "SELECT $COLUMN_ITEM_ID, $COLUMN_LEGO_CODE, $COLUMN_IMAGE FROM $TABLE_CODES WHERE $COLUMN_ITEM_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(itemId.toString()))
            val entity = LegoCodeEntity()
            if (cursor.moveToFirst()) {
                entity.id = cursor.getInt(0)
                entity.legoCode = cursor.getInt(1)
                entity.image = null
                val blob = cursor.getBlob(2)
                if (blob != null) {
                    entity.image = BitmapFactory.decodeByteArray(blob, 0, blob.size)
                }
                cursor.close()
            }
            return entity
        }

        suspend fun downloadPartImageIfNotPresent(
            part: Part.PartEntity,
            color: Color.ColorEntity
        ) {
            val db = Database.instance!!.readableDatabase
            var query =
                "SELECT $COLUMN_ID, $COLUMN_LEGO_CODE, $COLUMN_IMAGE FROM $TABLE_CODES WHERE $COLUMN_ITEM_ID = ? AND $COLUMN_COLOR_ID = ?"
            var cursor = db.rawQuery(query, arrayOf(part.id.toString(), color.id.toString()))
            if (cursor.moveToFirst()) {
                if (cursor.getBlob(2) != null) {
                    // The image was already downloaded
                    cursor.close()
                    return
                }
                val id = cursor.getInt(0)
                val legoCode = cursor.getInt(1)
                cursor.close()
                var image: Bitmap? = try {
                    downloadPartImage("https://www.lego.com/service/bricks/5/2/$legoCode")
                } catch (exception: Exception) {
                    Log.println(Log.ERROR, "IMAGE", exception.toString())
                    null
                }
                if (image != null) {
                    updatePartImage(id, image)
                } else {
                    image = try {
                        if (part.code != null && color.legoId != null) {
                            val partCode = part.code
                            val colorCode = color.legoId
                            downloadPartImage("http://img.bricklink.com/P/$colorCode/$partCode.gif")
                        } else {
                            null
                        }
                    } catch (exception: Exception) {
                        Log.println(Log.ERROR, "IMAGE", exception.toString())
                        null
                    }
                    if (image != null) {
                        updatePartImage(id, image)
                    }
                }
            } else {
                if (part.id != null) {
                    query = "SELECT $COLUMN_IMAGE FROM $TABLE_CODES WHERE $COLUMN_ITEM_ID = ?"
                    cursor = db.rawQuery(query, arrayOf(part.id.toString()))
                    // If a brick doesn't have color variants it doesn't also have it's entry in the Codes table
                    if (!cursor.moveToFirst() || cursor.getBlob(0) == null) {
                        val image = try {
                            val partCode = part.code
                            if (partCode != null) {
                                downloadPartImage("https://www.bricklink.com/PL/$partCode.jpg")
                            } else {
                                null
                            }
                        } catch (exception: Exception) {
                            val pctmp = part.code
                            Log.println(
                                Log.DEBUG,
                                "IMAGE",
                                "https://www.bricklink.com/PL/$pctmp.jpg"
                            )
                            Log.println(Log.ERROR, "IMAGE", exception.toString())
                            null
                        }
                        if (image != null && part.id != null) {
                            addPartImage(image, part.id!!)
                        }
                    }
                    cursor.close()
                }

            }
        }

        private suspend fun downloadPartImage(url: String): Bitmap? {
            var image: Bitmap? = null
            withContext(Dispatchers.IO) {
                val connection = URL(url).openConnection()
                connection.doInput = true
                connection.connectTimeout = 300
                connection.connect()
                val inputStream = connection.getInputStream()
                image = BitmapFactory.decodeStream(inputStream)
            }
            return image
        }

        private fun updatePartImage(id: Int, image: Bitmap) {
            val db = Database.instance!!.writableDatabase
            val values = ContentValues()
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val blob = stream.toByteArray()
            values.put(COLUMN_IMAGE, blob)
            db.update(TABLE_CODES, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        }

        private fun getFreeKey(): Int {
            val db = Database.instance!!.readableDatabase
            val query = "SELECT MAX($COLUMN_ID)+1 FROM $TABLE_CODES"
            val cursor = db.rawQuery(query, null)
            return if (cursor.moveToFirst()) {
                val key = cursor.getInt(0)
                cursor.close()
                key
            } else {
                0
            }
        }

        private fun addPartImage(image: Bitmap, itemId: Int) {
            val db = Database.instance!!.writableDatabase
            val values = ContentValues()
            values.put(COLUMN_ID, getFreeKey())
            values.put(COLUMN_ITEM_ID, itemId)
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val blob = stream.toByteArray()
            values.put(COLUMN_IMAGE, blob)
            db.insert(TABLE_CODES, null, values)
        }
    }
}