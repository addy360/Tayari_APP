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

class OffersAdapter(context: Context,private val onClick: (Restaurant) -> Unit) :
    ListAdapter<Restaurant, OffersAdapter.OffersViewHolder>(OffersDiffCallback) {
    private val context: Context? = context
    
    
    /* ViewHolder for Restaurant, takes in the inflated view and the onClick behavior. */
    class OffersViewHolder(itemView: View, val onClick: (Restaurant) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val businessImage: ImageView = itemView.findViewById(R.id.business_logo)
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
            Tools.displayImageBusiness(context, businessImage, business.business_banner)
        }


    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OffersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_offers, parent, false)
        return OffersViewHolder(view, onClick)
    }


    override fun onBindViewHolder(holder: OffersViewHolder, position: Int) {
        val business = getItem(position)
        holder.bind(business,context)
    }
}

object OffersDiffCallback : DiffUtil.ItemCallback<Restaurant>() {
    override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem.business_key == newItem.business_key
    }
}