package com.lockminds.tayari.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.github.ybq.android.spinkit.SpinKitView
import com.lockminds.tayari.adapter.RestaurantAdapter
import com.lockminds.tayari.constants.APIURLs
import com.lockminds.tayari.constants.Constants
import com.lockminds.tayari.databinding.FragmentRestaurantTabsBinding
import com.lockminds.tayari.model.Restaurant
import com.lockminds.tayari.utils.ItemAnimation

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val DATE_KEY = "data_key"


/**
 * A simple [Fragment] subclass.
 * Use the [RestaurantTabsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RestaurantTabsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var data_key: String? = null
    private var spinKitView: SpinKitView? = null
    private var restaurantAdapter: RestaurantAdapter? = null
    private val animation_type: Int = ItemAnimation.FADE_IN

    private var _binding: FragmentRestaurantTabsBinding? = null
    // This property is only valid between onCreateView and  onDestroyView.
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data_key = it.getString(DATE_KEY)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRestaurantTabsBinding.inflate(inflater, container, false)
        val view = binding?.root

        binding?.recyclerView?.layoutManager = LinearLayoutManager(activity)
        binding?.recyclerView?.setHasFixedSize(true)
        setAdapter()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setAdapter() {
        spinKitView?.isVisible = true
        spinKitView?.bringToFront()
        val preference = activity?.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
        if (preference != null) {
            AndroidNetworking.get(APIURLs.BASE_URL + "restaurants/get_all")
                .setTag("lots")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + preference.getString(Constants.LOGIN_TOKEN, "false"))
                .setPriority(Priority.HIGH)
                .setPriority(Priority.LOW)
                .build()
                .getAsObjectList(Restaurant::class.java, object : ParsedRequestListener<List<Restaurant>> {
                    override fun onResponse(business: List<Restaurant>) {
                        val items: List<Restaurant> = business
                        if (items.isNotEmpty()) {
                            //set data and list adapter
                            restaurantAdapter = RestaurantAdapter(activity, items, animation_type)
                            binding?.recyclerView?.adapter = restaurantAdapter

                        }
                    }

                    override fun onError(anError: ANError) {}
                })
        }
        spinKitView?.isVisible = false
    }

    companion object {

        @JvmStatic fun newInstance(param1: String, spinKitView: SpinKitView) =
                RestaurantTabsFragment().apply {
                    arguments = Bundle().apply {
                        putString(DATE_KEY, param1)
                    }
                    this.spinKitView = spinKitView
                    this.spinKitView?.isVisible =  true
                }
    }
}