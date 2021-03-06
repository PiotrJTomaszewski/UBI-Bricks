package pl.pjt.ubi_bricks.listAdapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import pl.pjt.ubi_bricks.R
import pl.pjt.ubi_bricks.database.Inventory

class InventoriesListAdapter (
    private var inventories: ArrayList<Inventory.InventoryEntity>?,
    private val onItemClickListener: ClickListener,
    private val context: Context
): RecyclerView.Adapter<InventoriesListAdapter.ViewHolder>() {
    class InventoryDiffCallback(
        private val oldList: ArrayList<Inventory.InventoryEntity>?,
        private val newList: ArrayList<Inventory.InventoryEntity>
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
            if (oldList == null) {
                return false
            }
            return (oldList[oldItemPosition].active == newList[newItemPosition].active
                    && oldList[oldItemPosition].lastAccessed == newList[newItemPosition].lastAccessed)
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return false // A workaround for broken callbacks
        }
    }

    class ViewHolder (val nameView: TextView): RecyclerView.ViewHolder(nameView) {

    }

    interface ClickListener {
        fun onItemClick(inventoryId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val invName = LayoutInflater.from(parent.context).inflate(
            R.layout.element_inventories_list, parent, false
        ) as TextView
        return ViewHolder(
            invName
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (inventories != null) {
            holder.nameView.text = inventories!![position].name
            holder.nameView.setOnClickListener {
                inventories!![position].id?.let { it1 -> onItemClickListener.onItemClick(it1) }
            }
            if (inventories!![position].active!!) {
                holder.nameView.setBackgroundColor(context.resources.getColor(R.color.defaultBackground, context.theme))
            } else {
                holder.nameView.setBackgroundColor(context.resources.getColor(R.color.inactiveBackground, context.theme))
            }
        }
    }

    override fun getItemCount(): Int {
        if (inventories == null)
            return 0
        return inventories!!.size
    }

    fun setInventories(newInventories: ArrayList<Inventory.InventoryEntity>) {
        val diffCallback =
            InventoryDiffCallback(
                inventories,
                newInventories
            )
        val result = DiffUtil.calculateDiff(diffCallback)
        inventories = newInventories
        result.dispatchUpdatesTo(this)
    }
}