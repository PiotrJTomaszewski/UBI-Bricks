package pl.pjt.ubi_bricks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_inventories_list.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pl.pjt.ubi_bricks.database.Database
import pl.pjt.ubi_bricks.database.Inventory
import pl.pjt.ubi_bricks.listAdapters.InventoriesListAdapter

class InventoriesListActivity : AppCompatActivity() {

    private val newProjectRequestCode = 10000
    private val inventoryPartsRequestCode = 20000
    private val settingsRequestCode = 30000

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    inner class ProjectListOnClickListener : InventoriesListAdapter.ClickListener {
        override fun onItemClick(inventoryId: Int) {
            showInventoryPartsActivity(inventoryId)
        }
    }

    private fun getProjects() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val showArchived = sharedPreferences.getBoolean("showArchived", true)
        val inventories = if (showArchived) {
            Inventory.getAll()
        } else {
            Inventory.getNonArchived()
        }
        (viewAdapter as InventoriesListAdapter).setInventories(inventories)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventories_list)
        setSupportActionBar(inventoriesListToolbar)

        newProjectButton.setOnClickListener {
            showNewInventoryActivity()
        }

        viewManager = LinearLayoutManager(this)

        viewAdapter = InventoriesListAdapter(
            null,
            ProjectListOnClickListener(),
            applicationContext
        )
        recyclerView = findViewById<RecyclerView>(R.id.inventoriesList).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        val dividerItemDecoration =
            DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        // Set preferences to default if they are missing
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getString("urlPrefix", null) == null) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("showArchived", true)
            editor.putString("urlPrefix", "http://http://fcds.cs.put.poznan.pl/MyWeb/BL")
            editor.apply()
        }
        disableControl()
        GlobalScope.launch {
            // Check if database exists
            val dbDownloader = lifecycleScope.async {
                if (!Database.getWasDBDownloaded(applicationContext)) {
                    runOnUiThread{
                        val toast = Toast.makeText(applicationContext,
                            "Downloading database. Please wait.", Toast.LENGTH_LONG)
                        toast.show()
                    }
                    Log.println(Log.DEBUG, "Network","Downloading the database")
                    Database.downloadDatabase(applicationContext)
                    Database.setWasDBDownloaded(applicationContext)
                    runOnUiThread{
                        val toast = Toast.makeText(applicationContext,
                            "Database downloaded", Toast.LENGTH_LONG)
                        toast.show()
                    }
                }
            }
            Database.initDatabase(applicationContext)
            // Wait for the database to be downloaded
            dbDownloader.await()
            getProjects()
            runOnUiThread {
                enableControl()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> showSettingsActivity()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSettingsActivity() : Boolean {
        val i = Intent(this, SettingsActivity::class.java)
        startActivityForResult(i, settingsRequestCode)
        return true
    }

    private fun showNewInventoryActivity() {
        val i = Intent(this, NewInventoryActivity::class.java)
        startActivityForResult(i, newProjectRequestCode)
    }

    private fun showInventoryPartsActivity(inventoryId: Int) {
        Inventory.setLastAccessed(inventoryId)
        val i = Intent(applicationContext, InventoryPartsActivity::class.java)
        i.putExtra("inventoryId", inventoryId)
        startActivityForResult(i, inventoryPartsRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if ((requestCode == newProjectRequestCode) && (resultCode == Activity.RESULT_OK)) {
            getProjects()
//        }
    }

    private fun disableControl() {
        newProjectButton.isEnabled = false

    }

    private fun enableControl() {
        newProjectButton.isEnabled = true
    }
}
