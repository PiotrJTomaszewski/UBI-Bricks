package pl.pjt.ubi_bricks

import java.time.LocalDate

class Inventory (
    val id: Int,
    val name: String,
    var active: Boolean,
    var lastAccessed: LocalDate
) {

}