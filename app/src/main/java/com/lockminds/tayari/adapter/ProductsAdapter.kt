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
import com.lockminds.tayari.data.Product

class ProductsAdapter(context: Context,private val onClick: (Product) -> Unit) :
        ListAdapter<Product, ProductsAdapter.ProductsViewHolder>(ProductsDiffCallback) {

    private val context: Context? = context

    /* ViewHolder for News, takes in the inflated view and the onClick behavior. */
    class ProductsViewHolder(itemView: View, val onClick: (Product) -> Unit) :
            RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.product_name)
        private val productDescription: TextView = itemView.findViewById(R.id.product_description)
        private val productImage: ImageView = itemView.findViewById(R.id.product_image)
        private val defaultPrice: TextView = itemView.findViewById(R.id.default_price)
        private var currentProduct: Product? = null

        init {
            itemView.setOnClickListener {
                currentProduct?.let {
                    onClick(it)
                }
            }
        }

        /* Bind product name and image. */
        fun bind(product: Product, context: Context?) {
            currentProduct = product
            productName.text = Tools.fromHtml(product.product_name)
            defaultPrice.text = Tools.fromHtml(product.default_price )
            productDescription.text = Tools.fromHtml(product.product_description)
            Tools.displayImageProducts(context, productImage, product.product_image)
        }

    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_products, parent, false)
        return ProductsViewHolder(view, onClick)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product,context)
    }
}

object ProductsDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.product_key == newItem.product_key
    }
}