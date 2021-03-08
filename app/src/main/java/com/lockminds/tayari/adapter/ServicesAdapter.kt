package com.lockminds.tayari.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lockminds.tayari.R
import com.lockminds.tayari.Tools
import com.lockminds.tayari.data.Service

class ServicesAdapter(context: Context,private val onClick: (Service) -> Unit) :
    ListAdapter<Service, ServicesAdapter.ServicesViewHolder>(FlowerDiffCallback) {
    private val context: Context? = context

    class ServicesViewHolder(itemView: View, val onClick: (Service) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val serviceImage: ImageView = itemView.findViewById(R.id.service_image)
        private val serviceName: TextView = itemView.findViewById(R.id.service_name)
        private val serviceCategoryName: TextView = itemView.findViewById(R.id.service_category_name)
        private val serviceDetails: TextView = itemView.findViewById(R.id.service_details)
        private var currentService: Service? = null

        init {
            itemView.setOnClickListener {
                currentService?.let {
                    onClick(it)
                }
            }
        }

        fun bind(service: Service,context: Context?) {
            currentService = service
            serviceCategoryName.text = service.service_amount
            serviceDetails.text = Tools.fromHtml(service.service_details)
            serviceName.text = service.service_name
            Tools.displayImageServices(context, serviceImage, service.service_image)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_services, parent, false)
        return ServicesViewHolder(view, onClick)
    }


    override fun onBindViewHolder(holder: ServicesViewHolder, position: Int) {
        val service = getItem(position)
        holder.bind(service, context)
    }
}

object FlowerDiffCallback : DiffUtil.ItemCallback<Service>() {

    override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem.service_key == newItem.service_key
    }

}
