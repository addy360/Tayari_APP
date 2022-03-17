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
import com.user.tayari.Tools
import com.user.tayari.model.Cousin
import com.user.tayari.utils.ItemAnimation
import user.tayari.R

class CousinsAdapter(context: Context, private val onClick: (Cousin) -> Unit) :
    ListAdapter<Cousin, CousinsAdapter.CousinsViewHolder>(CousinsDiffCallback) {
    private val context: Context? = context
    /* ViewHolder for Cousin, takes in the inflated view and the onClick behavior. */
    class CousinsViewHolder(itemView: View, val onClick: (Cousin) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.name)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private var currentCousin: Cousin? = null
        private val tools = Tools()
        init {
            itemView.setOnClickListener {
                currentCousin?.let {
                    onClick(it)
                }
            }
        }

        /* Bind cousin name and image. */
        fun bind(cousin: Cousin, context: Context?) {
            currentCousin = cousin
            name.text = cousin.name
            tools.displayImageBusiness(context, image, cousin.image.toString())
        }


    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CousinsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cousins, parent, false)
        return CousinsViewHolder(view, onClick)
    }


    override fun onBindViewHolder(holder: CousinsViewHolder, position: Int) {
        val cousin = getItem(position)
        holder.bind(cousin, context)
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

object CousinsDiffCallback : DiffUtil.ItemCallback<Cousin>() {
    override fun areItemsTheSame(oldItem: Cousin, newItem: Cousin): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Cousin, newItem: Cousin): Boolean {
        return oldItem.id == newItem.id
    }
}