package pl.pjt.ubi_bricks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_inventories_list.*
import kotlinx.android.synthetic.main.activity_inventory.*
import kotlinx.coroutines.launch
import pl.pjt.ubi_bricks.database.*

class InventoryPartsActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)
        setSupportActionBar(inventoryToolbar)


        val extras = intent.extras ?: return
        val inventoryId = extras.getInt("inventoryId")

        val parts = InventoryPart.getByInventoryId(inventoryId)
        for (i in 0 until parts.size) {
            if (parts[i].typeId != null) {
                parts[i].typeEntity = ItemType.getById(parts[i].typeId!!)
            }
            if (parts[i].partId != null) {
                parts[i].partEntity = Part.getById(parts[i].partId!!)
            }
            if (parts[i].colorEntity != null) {
                parts[i].colorEntity = Color.getById(parts[i].colorId!!)
            }
        }

        viewManager = LinearLayoutManager(this)
        viewAdapter = InventoryAdapter(parts, applicationContext)
        recyclerView = findViewById<RecyclerView>(R.id.inventoryList).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        val dividerItemDecoration =
            DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        lifecycleScope.launch {
            // Download images in the background
            for (i in 0 until parts.size) {
                if (parts[i].partId != null && parts[i].colorId != null) {
                    parts[i].legoCodeEntity = LegoCode.getByItemAndColorIdWithUpdateImage(parts[i].partId!!, parts[i].colorId!!)
                }
                runOnUiThread {
                    (viewAdapter as InventoryAdapter).setParts(parts)
                }
            }
        }


    }
}