package pl.pjt.ubi_bricks

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_inventory.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.pjt.ubi_bricks.database.*
import pl.pjt.ubi_bricks.listAdapters.InventoryAdapter
import java.lang.Exception

private const val XML_PATH_CHOOSE_INTENT = 14150

class InventoryPartsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var parts: ArrayList<InventoryPart.InventoryPartEntity>

    inner class PlusMinusButtonOnClickListener : InventoryAdapter.ButtonClickListener {
        override fun onPlusButtonClick(position: Int) {
            val partId = parts[position].id
            if (partId != null) {
                val result = InventoryPart.incrementInStore(partId)
                if (result) {
                    parts[position].quantityInStore = parts[position].quantityInStore?.plus(1)
                    (viewAdapter as InventoryAdapter).setParts(parts)
                    viewAdapter.notifyDataSetChanged()
                }
            }

        }

        override fun onMinusButtonClick(position: Int) {
            val partId = parts[position].id
            if (partId != null) {
                val result = InventoryPart.decrementInStore(partId)
                if (result) {
                    parts[position].quantityInStore = parts[position].quantityInStore?.minus(1)
                    (viewAdapter as InventoryAdapter).setParts(parts)
                    viewAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)
        setSupportActionBar(inventoryToolbar)


        val extras = intent.extras ?: return
        val inventoryId = extras.getInt("inventoryId")

        parts = InventoryPart.getByInventoryId(inventoryId)
        for (i in 0 until parts.size) {
            if (parts[i].typeId != null) {
                parts[i].typeEntity = ItemType.getById(parts[i].typeId!!)
            }
            if (parts[i].partId != null) {
                parts[i].partEntity = Part.getById(parts[i].partId!!)
            }
            if (parts[i].colorId != null) {
                parts[i].colorEntity = Color.getById(parts[i].colorId!!)
            }
            if (parts[i].partId != null) {
                parts[i].legoCodeEntity =
                    LegoCode.getByItemAndColorId(parts[i].partId!!, parts[i].colorId)
            }
        }

        viewManager = LinearLayoutManager(this)
        viewAdapter = InventoryAdapter(
            parts,
            applicationContext,
            PlusMinusButtonOnClickListener()
        )
        recyclerView = findViewById<RecyclerView>(R.id.inventoryList).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        val dividerItemDecoration =
            DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    fun saveToXml(view: View) {
        val intent = Intent().setAction(Intent.ACTION_CREATE_DOCUMENT).setType("text/xml")
        startActivityForResult(Intent.createChooser(intent, "Select a path to save"), XML_PATH_CHOOSE_INTENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == XML_PATH_CHOOSE_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                val selectedFile: Uri? = data!!.data
                if (selectedFile != null) {
                        // Clone the parts list to make sure nothing will change while creating xml
                        val partsCopy = parts.toArray()
                        GlobalScope.launch {
                            try {
                                BrickListExporter.writeXml(partsCopy, selectedFile, applicationContext)
                                runOnUiThread {
                                    Toast.makeText(applicationContext, "Inventory exported", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Log.println(Log.ERROR, "Exception", e.toString())
                                runOnUiThread {
                                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                }
            } else {
                Toast.makeText(applicationContext, "Export cancelled", Toast.LENGTH_LONG).show()
            }
        }
    }
}