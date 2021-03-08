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

class BusinessAdapter(context: Context,private val onClick: (Business) -> Unit) :
        ListAdapter<Business, BusinessAdapter.BusinessViewHolder>(BusinessDiffCallback) {
    private val context: Context? = context
    /* ViewHolder for Business, takes in the inflated view and the onClick behavior. */
    class BusinessViewHolder(itemView: View, val onClick: (Business) -> Unit) :
            RecyclerView.ViewHolder(itemView) {
        private val businessTitle: TextView = itemView.findViewById(R.id.business_name)
        private val businessCategory: TextView = itemView.findViewById(R.id.business_location)
        private val businessDate: TextView = itemView.findViewById(R.id.business_work_status)
        private val businessImage: ImageView = itemView.findViewById(R.id.business_banner)
        private var currentBusiness: Business? = null

        init {
            itemView.setOnClickListener {
                currentBusiness?.let {
                    onClick(it)
                }
            }
        }

        /* Bind business name and image. */
        fun bind(business: Business, context: Context?) {
            currentBusiness = business
            businessTitle.text = business.business_name
            businessCategory.text = business.business_location
            businessDate.text = business.business_work_status
            Tools.displayImageBusiness(context, businessImage, business.business_banner)
        }


    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_business, parent, false)
        return BusinessViewHolder(view, onClick)
    }


    override fun onBindViewHolder(holder: BusinessViewHolder, position: Int) {
        val business = getItem(position)
        holder.bind(business,context)
    }
}

object BusinessDiffCallback : DiffUtil.ItemCallback<Business>() {
    override fun areItemsTheSame(oldItem: Business, newItem: Business): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Business, newItem: Business): Boolean {
        return oldItem.business_key == newItem.business_key
    }
}