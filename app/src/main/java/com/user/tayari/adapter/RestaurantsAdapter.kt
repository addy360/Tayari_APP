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
import com.user.tayari.model.Restaurant
import com.user.tayari.utils.ItemAnimation

class RestaurantsAdapter(context: Context, private val onClick: (Restaurant) -> Unit) :
    ListAdapter<Restaurant, RestaurantsAdapter.RestaurantsViewHolder>(RestaurantsDiffCallback) {
    private val context: Context? = context
    /* ViewHolder for Restaurant, takes in the inflated view and the onClick behavior. */
    class RestaurantsViewHolder(itemView: View, val onClick: (Restaurant) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val businessTitle: TextView = itemView.findViewById(R.id.business_name)
        private val businessImage: ImageView = itemView.findViewById(R.id.business_logo)
        private val businessLocation: TextView = itemView.findViewById(R.id.business_location)
        private val businessAddress: TextView = itemView.findViewById(R.id.business_address)
        private var currentRestaurant: Restaurant? = null
        private val tools = Tools()
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
            businessAddress.text = business.address
            businessLocation.text = ""
            tools.displayImageBusiness(context, businessImage, business.logo.toString())
        }


    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurants, parent, false)
        return RestaurantsViewHolder(view, onClick)
    }


    override fun onBindViewHolder(holder: RestaurantsViewHolder, position: Int) {
        val business = getItem(position)
        holder.bind(business, context)
       // setAnimation(holder.itemView, position)
    }

    private var lastPosition = -1
    private val on_attach = true

    private fun setAnimation(view: View, position: Int) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, if (on_attach) position else -1, ItemAnimation.FADE_IN)
            lastPosition = position
        }
    }
}

object RestaurantsDiffCallback : DiffUtil.ItemCallback<Restaurant>() {
    override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem.id == newItem.id
    }
}