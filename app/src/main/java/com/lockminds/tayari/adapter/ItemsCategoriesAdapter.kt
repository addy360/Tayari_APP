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
import com.lockminds.tayari.model.Restaurant

class ItemsCategoriesAdapter(context: Context,private val onClick: (Restaurant) -> Unit) :
        ListAdapter<Restaurant, ItemsCategoriesAdapter.ItemsCategoriesViewHolder>(ItemsCategoriesDiffCallback) {
    private val context: Context? = context
    /* ViewHolder for Restaurant, takes in the inflated view and the onClick behavior. */
    class ItemsCategoriesViewHolder(itemView: View, val onClick: (Restaurant) -> Unit) :
            RecyclerView.ViewHolder(itemView) {
        private val businessTitle: TextView = itemView.findViewById(R.id.business_name)
        private val businessImage: ImageView = itemView.findViewById(R.id.business_banner)
        private var currentRestaurant: Restaurant? = null

        private val tools=  Tools()
        init {
            itemView.setOnClickListener {
                currentRestaurant?.let {
                    onClick(it)
                }
            }
        }

        /* Bind business name and image. */
        fun bind(business: Restaurant, context: Context?) {
            currentRestaurant = business
            businessTitle.text = business.name
            tools.displayImageBusiness(context, businessImage, business.banner.toString())
        }


    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsCategoriesViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_categories_home, parent, false)
        return ItemsCategoriesViewHolder(view, onClick)
    }


    override fun onBindViewHolder(holder: ItemsCategoriesViewHolder, position: Int) {
        val business = getItem(position)
        holder.bind(business,context)
    }
}

object ItemsCategoriesDiffCallback : DiffUtil.ItemCallback<Restaurant>() {
    override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem.id == newItem.id
    }
}