package pl.pjt.ubi_bricks.listAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.element_inventory.view.*
import pl.pjt.ubi_bricks.R
import pl.pjt.ubi_bricks.database.InventoryPart

class InventoryAdapter(
    private var parts: ArrayList<InventoryPart.InventoryPartEntity>,
    private val context: Context,
    private val buttonClickCallback: ButtonClickListener

) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {
    class PartDiffCallback(
        private val oldList: ArrayList<InventoryPart.InventoryPartEntity>,
        private val newList: ArrayList<InventoryPart.InventoryPartEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return (oldItem.quantityInStore == newItem.quantityInStore)
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return (oldList[oldItemPosition].partId == newList[newItemPosition].partId)
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }

    interface ButtonClickListener {
        fun onPlusButtonClick(position: Int)
        fun onMinusButtonClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.element_inventory, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val part = parts[position]
        if (part.legoCodeEntity != null && part.legoCodeEntity!!.image != null) {
            holder.view.partImage.setImageBitmap(part.legoCodeEntity!!.image)
        } else {
            holder.view.partImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_image_black_48dp))
        }
        val name = if (part.partEntity != null && part.partEntity!!.name != null) {
            part.partEntity!!.name
        } else {
            "Unknown"
        }
        holder.view.partTextType.text = name
        val color = if (part.colorEntity != null && part.colorEntity!!.name != null) {
            part.colorEntity!!.name
        } else {
            "Unknown"
        }
        val designIdCode =
            if (part.legoCodeEntity != null && part.legoCodeEntity!!.legoCode != null) {
                part.legoCodeEntity!!.legoCode
            } else {
                "Unknown"
            }
        val colorAndType = "$color [$designIdCode]"
        holder.view.partTextCodeColor.text = colorAndType
        val quantityText = "${part.quantityInStore} of ${part.quantityInSet}"
        holder.view.partTextQuantity.text = quantityText
        holder.view.partMinusButton.setOnClickListener {
            buttonClickCallback.onMinusButtonClick(position)
        }
        holder.view.partPlusButton.setOnClickListener {
            buttonClickCallback.onPlusButtonClick(position)
        }
        if (part.quantityInStore == part.quantityInSet) {
            holder.view.setBackgroundColor(context.resources.getColor(R.color.partFoundBackground))
        } else {
            holder.view.setBackgroundColor(context.resources.getColor(R.color.defaultBackground))
        }
    }

    override fun getItemCount(): Int {
        return parts.size
    }

    fun setParts(newParts: ArrayList<InventoryPart.InventoryPartEntity>) {
        val diffCallback =
            PartDiffCallback(
                parts,
                newParts
            )
        val result = DiffUtil.calculateDiff(diffCallback)
        parts = newParts
        result.dispatchUpdatesTo(this)
    }
}