package com.lockminds.tayari.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.lockminds.tayari.adapter.ProductsAdapter
import com.lockminds.tayari.data.Product
import com.lockminds.tayari.databinding.FragmentProductsBinding
import com.lockminds.tayari.viewModels.ProductsListViewModel
import com.lockminds.tayari.viewModels.ProductsListViewModelFactory

private const val BUSINESS_KEY = ""

class ProductsFragment : Fragment() {

    private var busines_key: String? = null
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager

    val productsListViewModel by viewModels<ProductsListViewModel>{
        ProductsListViewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            busines_key = it.getString(BUSINESS_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productsAdapter = ProductsAdapter(requireContext()) { product -> adapterOnClick(product) }

        linearLayoutManager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = productsAdapter

        productsListViewModel.refreshList()
        productsListViewModel.productsLiveData.observe(requireActivity(), {
            it?.let {
                productsAdapter.submitList(it as MutableList<Product>)
            }
        })

        fetchProducts()
    }

    private fun fetchProducts() {
        AndroidNetworking.get("https://api.tayari.co.tz/api/products")
            .addQueryParameter("business", busines_key)
            .setTag(this)
            .setPriority(Priority.LOW)
            .build()
            .getAsObjectList(Product::class.java, object : ParsedRequestListener<List<Product>> {
                override fun onResponse(products: List<Product>) {
                    val items: List<Product> = products
                    if (items.size > 0) {
                        productsListViewModel.insertProductsList(items)
                        binding.recyclerView.bringToFront()
                    }
                }

                override fun onError(anError: ANError) {
                    Log.e("KELLY",anError.errorBody + anError.message)
                }
            })
    }


    private fun adapterOnClick(product: Product) {
//        val intent = Intent(requireContext(), ProductDetailsActivity()::class.java)
//        intent.putExtra(General.PRODUCT_KEY, product.product_key)
//        startActivity(intent)
    }

    companion object {

        @JvmStatic
        fun newInstance(business_key: String) =
            ProductsFragment().apply {
                arguments = Bundle().apply {
                    putString(BUSINESS_KEY, business_key)
                }
            }
    }
}