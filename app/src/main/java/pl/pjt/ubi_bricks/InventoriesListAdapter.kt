package pl.pjt.ubi_bricks

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class InventoriesListAdapter (
    private var inventories: ArrayList<Inventory>?,
    private val onItemClickListener: ClickListener
): RecyclerView.Adapter<InventoriesListAdapter.ViewHolder>() {
    class InventoryDiffCallback(
        private val oldList: ArrayList<Inventory>?,
        private val newList: ArrayList<Inventory>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            if (oldList == null)
                return 0
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            TODO("Implement")

            return false // TODO
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            TODO("Implement")
            return false // TODO
        }
    }

    class ViewHolder (private val nameView: TextView): RecyclerView.ViewHolder(nameView) {

    }

    interface ClickListener {
        fun onItemClick(inventoryId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val invName = LayoutInflater.from(parent.context).inflate(
            R.layout.element_inventories_list, parent, false
        ) as TextView
        return ViewHolder(invName)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        if (inventories == null)
            return 0
        return inventories!!.size
    }

    fun setInventories(newInventories: ArrayList<Inventory>) {
        TODO("Implement")
    }
}