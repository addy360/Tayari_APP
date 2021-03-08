package com.lockminds.tayari.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lockminds.tayari.R
import com.lockminds.tayari.Tools
import com.lockminds.tayari.data.Business
import com.lockminds.tayari.data.ItemCategory

class ItemsCategoriesAdapter(context: Context,private val onClick: (ItemCategory) -> Unit) :
        ListAdapter<ItemCategory, ItemsCategoriesAdapter.ItemsCategoriesViewHolder>(ItemsCategoriesDiffCallback) {
    private val context: Context? = context
    /* ViewHolder for Business, takes in the inflated view and the onClick behavior. */
    class ItemsCategoriesViewHolder(itemView: View, val onClick: (ItemCategory) -> Unit) :
            RecyclerView.ViewHolder(itemView) {
        private val businessTitle: TextView = itemView.findViewById(R.id.business_name)
        private val businessCategory: TextView = itemView.findViewById(R.id.business_location)
        private val businessDate: TextView = itemView.findViewById(R.id.business_work_status)
        private val businessImage: ImageView = itemView.findViewById(R.id.business_banner)
        private var currentBusiness: ItemCategory? = null

        init {
            itemView.setOnClickListener {
                currentBusiness?.let {
                    onClick(it)
                }
            }
        }

        /* Bind business name and image. */
        fun bind(business: ItemCategory, context: Context?) {
            currentBusiness = business
            businessTitle.text = business.business_name
            businessCategory.text = business.business_location
            businessDate.text = business.business_work_status
            Tools.displayImageBusiness(context, businessImage, business.business_banner)
        }


    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsCategoriesViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_business, parent, false)
        return ItemsCategoriesViewHolder(view, onClick)
    }


    override fun onBindViewHolder(holder: ItemsCategoriesViewHolder, position: Int) {
        val business = getItem(position)
        holder.bind(business,context)
    }
}

object ItemsCategoriesDiffCallback : DiffUtil.ItemCallback<ItemCategory>() {
    override fun areItemsTheSame(oldItem: ItemCategory, newItem: ItemCategory): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ItemCategory, newItem: ItemCategory): Boolean {
        return oldItem.business_key == newItem.business_key
    }
}