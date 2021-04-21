package com.lockminds.tayari.adapter
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lockminds.tayari.App
import com.lockminds.tayari.MenuActivity
import com.lockminds.tayari.MenuActivity.Companion.createMenuIntent
import com.lockminds.tayari.R
import com.lockminds.tayari.Tools
import com.lockminds.tayari.model.CartMenu
import com.lockminds.tayari.model.Menu
import com.lockminds.tayari.utils.ItemAnimation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartMenuAdapter(context: Context) :
    ListAdapter<CartMenu, CartMenuAdapter.CartMenuViewHolder>(CartMenuDiffCallback) {
    private val context: Context? = context

    class CartMenuViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val vh: View = itemView.findViewById(R.id.lyt_parent)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val qty: TextView = itemView.findViewById(R.id.qty)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val total: TextView = itemView.findViewById(R.id.total)
        private val increase: ImageButton = itemView.findViewById(R.id.cart_increase)
        private val remove: ImageButton = itemView.findViewById(R.id.item_remove)
        private val decrease: ImageButton = itemView.findViewById(R.id.cart_decrease)
        private var currentCartMenu: CartMenu? = null

        fun bind(cart: CartMenu, context: Context?) {
            currentCartMenu = cart
            name.text = cart.name
            qty.text = cart.qty
            total.text = cart.total
            price.text = cart.price
            Tools.displayImageBusiness(context, image, cart.image)

            vh.setOnClickListener {
                currentCartMenu?.let {
                    val menu = Menu()
                    menu.id = currentCartMenu!!.id
                    menu.name = currentCartMenu!!.name
                    menu.image = currentCartMenu!!.image
                    menu.qty = currentCartMenu!!.qty
                    menu.price = currentCartMenu!!.price
                    menu.currency = currentCartMenu!!.currency
                    menu.total = currentCartMenu!!.total
                    menu.restaurant_banner = currentCartMenu!!.restaurant_banner
                    menu.restaurant_logo = currentCartMenu!!.restaurant_logo
                    menu.restaurant_name = currentCartMenu!!.restaurant_name
                    menu.cousin_name = currentCartMenu!!.cousin_name
                    menu.description = currentCartMenu!!.description
                    createMenuIntent(context!!, menu)
                }
                Log.e("KELLY","you clicked me")
            }

            increase.setOnClickListener {
                currentCartMenu?.let {
                    val menuItem = CartMenu()
                    menuItem.type = "menu"
                    menuItem.id = it.id
                    GlobalScope.launch {
                        (context?.applicationContext as App).repository.addCartMenu(menuItem)
                    }
                }
            }

            decrease.setOnClickListener {
                currentCartMenu?.let {
                    val menuItem = CartMenu()
                    menuItem.type = "menu"
                    menuItem.id = it.id
                    GlobalScope.launch {
                        (context?.applicationContext as App).repository.lowerCartMenu(menuItem)
                    }
                }
            }

            remove.setOnClickListener {
                currentCartMenu?.let {
                    val menuItem = CartMenu()
                    menuItem.type = "menu"
                    menuItem.id = it.id
                    GlobalScope.launch {
                        (context?.applicationContext as App).repository.removeCartMenu(menuItem)
                    }
                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartMenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartMenuViewHolder(view)
    }


    override fun onBindViewHolder(holder: CartMenuViewHolder, position: Int) {
        val business = getItem(position)
        holder.bind(business, context)
        setAnimation(holder.itemView, position)
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

object CartMenuDiffCallback : DiffUtil.ItemCallback<CartMenu>() {
    override fun areItemsTheSame(oldItem: CartMenu, newItem: CartMenu): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CartMenu, newItem: CartMenu): Boolean {
        return oldItem.id == newItem.id
    }
}