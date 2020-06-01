package pl.pjt.ubi_bricks

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.JobIntentService.enqueueWork
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_inventories_list.*
import kotlinx.android.synthetic.main.activity_new_inventory.*
import kotlinx.android.synthetic.main.element_inventories_list.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pl.pjt.ubi_bricks.database.*
import java.lang.Exception

class NewInventoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_inventory)
        setSupportActionBar(newInventoryToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

    }

    fun checkInventoryCallback(view: View?) {
        disableControl()
        val setId = newInventorySetId.text.toString()
        if (setId == "") {
            Toast.makeText(applicationContext, "Set ID is incorrect!", Toast.LENGTH_SHORT).show()
            enableControl()
        } else {
            lifecycleScope.launch {
                try {
                    val exists = BrickSet(setId, applicationContext).checkSetExists()
                    if (exists) {
                        runOnUiThread {
                            enableControl()
                            Toast.makeText(
                                applicationContext,
                                "Set ID is correct",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        runOnUiThread {
                            enableControl()
                            Toast.makeText(
                                applicationContext,
                                "Set ID is incorrect!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (exception: Exception) {
                    runOnUiThread {
                        enableControl()
                        Toast.makeText(
                            applicationContext,
                            "Something went wrong, check your URL prefix",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        }
    }

    override fun finish() {
        setResult(Activity.RESULT_CANCELED)
        super.finish()
    }

    private fun close() {
        setResult(Activity.RESULT_OK)
        super.finish()
    }

    fun addInventoryCallback(view: View?) {
        disableControl()
        val setId = newInventorySetId.text.toString()
        val projectName = newInventoryName.text.toString()
        when {
            projectName == "" -> {
                Toast.makeText(
                    applicationContext,
                    "Please input a project name!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                enableControl()
            }
            setId != "" -> {
                GlobalScope.launch {
                    try {
                        downloadSet(setId, projectName)
                        runOnUiThread {
                            enableControl()
                        }
                    } catch (exception: Exception) {
                        runOnUiThread {
                            enableControl()
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong, check your URL prefix",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
            else -> {
                enableControl()
                Toast.makeText(applicationContext, "Set ID is incorrect!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private suspend fun downloadSet(setId: String, projectName: String) {
        val brickSet = BrickSet(setId, applicationContext)
        val result = brickSet.downloadInventory()
        if (result) {
            val downloadedParts = brickSet.createPartsList()
            val inventoryId = Inventory.add(projectName)
            var nextPartId = InventoryPart.getFreeKey()
            val partColorIdArray = ArrayList<Pair<Part.PartEntity?, Color.ColorEntity?>>()
            val partsNotInDb = ArrayList<BrickSet.DownloadedPart>()
            for (downloadedPart: BrickSet.DownloadedPart in downloadedParts) {
                val dbPart = InventoryPart.InventoryPartEntity()
                val element = Part.getByCode(downloadedPart.itemId)
                if (element.id == null) {
                    // Part not found in the database
                    partsNotInDb.add(downloadedPart)
                    continue
                }
                dbPart.id = nextPartId
                dbPart.inventoryId = inventoryId
                dbPart.quantityInStore = 0
                dbPart.quantityInSet = downloadedPart.quantityInSet
                val color = Color.getByLegoId(downloadedPart.colorCode)
                dbPart.colorId = color.id
                dbPart.partId = element.id
                val type = ItemType.getByCode(downloadedPart.itemType)
                dbPart.typeId = type.id
                // Extra field is not used for anything

                partColorIdArray.add(Pair(element, color))
                InventoryPart.add(dbPart)
                nextPartId++
            }
            runOnUiThread {
                if (partsNotInDb.size != 0) {
                    var partsString = ""
                    for (part: BrickSet.DownloadedPart in partsNotInDb) {
                        partsString += "Part ${part.itemId}, Color ${part.colorCode}\n"
                    }
                    AlertDialog.Builder(this)
                        .setTitle("Parts not found in the database").setMessage(partsString).show()
                }
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.set_download_ok),
                    Toast.LENGTH_SHORT
                ).show()
            }
            // After adding set data start downloading images
            // (this allows user to start using the project while images are being downloaded)
            for (pair: Pair<Part.PartEntity?, Color.ColorEntity?> in partColorIdArray) {
                if (pair.first != null && pair.second != null) {
                    LegoCode.downloadPartImageIfNotPresent(pair.first!!, pair.second!!)
                }
            }
            runOnUiThread {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.set_download_image_ok),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            runOnUiThread {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.set_download_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun disableControl() {
        checkInventoryButton.isEnabled = false
        addInventoryButton.isEnabled = false
        newInventorySetId.isEnabled = false
        newInventoryName.isEnabled = false
    }

    private fun enableControl() {
        checkInventoryButton.isEnabled = true
        addInventoryButton.isEnabled = true
        newInventorySetId.isEnabled = true
        newInventoryName.isEnabled = true
    }
}