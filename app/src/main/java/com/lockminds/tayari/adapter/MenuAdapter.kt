package com.lockminds.tayari.adapter
import android.annotation.SuppressLint
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
import com.lockminds.tayari.datasource.myMoney
import com.lockminds.tayari.model.Menu
import com.lockminds.tayari.utils.ItemAnimation
import java.text.NumberFormat

class MenuAdapter(context: Context, private val onClick: (Menu) -> Unit) :
    ListAdapter<Menu, MenuAdapter.MenuViewHolder>(MenuDiffCallback) {
    private val context: Context? = context
    /* ViewHolder for Menu, takes in the inflated view and the onClick behavior. */
    class MenuViewHolder(itemView: View, val onClick: (Menu) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.name)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private var currentMenu: Menu? = null
        private val tools= Tools()
        init {
            itemView.setOnClickListener {
                currentMenu?.let {
                    onClick(it)
                }
            }
        }

        /* Bind menu name and image. */

        @SuppressLint("SetTextI18n")
        fun bind(menu: Menu, context: Context?) {
            currentMenu = menu
            name.text = menu.name
            price.text = myMoney(menu.price.toString())+menu.currency
            tools.displayImageBusiness(context, image, menu.image.toString())
        }


    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menus, parent, false)
        return MenuViewHolder(view, onClick)
    }


    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = getItem(position)
        holder.bind(menu, context)
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

object MenuDiffCallback : DiffUtil.ItemCallback<Menu>() {
    override fun areItemsTheSame(oldItem: Menu, newItem: Menu): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Menu, newItem: Menu): Boolean {
        return oldItem.id == newItem.id
    }
}