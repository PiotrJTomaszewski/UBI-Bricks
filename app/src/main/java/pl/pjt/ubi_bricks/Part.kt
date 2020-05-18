package pl.pjt.ubi_bricks

import android.content.Context
import android.graphics.Bitmap

class Part(val itemId: Int, val colorId: Int) {
    var code: String = "asddsad"
    var partName: String? = null
    var colorName: String? = null
    var codeId: Int? = null
    var image: Bitmap? = null
    var designId: String = "a"
    var quantityInSet: Int = 111111
    var quantityInStore: Int = 0

    fun fillPartData(databaseHandler: DatabaseHandler) {
        partName = databaseHandler.getPartName(itemId)
        colorName = databaseHandler.getColorName(colorId)
        codeId = databaseHandler.getPartCodeId(itemId, colorId)

    }

}