package com.lockminds.tayari.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lockminds.tayari.R
import com.lockminds.tayari.Tools
import com.lockminds.tayari.model.Menu

class OffersAdapter(context: Context,private val onClick: (Menu) -> Unit) :
    ListAdapter<Menu, OffersAdapter.OffersViewHolder>(OffersDiffCallback) {
    private val context: Context? = context

    class OffersViewHolder(itemView: View, val onClick: (Menu) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.image)
        private var currentOffer: Menu? = null
        private val tools = Tools()
        init {
            itemView.setOnClickListener {
                currentOffer?.let {
                    onClick(it)
                }
            }
        }

        /* Bind business name and image. */
        fun bind(offer: Menu, context: Context?) {
            currentOffer = offer
            tools.displayImageBusiness(context, image, offer.sale_image.toString())
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

object OffersDiffCallback : DiffUtil.ItemCallback<Menu>() {
    override fun areItemsTheSame(oldItem: Menu, newItem: Menu): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Menu, newItem: Menu): Boolean {
        return oldItem.id == newItem.id
    }
}
