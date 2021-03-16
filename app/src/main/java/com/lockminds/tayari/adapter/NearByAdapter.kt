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

class NearByAdapter(context: Context,private val onClick: (Restaurant) -> Unit) :
    ListAdapter<Restaurant, NearByAdapter.NearByViewHolder>(NearByDiffCallback) {
    private val context: Context? = context
    /* ViewHolder for Restaurant, takes in the inflated view and the onClick behavior. */
    class NearByViewHolder(itemView: View, val onClick: (Restaurant) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val businessTitle: TextView = itemView.findViewById(R.id.business_name)
        private val businessImage: ImageView = itemView.findViewById(R.id.business_logo)
        private val businessLocation: TextView = itemView.findViewById(R.id.business_location)
        private val businessAddress: TextView = itemView.findViewById(R.id.business_address)
        private var currentRestaurant: Restaurant? = null

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
            businessTitle.text = business.business_name
            businessAddress.text = business.business_address
            businessLocation.text = "2.5 km "+business.business_location
            Tools.displayImageBusiness(context, businessImage, business.business_banner)
        }


    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearByViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurants_near_by, parent, false)
        return NearByViewHolder(view, onClick)
    }


    override fun onBindViewHolder(holder: NearByViewHolder, position: Int) {
        val business = getItem(position)
        holder.bind(business,context)
    }
}

object NearByDiffCallback : DiffUtil.ItemCallback<Restaurant>() {
    override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem.business_key == newItem.business_key
    }
}