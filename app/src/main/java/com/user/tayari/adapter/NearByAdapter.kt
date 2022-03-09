package com.user.tayari.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.user.tayari.R
import com.user.tayari.Tools
import com.user.tayari.model.RestaurantNear

class NearByAdapter(context: Context,private val onClick: (RestaurantNear) -> Unit) :
    ListAdapter<RestaurantNear, NearByAdapter.NearByViewHolder>(NearByDiffCallback) {
    private val context: Context? = context
    /* ViewHolder for RestaurantNear, takes in the inflated view and the onClick behavior. */
    class NearByViewHolder(itemView: View, val onClick: (RestaurantNear) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val businessTitle: TextView = itemView.findViewById(R.id.business_name)
        private val businessImage: ImageView = itemView.findViewById(R.id.business_logo)
        private val businessLocation: TextView = itemView.findViewById(R.id.business_location)
        private val businessAddress: TextView = itemView.findViewById(R.id.business_address)
        private var currentRestaurantNear: RestaurantNear? = null
        private val tools= Tools()
        init {
            itemView.setOnClickListener {
                currentRestaurantNear?.let {
                    onClick(it)
                }
            }
        }

        /* Bind business name and image. */
        fun bind(business: RestaurantNear, context: Context?) {
            currentRestaurantNear = business
            businessTitle.text = business.name
            businessAddress.text = business.address
            businessLocation.text = ""
            tools.displayImageBusiness(context, businessImage, business.logo.toString())
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

object NearByDiffCallback : DiffUtil.ItemCallback<RestaurantNear>() {
    override fun areItemsTheSame(oldItem: RestaurantNear, newItem: RestaurantNear): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: RestaurantNear, newItem: RestaurantNear): Boolean {
        return oldItem.id == newItem.id
    }
}